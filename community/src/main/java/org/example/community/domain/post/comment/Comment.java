package org.example.community.domain.post.comment;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.community.global.base.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {
    private Long id;
    private String content;
    private Long userId;
    private Long postId;

    @Builder
    private Comment(String content, Long userId, Long postId) {
        this.content = content;
        this.userId = userId;
        this.postId = postId;
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("ID는 한 번만 할당 가능합니다.");
        }
        this.id = id;
    }

    public void update(String content) {
        this.content = content;
        onUpdate();
    }
}
