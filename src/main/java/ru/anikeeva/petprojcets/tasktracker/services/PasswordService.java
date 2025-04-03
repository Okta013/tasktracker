package ru.anikeeva.petprojcets.tasktracker.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.anikeeva.petprojcets.tasktracker.exceptions.IncorrectParametersException;
import ru.anikeeva.petprojcets.tasktracker.exceptions.NoRightException;
import ru.anikeeva.petprojcets.tasktracker.exceptions.UserNotFoundException;
import ru.anikeeva.petprojcets.tasktracker.models.User;
import ru.anikeeva.petprojcets.tasktracker.models.impl.UserDetailsImpl;
import ru.anikeeva.petprojcets.tasktracker.payload.request.ChangePasswordRequest;
import ru.anikeeva.petprojcets.tasktracker.repositories.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void changePassword(final UserDetailsImpl currentUser, final UUID userId, final ChangePasswordRequest request) {
        if (!currentUser.getId().equals(userId)) {
            throw new NoRightException("У вас нет прав на изменение профиля этого пользователя");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IncorrectParametersException("Старый пароль неверный");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IncorrectParametersException("Новый пароль и его подтверждение не совпадают");
        }
        String encodeNewPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(encodeNewPassword);
        userRepository.save(user);
    }
}