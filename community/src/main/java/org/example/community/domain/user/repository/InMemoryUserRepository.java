package org.example.community.domain.user.repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.example.community.domain.user.User;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> store = new ConcurrentHashMap<>();
    private Long sequence = 1L;

    @Override
    public <S extends User> S save(S user) {
        if (user.getId() == null) {
            // 신규 저장: assignId로 id 할당
            Long id = sequence++;
            user.assignId(id);
            user.onCreate();
            store.put(id, user);
        }
        else
        // 수정
            store.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }

    // 이미 이메일 존재하는지 확인
    @Override
    public boolean existsByEmail(String email) {
        return store.values().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }

    // 이미 닉네임이 존재하는지 확인
    @Override
    public boolean existsByNickname(String nickname) {
        return store.values().stream()
                .anyMatch(user -> user.getNickname().equals(nickname));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return store.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }
}
