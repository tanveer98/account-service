package org.example.accountservice.presentation.dto;

public record UserDto(long id, String name, boolean active) {

    @Override
    public String toString() {
        return "User[id=" + id + ", active=" + active + "]";
    }
}
