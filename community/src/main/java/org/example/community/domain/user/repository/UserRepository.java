package org.example.community.domain.user.repository;

import java.util.Optional;
import org.example.community.domain.user.User;

public interface UserRepository {
    <S extends User> S save(S entity);

    Optional<User> findById(Long id);

    void deleteById(Long id);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<User> findByEmail(String email);
}