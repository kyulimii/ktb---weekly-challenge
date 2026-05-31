package org.example.community.domain.post.api.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.community.domain.post.Post;

@Getter
@AllArgsConstructor
public class PostListResponse {
    // content, postImage 제외
    private Long id;
    private String title;
    private Long authorId;
    private int likeCount;
    private int viewCount;
    private int commentCount;
    private LocalDateTime createdAt;

    public static PostListResponse from(Post post) {
        return new PostListResponse(
                post.getId(),
                post.getTitle(),
                post.getUserId(),
                post.getLikeCount(),
                post.getViewCount(),
                post.getCommentCount(),
                post.getCreatedAt()
        );
    }
}