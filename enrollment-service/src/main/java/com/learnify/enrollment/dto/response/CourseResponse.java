package com.learnify.enrollment.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CourseResponse {

    private UUID id;
    private UUID instructorId;
    private String title;
    private String instructorName;
    private BigDecimal price;
    private String status;
    private String thumbnailUrl;

}
