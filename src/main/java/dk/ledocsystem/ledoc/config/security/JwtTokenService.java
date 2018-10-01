package dk.ledocsystem.ledoc.config.security;

import com.google.common.collect.Collections2;
import dk.ledocsystem.ledoc.exceptions.InvalidTokenException;
import dk.ledocsystem.ledoc.model.security.State;
import dk.ledocsystem.ledoc.model.security.Token;
import dk.ledocsystem.ledoc.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static dk.ledocsystem.ledoc.config.security.SecurityConstants.*;

@Service
public class JwtTokenService {

    private static final String ROLE_PREFIX = "ROLE_";

    @Resource
    private TokenRepository tokenRepository;

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
    public void invalidateByUserIds(Iterable<Long> userIds) {
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
            throw new InvalidTokenException("token.not.found", token);
        }
    }

    public void updateTokens(Long employeeId, Set<UserAuthorities> authorities) {
        List<String> tokens = tokenRepository.selectAllTokensByUserId(employeeId);
        tokens.forEach(token -> {
            Claims claims = Jwts.parser().setSigningKey(JWT_SECRET.getBytes())
                    .parseClaimsJws(token)
                    .getBody();

            String updatedToken = Jwts.builder()
                    .setClaims(claims)
                    .claim(JWT_AUTHORITIES_CLAIM, convertAuthorities(authorities))
                    .signWith(SignatureAlgorithm.HS512, JWT_SECRET.getBytes())
                    .compact();

            tokenRepository.updateToken(updatedToken, employeeId);
        });
    }

    private Collection<String> convertAuthorities(Collection<UserAuthorities> userAuthorities) {
        return Collections2.transform(userAuthorities, (auth) -> ROLE_PREFIX + auth.toString().toLowerCase());
    }
}
