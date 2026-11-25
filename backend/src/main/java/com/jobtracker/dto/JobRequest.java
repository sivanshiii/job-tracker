package com.jobtracker.dto;

import com.jobtracker.entity.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record JobRequest(
        @NotBlank String company,
        @NotBlank String role,
        String location,
        @NotNull Status status,
        String jobUrl,
        @Size(max = 2000) String notes,
        LocalDate appliedDate) {
}
