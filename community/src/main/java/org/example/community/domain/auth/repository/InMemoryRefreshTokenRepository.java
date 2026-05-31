package org.example.community.domain.auth.repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.example.community.domain.auth.RefreshToken;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryRefreshTokenRepository implements RefreshTokenRepository {

    // key: userId, value: RefreshToken 객체
    private final Map<Long, RefreshToken> tokenStore = new ConcurrentHashMap<>();

    @Override
    public void save(RefreshToken refreshToken) {
        tokenStore.put(refreshToken.getUserId(), refreshToken);
    }

    @Override
    public Optional<RefreshToken> findByUserId(Long userId) {
        return Optional.ofNullable(tokenStore.get(userId));
    }

    @Override
    public void deleteByUserId(Long userId) {
        tokenStore.remove(userId);
    }
}