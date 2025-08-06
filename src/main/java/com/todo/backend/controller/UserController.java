package com.todo.backend.controller;

import com.todo.backend.entity.User;
import com.todo.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")  // /api는 Nginx에서 처리하니까 Spring은 그냥 /users로
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.save(user));
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> getUser(@PathVariable String username) {
        return userService.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
