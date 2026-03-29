package com.learnify.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {

    @Id
    private UUID id; // Same as auth-service user ID

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(length = 500)
    private String bio;

    @Column
    private String headline;

    @Column
    private String website;

    @Column
    private String linkedin;

    @Column
    private String twitter;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "is_instructor_verified")
    private boolean instructorVerified = false;

    @Column(name = "total_students")
    private Integer totalStudents = 0;

    @Column(name = "total_courses")
    private Integer totalCourses = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

