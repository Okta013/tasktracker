package ru.anikeeva.petprojcets.tasktracker.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.anikeeva.petprojcets.tasktracker.models.User;
import ru.anikeeva.petprojcets.tasktracker.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User saveUser(final User user) {
        return userRepository.save(user);
    }

    public User findUserByUsername(final String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Пользователь с указанным именем не найден"));
    }

    public User getCurrentUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return findUserByUsername(username);
    }
}