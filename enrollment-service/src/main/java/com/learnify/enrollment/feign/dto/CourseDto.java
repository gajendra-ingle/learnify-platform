package com.learnify.enrollment.feign.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDto {

    private UUID id;
    private String title;
    private String shortDescription;
    private UUID instructorId;
    private String instructorName;
    private BigDecimal price;
    private String status;
    private boolean free;
    private boolean certificateAvailable;

}

