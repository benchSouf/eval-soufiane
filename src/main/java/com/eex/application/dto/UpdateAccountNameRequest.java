package com.eex.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateAccountNameRequest {
    @NotBlank(message = "Account name cannot be empty")
    @Size(max = 100, message = "Account name must not exceed 100 characters")
    private String name;
}