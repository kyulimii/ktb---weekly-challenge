package org.example.community.domain.post.repository;

import java.util.List;
import java.util.Optional;
import org.example.community.domain.post.Post;

public interface PostRepository {
    <S extends Post> S save(S entity);

    Optional<Post> findById(Long postId);

    List<Post> findAll();

    void deleteById(Long postId);

    void deleteByUserId(Long loginUserId);
}
