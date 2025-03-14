package ru.anikeeva.petprojcets.tasktracker.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.anikeeva.petprojcets.tasktracker.payload.request.SignInRequest;
import ru.anikeeva.petprojcets.tasktracker.payload.response.JwtAuthenticationResponse;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationResponse authenticate(SignInRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadCredentialsException("Сочетание имени пользователя и пароля не найдено");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtService.generateToken(userDetails);
        String role = userDetails.getAuthorities().toString();
        return new JwtAuthenticationResponse(token, userDetails.getUsername(), role);
    }
}