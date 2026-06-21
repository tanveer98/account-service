package org.example.accountservice.application;

import org.example.accountservice.domain.User;
import org.example.accountservice.domain.UserId;
import org.example.accountservice.domain.UserName;
import org.example.accountservice.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findById(UserId userId) {
        //TODO: set proper exception here
        return userRepository.findById(userId).orElseThrow();
    }

    public User create(UserName userName) {
        var id = userRepository.save(userName);
        return findById(id);
    }
}
