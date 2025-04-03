package ru.anikeeva.petprojcets.tasktracker.controllers;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.anikeeva.petprojcets.tasktracker.models.enums.EnumResetPasswordParameter;
import ru.anikeeva.petprojcets.tasktracker.payload.request.ResetPasswordRequest;
import ru.anikeeva.petprojcets.tasktracker.payload.request.SignInRequest;
import ru.anikeeva.petprojcets.tasktracker.payload.response.ChangeForgottenPasswordResponse;
import ru.anikeeva.petprojcets.tasktracker.payload.response.JwtAuthenticationResponse;
import ru.anikeeva.petprojcets.tasktracker.services.AuthService;
import ru.anikeeva.petprojcets.tasktracker.services.PasswordService;

@RestController
@RequestMapping("/signin")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final PasswordService passwordService;

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> authenticateUser(@RequestBody SignInRequest request) {
        JwtAuthenticationResponse response = authService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset_password")
    public ResponseEntity<Void> resetPassword(@RequestParam EnumResetPasswordParameter parameter,
                                              @RequestParam String value) throws MessagingException {
        passwordService.resetPassword(parameter, value);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/change_password")
    public ResponseEntity<Boolean> validateResetToken(@RequestParam String token) {
        boolean isValid = passwordService.isResetTokenValid(token);
        return ResponseEntity.ok(isValid);
    }

    @PostMapping("/change_password")
    public ResponseEntity<ChangeForgottenPasswordResponse> changeForgottenPassword(@RequestBody ResetPasswordRequest request,
                                                                                   @RequestParam String token) {
        ChangeForgottenPasswordResponse response = passwordService.changeForgottenPassword(request, token);
        return ResponseEntity.ok(response);
    }
}