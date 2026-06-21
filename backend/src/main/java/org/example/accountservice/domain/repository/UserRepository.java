package org.example.accountservice.domain.repository;

import org.example.accountservice.domain.User;
import org.example.accountservice.domain.UserId;
import org.example.accountservice.domain.UserName;

import java.util.Optional;

public interface UserRepository {
    UserId save(UserName name);
    Optional<User> findById(UserId userId);
}
