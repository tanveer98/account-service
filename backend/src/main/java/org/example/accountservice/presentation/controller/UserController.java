package org.example.accountservice.presentation.controller;

import jakarta.validation.Valid;
import org.example.accountservice.application.AccountService;
import org.example.accountservice.application.UserService;
import org.example.accountservice.application.mapper.AccountMapper;
import org.example.accountservice.application.mapper.UserMapper;
import org.example.accountservice.domain.UserId;
import org.example.accountservice.domain.UserName;
import org.example.accountservice.presentation.dto.AccountDto;
import org.example.accountservice.presentation.dto.CreateUserRequestDto;
import org.example.accountservice.presentation.dto.UserDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final AccountService accountService;
    private final UserService userService;

    public UserController(AccountService accountService, UserService userService) {
        this.accountService = accountService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable long id) {
        return UserMapper.toUserDto(userService.findById(new UserId(id)));
    }

    @PostMapping
    public UserDto create(@RequestBody @Valid CreateUserRequestDto dto) {
        return UserMapper.toUserDto(userService.create(new UserName(dto.name())));
    }

    @GetMapping("/{id}/accounts")
    public List<AccountDto> getAccounts(@PathVariable long id) {
        return accountService.findByUserId(new UserId(id)).stream()
                .map(AccountMapper::toAccountDto)
                .toList();
    }
}
