package org.example.community.domain.post.comment.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.example.community.domain.post.comment.Comment;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryCommentRepository implements CommentRepository {
    private final Map<Long, Comment> store = new ConcurrentHashMap<>();
    private Long sequence = 1L;

    @Override
    public <S extends Comment> S save(S comment) {
        if (comment.getId() == null) {
            // 신규 저장: assignId로 id 할당
            Long id = sequence++;
            comment.assignId(id);
            comment.onCreate();
        }
        // 수정
        store.put(comment.getId(), comment);
        return comment;
    }

    @Override
    public Optional<Comment> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Comment> findByPostId(Long postId) {
        return store.values().stream()
                .filter(comment -> comment.getPostId().equals(postId))
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }

    @Override
    public void deleteByUserId(Long loginUserId) {
        store.entrySet()
                .removeIf(entry -> entry.getValue().getUserId().equals(loginUserId));
    }

    @Override
    public void deleteByPostId(Long postId) {
        store.entrySet()
                .removeIf(entry -> entry.getValue().getPostId().equals(postId));
    }
}
