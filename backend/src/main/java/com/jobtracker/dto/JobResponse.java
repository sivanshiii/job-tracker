package com.jobtracker.dto;

import com.jobtracker.entity.JobApplication;
import com.jobtracker.entity.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record JobResponse(
        Long id,
        String company,
        String role,
        String location,
        Status status,
        String jobUrl,
        String notes,
        LocalDate appliedDate,
        LocalDateTime createdAt) {

    public static JobResponse from(JobApplication j) {
        return new JobResponse(j.getId(), j.getCompany(), j.getRole(), j.getLocation(),
                j.getStatus(), j.getJobUrl(), j.getNotes(), j.getAppliedDate(), j.getCreatedAt());
    }
}
