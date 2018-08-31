package dk.ledocsystem.ledoc.config.security;

import dk.ledocsystem.ledoc.exceptions.InvalidTokenException;
import dk.ledocsystem.ledoc.model.security.State;
import dk.ledocsystem.ledoc.model.security.Token;
import dk.ledocsystem.ledoc.repository.EmployeeRepository;
import dk.ledocsystem.ledoc.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

import static dk.ledocsystem.ledoc.config.security.SecurityConstants.*;

@Service
public class JwtTokenService {

    @Resource
    private TokenRepository tokenRepository;

    @Resource
    private EmployeeRepository employeeRepository;

    @Transactional
    public void saveToken(String accessToken, Long userId, LocalDateTime expDate) {
        Token token = new Token();
        token.setToken(accessToken);
        token.setUserId(userId);
        token.setExpDate(expDate);

        tokenRepository.save(token);
    }

    @Transactional
    public void invalidateToken(String token) {
        tokenRepository.deleteByToken(token);
    }

    @Transactional
    public void invalidateByUserId(Long userId) {
        tokenRepository.deleteByUserId(userId);
    }

    @Transactional
    public void invalidateByUserIds(Collection<Long> userIds) {
        tokenRepository.deleteByUserIdIn(userIds);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    String checkAndUpdateToken(String token) {
        return tokenRepository.checkAndUpdateToken(token, token);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public long countUsersOnline() {
        return tokenRepository.countUsersOnline();
    }

    @Transactional
    public void setUserOffline(String token) {
        Token t = tokenRepository.findByToken(token);
        if (t != null) {
            t.setState(State.OFFLINE);
            tokenRepository.save(t);
        }
        else {
            throw new InvalidTokenException("token.invalid", "");
        }
    }

    public void updateTokens(Long employeeId, UserAuthorities authorities) {
        List<String> tokens = tokenRepository.selectAllTokensByUserId(employeeId);
        tokens.forEach(token -> {
            Claims claims = Jwts.parser().setSigningKey(JWT_SECRET.getBytes())
                    .parseClaimsJws(token)
                    .getBody();

            String updatedToken = Jwts.builder()
                    .setSubject(claims.getSubject())
                    .claim(JWT_AUTHORITIES_CLAIM, updateAuthorities(claims, authorities))
                    .claim(CUSTOMER_CLAIM, claims.get(CUSTOMER_CLAIM, Long.class))
                    .setExpiration(claims.getExpiration())
                    .signWith(SignatureAlgorithm.HS512, JWT_SECRET.getBytes())
                    .compact();

            tokenRepository.updateToken(updatedToken, employeeId);
            employeeRepository.addAuthorities(employeeId, authorities);
        });
    }

    private String updateAuthorities(Claims claims, UserAuthorities authorities) {
        List<GrantedAuthority> grantedAuthorities =
                AuthorityUtils.commaSeparatedStringToAuthorityList(claims.get(JWT_AUTHORITIES_CLAIM, String.class));
        grantedAuthorities.add(new SimpleGrantedAuthority(authorities.name()));
        return StringUtils.join(grantedAuthorities, ',');
    }
}
