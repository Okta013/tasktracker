package ru.anikeeva.petprojcets.tasktracker.services;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.anikeeva.petprojcets.tasktracker.dto.UserDTO;
import ru.anikeeva.petprojcets.tasktracker.exceptions.DuplicateAccountException;
import ru.anikeeva.petprojcets.tasktracker.mappers.UserMapper;
import ru.anikeeva.petprojcets.tasktracker.models.User;
import ru.anikeeva.petprojcets.tasktracker.models.SupportiveToken;
import ru.anikeeva.petprojcets.tasktracker.models.enums.EnumRole;
import ru.anikeeva.petprojcets.tasktracker.observer.OnRegistrationCompleteEvent;
import ru.anikeeva.petprojcets.tasktracker.payload.request.SignUpRequest;
import ru.anikeeva.petprojcets.tasktracker.payload.response.JwtConfirmResponse;
import ru.anikeeva.petprojcets.tasktracker.repositories.UserRepository;
import ru.anikeeva.petprojcets.tasktracker.repositories.SupportiveTokenRepository;

import java.util.Date;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class RegistrationService implements IUserService{
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final SupportiveTokenRepository tokenRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final int EXPIRY_TIME_MINUTES = 24 * 60;
    private final String APP_URL = "http://localhost:8080";

    @Override
    public UserDTO registerUser(final SignUpRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateAccountException("Пользователь с таким именем уже существует");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateAccountException("Пользователь с таким email уже существует");
        }
        User user = new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getEmail(),
                EnumRole.ROLE_USER
        );
        userRepository.save(user);
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(Locale.getDefault(), APP_URL, user));
        return userMapper.userToUserDTO(user);
    }

    @Override
    public void createVerificationToken(final User user, final String verificationToken) {
        SupportiveToken myToken = new SupportiveToken(verificationToken, user, EXPIRY_TIME_MINUTES);
        tokenRepository.save(myToken);
    }

    @Override
    public JwtConfirmResponse confirmEmail(final String token) {
        SupportiveToken verificationToken = tokenRepository.findByToken(token).orElseThrow(
                () -> new IllegalArgumentException("Токен не найден"));
        if (verificationToken.getExpiryDate().before(new Date())) {
            return new JwtConfirmResponse("Срок действия токена истек", false);
        }
        User user = verificationToken.getUser();
        if (user.isEnabled()) {
            return new JwtConfirmResponse("Пользователь уже активирован", false);
        }
        user.setEnabled(true);
        userRepository.save(user);
        tokenRepository.delete(verificationToken);
        return new JwtConfirmResponse("Email успешно подтвержден", true);
    }
}