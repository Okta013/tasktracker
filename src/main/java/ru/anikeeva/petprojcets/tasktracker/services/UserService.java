package ru.anikeeva.petprojcets.tasktracker.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.anikeeva.petprojcets.tasktracker.dto.UserDTO;
import ru.anikeeva.petprojcets.tasktracker.exceptions.IncorrectParametersException;
import ru.anikeeva.petprojcets.tasktracker.exceptions.NoRightException;
import ru.anikeeva.petprojcets.tasktracker.exceptions.UserNotFoundException;
import ru.anikeeva.petprojcets.tasktracker.mappers.UserMapper;
import ru.anikeeva.petprojcets.tasktracker.models.Position;
import ru.anikeeva.petprojcets.tasktracker.models.User;
import ru.anikeeva.petprojcets.tasktracker.models.enums.EnumRole;
import ru.anikeeva.petprojcets.tasktracker.models.impl.UserDetailsImpl;
import ru.anikeeva.petprojcets.tasktracker.repositories.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    public User findUserByUsername(final String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Пользователь с указанным именем не найден"));
    }

    public UserDTO showUser(final UUID userId) {
        return userMapper.userToUserDTO(userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("Пользователь не найден")));
    }

    public Page<UserDTO> showAllUsers(final LocalDate startBirthday, final LocalDate endBirthday,
                                      final Boolean isEnabled, final Boolean isDeleted, final EnumRole role,
                                      final Position position, final Integer offset, final Integer limit) {
        if (startBirthday != null && endBirthday != null && startBirthday.isAfter(endBirthday)) {
            throw new IncorrectParametersException("Начальная дата не должна быть позже конечной даты");
        }
        Pageable pageable = PageRequest.of(offset, limit, Sort.by("username").ascending());
        Page<User> users = userRepository.findAllUsersWithFilters(startBirthday, endBirthday, isEnabled, isDeleted, role,
                position, pageable);
        return users.map(userMapper::userToUserDTO);
    }

    public List<UserDTO> findUsers(final String username, final String phone, final String email,
                                   final LocalDate birthday) {
        String usernameBuilder = new StringBuilder("%" + username + "%").toString();
        String phoneBuilder = new StringBuilder("%" + phone + "%").toString();
        String emailBuilder = new StringBuilder("%" + email + "%").toString();
        return userRepository.findAllUsersWithParameters(usernameBuilder, phoneBuilder, emailBuilder, birthday)
                .stream().map(userMapper::userToUserDTO).toList();
    }

    public UserDTO changeUser(final UserDetailsImpl currentUser, final UUID userId, final UserDTO userDTO) {
        if (!currentUser.getId().equals(userId)) {
            throw new NoRightException("У вас нет прав на изменение профиля этого пользователя");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        if (userDTO != null) {
            userMapper.updateUserFromUserDTO(userDTO, user);
        }
        return userMapper.userToUserDTO(userRepository.save(user));
    }
}