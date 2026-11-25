package com.jobtracker.dto;

import com.jobtracker.entity.Status;
import jakarta.validation.constraints.NotNull;

public record StatusUpdateRequest(@NotNull Status status) {
}
