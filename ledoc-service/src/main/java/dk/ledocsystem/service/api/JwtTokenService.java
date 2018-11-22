package dk.ledocsystem.service.api;

import com.google.common.collect.Collections2;
import dk.ledocsystem.data.model.security.UserAuthorities;
import dk.ledocsystem.service.api.exceptions.InvalidTokenException;
import dk.ledocsystem.data.model.security.State;
import dk.ledocsystem.data.model.security.Token;
import dk.ledocsystem.data.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static dk.ledocsystem.service.impl.constant.SecurityConstants.*;

@Service
@RequiredArgsConstructor
public class JwtTokenService {

    private static final String ROLE_PREFIX = "ROLE_";

    private final TokenRepository tokenRepository;

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
    public String checkAndUpdateToken(String token) {
        return tokenRepository.checkAndUpdateToken(token);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public long countUsersOnline() {
        return tokenRepository.countUsersOnline();
    }

    @Transactional
    public void setUserOffline(String tokenString) {
        Token token = tokenRepository.findByToken(tokenString)
                .orElseThrow(() -> new InvalidTokenException("token.not.found", tokenString));
        token.setState(State.OFFLINE);
        tokenRepository.save(token);
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
