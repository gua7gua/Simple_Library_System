package edu.cupk.simple_library_system.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenService {
    @Value("${app.token.expire-seconds:3600}")
    private long expireSeconds;

    private final Map<String, TokenPayload> tokenStore = new ConcurrentHashMap<>();

    public String createToken(Integer userId) {
        String token = UUID.randomUUID().toString().replace("-", "");
        tokenStore.put(token, new TokenPayload(userId, Instant.now().getEpochSecond() + expireSeconds));
        return token;
    }

    public Integer verify(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        TokenPayload payload = tokenStore.get(token);
        if (payload == null) {
            return null;
        }
        if (payload.expireAt < Instant.now().getEpochSecond()) {
            tokenStore.remove(token);
            return null;
        }
        return payload.userId;
    }

    public void remove(String token) {
        if (token != null) {
            tokenStore.remove(token);
        }
    }

    private record TokenPayload(Integer userId, long expireAt) {
    }
}
