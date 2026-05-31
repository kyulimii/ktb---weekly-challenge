package org.example.community.global.base;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public abstract class BaseEntity {

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 생성 시 호출
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 수정 시 호출
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}