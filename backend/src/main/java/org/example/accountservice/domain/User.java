package org.example.accountservice.domain;

public record User(
        UserId id,
        UserName userName,
        boolean active
) {
}
