package org.example.community.domain.post.repository;

public interface LikeRepository {
    void save(Long userId, Long postId);
    boolean exists(Long userId, Long postId);
    void delete(Long userId, Long postId);

    void deleteByUserId(Long loginUserId);

    void deleteByPostId(Long postId);
}