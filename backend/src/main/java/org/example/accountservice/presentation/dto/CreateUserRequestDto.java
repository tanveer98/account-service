package org.example.accountservice.presentation.dto;

import jakarta.validation.constraints.NotEmpty;

public record CreateUserRequestDto(@NotEmpty String name) {

    @Override
    public String toString() {
        return "[CreateUserRequestDto] PII - Obfuscated";
    }
}
