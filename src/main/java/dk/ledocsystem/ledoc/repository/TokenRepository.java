package dk.ledocsystem.ledoc.repository;

import dk.ledocsystem.ledoc.model.security.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    void deleteByToken(String token);

    @Query(value = "update main.access_tokens set state=\'ONLINE\', token=case when main.access_tokens.new_token isnull " +
            "or main.access_tokens.new_token=\'\' then main.access_tokens.token else " +
            "(select main.access_tokens.new_token from main.access_tokens where main.access_tokens.token=?1) end, " +
            "new_token=null where main.access_tokens.token=?1 returning main.access_tokens.token",
           nativeQuery = true)
    String checkAndUpdateToken(String token);

    @Query(value = "select count(online.user_id) from " +
            "(select main.access_tokens.user_id FROM main.access_tokens WHERE state='ONLINE' GROUP BY user_id) online",
            nativeQuery = true)
    long countUsersOnline();

    void deleteByUserId(Long userId);

    void deleteByUserIdIn(Iterable<Long> userIds);

    Optional<Token> findByToken(String token);

    @Query(value = "select main.access_tokens.token from main.access_tokens where main.access_tokens.user_id=?",
            nativeQuery = true)
    List<String> selectAllTokensByUserId(Long userId);

    @Modifying
    @Query(value = "update main.access_tokens set new_token=? where user_id=?", nativeQuery = true)
    void updateToken(String newToken, Long employeeId);
}
