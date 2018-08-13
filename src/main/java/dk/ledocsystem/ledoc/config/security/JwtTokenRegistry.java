package dk.ledocsystem.ledoc.config.security;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class JwtTokenRegistry {

    private final static Map<String, Long> REGISTRY = new ConcurrentHashMap<>();

    void saveToken(String token, Long currentNanos) {
        REGISTRY.put(token, currentNanos);
    }

    public int getActiveTokens() {
        return REGISTRY.size();
    }

    @Scheduled(fixedRate = 1000*60*5) //5 minutes
    private void checkTokens() {
        Set<Map.Entry<String, Long>> entries = REGISTRY.entrySet();
        entries.parallelStream().forEach(
                entry -> {
                    long tokenTime = entry.getValue();
                    long currentTime = System.nanoTime();
                    long diff = currentTime - tokenTime;
                    if (diff > 864e9) {
                        REGISTRY.remove(entry.getKey());
                    }
                }
        );
    }
}
