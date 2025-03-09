package ru.anikeeva.petprojcets.tasktracker.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.anikeeva.petprojcets.tasktracker.payload.request.SignInRequest;
import ru.anikeeva.petprojcets.tasktracker.payload.response.JwtAuthenticationResponse;
import ru.anikeeva.petprojcets.tasktracker.services.AuthService;

@RestController
@RequestMapping("/signin")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> authenticateUser(@RequestBody SignInRequest request) {
        JwtAuthenticationResponse response = authService.authenticate(request);
        return ResponseEntity.ok(response);
    }
}