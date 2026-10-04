package com.adhil.studentapi.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Request body for creating or updating a student. Validated with @Valid in the controller. */
public record StudentRequest(
        @NotBlank(message = "name is required")
        @Size(max = 100, message = "name must be at most 100 characters")
        String name,

        @NotBlank(message = "email is required")
        @Email(message = "email must be a valid address")
        @Size(max = 120, message = "email must be at most 120 characters")
        String email,

        @NotBlank(message = "department is required")
        @Size(max = 60, message = "department must be at most 60 characters")
        String department,

        @NotNull(message = "semester is required")
        @Min(value = 1, message = "semester must be between 1 and 8")
        @Max(value = 8, message = "semester must be between 1 and 8")
        Integer semester,

        @DecimalMin(value = "0.0", message = "cgpa must be between 0 and 10")
        @DecimalMax(value = "10.0", message = "cgpa must be between 0 and 10")
        Double cgpa
) {
}
