package com.dyes.backend.domain.user.controller;

import com.dyes.backend.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@ToString
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    final private UserService userService;
    @PostMapping("/oauth/google/callback")
    public void callback (@RequestParam(name = "code") String code) throws IOException {
        userService.googleUserChecker(code);
    }
}
