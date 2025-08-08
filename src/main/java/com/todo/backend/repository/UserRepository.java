package com.todo.backend.repository;

import com.todo.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    // 삭제: 삭제된 행 수 반환 (void로 해도 됨)
    void deleteByUsername(String username);
}
