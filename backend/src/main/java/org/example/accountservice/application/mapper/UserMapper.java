package org.example.accountservice.application.mapper;

import org.example.accountservice.domain.User;
import org.example.accountservice.presentation.dto.UserDto;

public class UserMapper {
    private UserMapper() {
    }

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.id().value(),
                user.userName().value(),
                user.active()
        );
    }
}
