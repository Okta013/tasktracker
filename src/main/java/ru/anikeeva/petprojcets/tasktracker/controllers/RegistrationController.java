package ru.anikeeva.petprojcets.tasktracker.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.anikeeva.petprojcets.tasktracker.dto.UserDTO;
import ru.anikeeva.petprojcets.tasktracker.payload.request.SignUpRequest;
import ru.anikeeva.petprojcets.tasktracker.payload.response.JwtConfirmResponse;
import ru.anikeeva.petprojcets.tasktracker.services.RegistrationService;

@RestController
@RequestMapping("/registration")
@RequiredArgsConstructor
public class RegistrationController {
    private final RegistrationService registrationService;

    @PostMapping("/signup")
    public UserDTO signup(@RequestBody final SignUpRequest request) {
        return registrationService.registerUser(request);
    }

    @GetMapping("/confirm")
    public ResponseEntity<JwtConfirmResponse> confirmEmail(@RequestParam("token") String token) {
        JwtConfirmResponse response = registrationService.confirmEmail(token);
        return ResponseEntity.ok(response);
    }
}