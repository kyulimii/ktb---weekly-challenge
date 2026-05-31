package org.example.community.domain.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.community.global.base.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    private Long id;
    private String email;
    private String password;
    private String nickname;
    private byte[] profileImage;

    @Builder
    private User(String email, String password, String nickname, byte[] profileImage) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

    // setter 사용을 지양하기 위한 id 할당 전용 메서드
    public void assignId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("ID는 한 번만 할당 가능합니다.");
        }
        this.id = id;
    }

    public void updateUserInfo(String nickname, byte[] image) {
        this.nickname = nickname;
        this.profileImage = image;
        this.onUpdate();
    }

    public void updatePassword(String password) {
        this.password = password;
        this.onUpdate();
    }

    public boolean matchPassword(String password) {
        return this.password.equals(password);
    }
}
