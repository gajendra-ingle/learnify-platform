package com.learnify.lesson.repository;

import com.learnify.lesson.entity.Section;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SectionRepositoryTest {

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private EntityManager entityManager;


    @Test
    @DisplayName("Should return sections ordered by orderIndex")
    void shouldFindSectionsByCourseIdOrdered() {
        UUID courseId = UUID.randomUUID();

        Section s1 = Section.builder()
                .id(UUID.randomUUID())
                .courseId(courseId)
                .title("Section 1")
                .orderIndex(2)
                .build();

        Section s2 = Section.builder()
                .id(UUID.randomUUID())
                .courseId(courseId)
                .title("Section 2")
                .orderIndex(1)
                .build();

        entityManager.persist(s1);
        entityManager.persist(s2);
        entityManager.flush();

        List<Section> result = sectionRepository.findByCourseIdOrderByOrderIndex(courseId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getOrderIndex()).isEqualTo(1);
        assertThat(result.get(1).getOrderIndex()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should return empty list when no sections found")
    void shouldReturnEmptySections() {
        UUID courseId = UUID.randomUUID();
        List<Section> result = sectionRepository.findByCourseIdOrderByOrderIndex(courseId);
        assertThat(result).isEmpty();
    }
    @DisplayName("Should return empty list when no sections found for course")
    void shouldReturnEmptyListWhenNoSections() {
        UUID courseId = UUID.randomUUID();

        List<Section> result = sectionRepository
                .findByCourseIdOrderByOrderIndex(courseId);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should only return sections for the given course")
    void shouldOnlyReturnSectionsForGivenCourse() {
        UUID course1 = UUID.randomUUID();
        UUID course2 = UUID.randomUUID();

        Section s1 = new Section();
        s1.setCourseId(course1);
        s1.setOrderIndex(1);

        Section s2 = new Section();
        s2.setCourseId(course2);
        s2.setOrderIndex(1);

        sectionRepository.saveAll(List.of(s1, s2));

        List<Section> result = sectionRepository
                .findByCourseIdOrderByOrderIndex(course1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCourseId()).isEqualTo(course1);
    }
}