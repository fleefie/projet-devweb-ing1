package fr.cytech.projetdevwebbackend.auth;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtBlacklistService {
    @Resource(name = "redisTemplate")
    private RedisTemplate<String, String> redisTemplate;
    private final long EXPIRATION_TIME = 86400; // 1 day

    public void blacklistToken(String token) {
        redisTemplate.opsForValue().set(token, "blacklisted", EXPIRATION_TIME, TimeUnit.SECONDS);
    }

    public boolean isTokenBlacklisted(String token) {
        return redisTemplate.hasKey(token);
    }
}
