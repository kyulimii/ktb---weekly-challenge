package org.example.community.domain.post.comment.repository;

import java.util.List;
import java.util.Optional;
import org.example.community.domain.post.comment.Comment;

public interface CommentRepository {
    <S extends Comment> S save(S entity);

    Optional<Comment> findById(Long id);

    List<Comment> findByPostId(Long postId);

    void deleteById(Long id);

    void deleteByUserId(Long loginUserId);

    void deleteByPostId(Long postId);
}
