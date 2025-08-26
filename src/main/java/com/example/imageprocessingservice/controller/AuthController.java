package com.example.imageprocessingservice.controller;

import com.example.imageprocessingservice.model.User;
import com.example.imageprocessingservice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity <User> register (@RequestParam String username, @RequestParam String password) {
        User user = this.authService.register (username, password);
        return ResponseEntity.ok (user);
    }

    @PostMapping("/login")
    public ResponseEntity <String> login (@RequestParam String username, @RequestParam String password) {
        String token = this.authService.login (username, password);
        return ResponseEntity.ok (token);
    }
}