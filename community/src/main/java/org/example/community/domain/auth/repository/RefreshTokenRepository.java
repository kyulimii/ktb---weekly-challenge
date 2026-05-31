package org.example.community.domain.auth.repository;

import java.util.Optional;
import org.example.community.domain.auth.RefreshToken;

public interface RefreshTokenRepository {
    void save(RefreshToken refreshToken);
    Optional<RefreshToken> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}