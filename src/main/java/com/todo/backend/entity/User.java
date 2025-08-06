package com.todo.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")  // 'user'는 예약어일 수 있어 'users'로 맞추는 게 안전
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String username;

    private String password;

    private String role;

    @Column(name = "created_at")
    private String createdAt; // 또는 LocalDateTime
}

