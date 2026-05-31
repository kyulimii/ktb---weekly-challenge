package org.example.community.domain.auth;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RefreshToken {
    private final Long userId;
    private final String token;
    private final LocalDateTime expiresAt;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }
}
