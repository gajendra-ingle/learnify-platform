package com.learnify.analytics.exception;

import java.util.UUID;

public class CourseNotFoundException extends RuntimeException {

    public CourseNotFoundException(UUID courseId) {
        super("Course analytics not found for courseId: " + courseId);
    }

}
