package org.example.community.domain.post.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.example.community.domain.post.Post;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryPostRepository implements PostRepository {

    private final Map<Long, Post> store = new ConcurrentHashMap<>();
    private Long sequence = 1L;

    @Override
    public <S extends Post> S save(S post) {
        if (post.getId() == null) {
            // 신규 저장: assignId로 id 할당
            Long id = sequence++;
            post.assignId(id);
            post.onCreate();
            store.put(id, post);
        }
        else
        // 수정
            store.put(post.getId(), post);
        return post;
    }

    @Override
    public Optional<Post> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Post> findAll() {
        return new ArrayList<>(store.values());
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
}