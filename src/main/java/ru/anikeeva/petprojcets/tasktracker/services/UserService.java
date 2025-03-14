package ru.anikeeva.petprojcets.tasktracker.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.anikeeva.petprojcets.tasktracker.models.User;
import ru.anikeeva.petprojcets.tasktracker.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    public User findUserByUsername(final String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Пользователь с указанным именем не найден"));
    }
}