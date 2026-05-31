package org.example.community.domain.post.repository;

import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryLikeRepository implements LikeRepository {

    // key: "userId:postId"
    private final Set<String> store = new HashSet<>();

    @Override
    public void save(Long userId, Long postId) {
        store.add(toKey(userId, postId));
    }

    @Override
    public boolean exists(Long userId, Long postId) {
        return store.contains(toKey(userId, postId));
    }

    @Override
    public void delete(Long userId, Long postId) {
        store.remove(toKey(userId, postId));
    }

    @Override
    public void deleteByUserId(Long loginUserId) {
        store.removeIf(key -> key.startsWith(loginUserId + ":"));
    }

    @Override
    public void deleteByPostId(Long postId) {
        store.removeIf(key -> key.startsWith(postId + ":"));
    }

    private String toKey(Long userId, Long postId) {
        return userId + ":" + postId;
    }
}