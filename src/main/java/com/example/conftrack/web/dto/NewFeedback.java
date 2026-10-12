package com.example.conftrack.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NewFeedback(
        @NotBlank @Email String attendeeEmail,
        @Min(1) @Max(5) int rating,
        @Size(max = 500) String comments) {
}
