package ru.anikeeva.petprojcets.tasktracker.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import ru.anikeeva.petprojcets.tasktracker.entities.User;
import ru.anikeeva.petprojcets.tasktracker.entities.enums.EnumRole;
import ru.anikeeva.petprojcets.tasktracker.entities.impl.UserDetailsImpl;
import ru.anikeeva.petprojcets.tasktracker.exceptions.DuplicateAccountException;
import ru.anikeeva.petprojcets.tasktracker.payload.request.SignUpRequest;
import ru.anikeeva.petprojcets.tasktracker.payload.response.JwtAuthenticationResponse;
import ru.anikeeva.petprojcets.tasktracker.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public JwtAuthenticationResponse registerUser(SignUpRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateAccountException("Пользователь с таким именем уже существует");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateAccountException("Пользователь с таким email уже существует");
        }
        User user = new User(
                request.getUsername(),
                BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()),
                request.getEmail(),
                EnumRole.ROLE_USER
        );
        userRepository.save(user);
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        String token = jwtService.generateToken(userDetails);
        return new JwtAuthenticationResponse(token, userDetails.getUsername(), userDetails.getAuthorities().toString());
    }
}
