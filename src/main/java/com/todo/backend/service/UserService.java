package com.todo.backend.service;

import com.todo.backend.entity.User;
import com.todo.backend.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 생성/수정 시: 해당 username 캐시 갱신
    @CachePut(value = "userByUsername", key = "#result.username", unless = "#result == null")
    public User save(User user) {
        return userRepository.save(user);
    }

    // 조회 시: 캐시 우선
    @Cacheable(value = "userByUsername", key = "#username")
    public User getByUsername(String username) {
        // DB 미스면 null 반환 → application.yml에서 cache-null-values:false라 캐시 안 됨
        return userRepository.findByUsername(username).orElse(null);
    }

    // 삭제 시: 캐시 무효화
    @Transactional
    @CacheEvict(value = "userByUsername", key = "#username")
    public void deleteByUsername(String username) {
        userRepository.deleteByUsername(username);
    }
}
