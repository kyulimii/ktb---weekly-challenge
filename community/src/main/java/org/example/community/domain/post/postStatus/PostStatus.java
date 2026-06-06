package org.example.community.domain.post.postStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.community.domain.post.Post;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostStatus {
    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "view_count")
    private int viewCount;

    @Column(name = "like_count")
    private int likeCount;

    @Column(name = "comment_count")
    private int commentCount;

    @Builder
    private PostStatus(Post post, int viewCount, int likeCount, int commentCount) {
        this.post = post;
        this.viewCount = 0;
        this.likeCount = 0;
        this.commentCount = 0;
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        this.likeCount = Math.max(0, this.likeCount - 1);
    }

    public void increaseCommentCount() {
        this.commentCount++;
    }

    public void decreaseCommentCount() {
        this.commentCount = Math.max(0, this.commentCount - 1);
    }

    public void addViewCount(int count) {
        this.viewCount += count;
    }
}
