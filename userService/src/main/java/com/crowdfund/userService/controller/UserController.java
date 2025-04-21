package com.crowdfund.userService.controller;

import com.crowdfund.userService.model.request.LoginRequest;
import com.crowdfund.userService.model.request.UpdateUserRequest;
import com.crowdfund.userService.model.request.UserRequest;
import com.crowdfund.userService.model.response.AuthResponse;
import com.crowdfund.userService.model.response.UserProfileResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.crowdfund.userService.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody UserRequest request) {
        try {
            AuthResponse response = userService.register(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new AuthResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new AuthResponse("Registration failed: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = userService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(new AuthResponse(e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getProfile(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(userService.getProfile(username));
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateUserRequest request, Authentication authentication) {
        String username = authentication.getName();
        userService.updateProfile(username, request);
        return ResponseEntity.ok("Profile updated");
    }
}
