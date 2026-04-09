package com.learnify.lesson.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "lessons")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    private LessonType type;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "video_duration_seconds")
    private Integer videoDurationSeconds;

    @Column(name = "content_text", columnDefinition = "TEXT")
    private String contentText;

    @Column(name = "order_index")
    private Integer orderIndex;

    @Column(name = "is_free_preview")
    private boolean freePreview = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
