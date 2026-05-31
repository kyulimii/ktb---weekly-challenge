package org.example.community.domain.post;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.community.global.base.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {
    private Long id;
    private String title;
    private String content;
    private byte[] postImage;
    private Long userId;
    private int likeCount;
    private int viewCount;
    private int commentCount;

    @Builder
    private Post(String title, String content, byte[] postImage,Long userId) {
        this.title = title;
        this.content = content;
        this.postImage = postImage;
        this.userId = userId;
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("ID는 한 번만 할당 가능합니다.");
        }
        this.id = id;
    }

    public void update(String title, String content, byte[] image) {
        this.title = title;
        this.content = content;
        this.postImage = image;
        this.onUpdate();
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        this.likeCount--;
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void increaseCommentCount() {
        this.commentCount++;
    }

    public void decreaseCommentCount() {
        this.commentCount--;
    }
}
