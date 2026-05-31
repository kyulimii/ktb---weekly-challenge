package org.example.community.domain.post.api.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.community.domain.post.Post;

@Getter
@AllArgsConstructor
public class PostDetailResponse {
    private Long id;
    private String title;
    private String content;
    private byte[] postImage;
    private Long authorId;
    private int likeCount;
    private int viewCount;
    private int commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PostDetailResponse from(Post post) {
        return new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getPostImage(),
                post.getUserId(),
                post.getLikeCount(),
                post.getViewCount(),
                post.getCommentCount(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}