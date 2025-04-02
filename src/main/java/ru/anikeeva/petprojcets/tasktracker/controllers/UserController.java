package ru.anikeeva.petprojcets.tasktracker.controllers;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.anikeeva.petprojcets.tasktracker.dto.UserDTO;
import ru.anikeeva.petprojcets.tasktracker.models.Position;
import ru.anikeeva.petprojcets.tasktracker.models.enums.EnumRole;
import ru.anikeeva.petprojcets.tasktracker.models.impl.UserDetailsImpl;
import ru.anikeeva.petprojcets.tasktracker.services.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public UserDTO showUser(@PathVariable UUID id) {
        return userService.showUser(id);
    }

    @GetMapping
    public Page<UserDTO> showAllUsers(@RequestParam(required = false) LocalDate startBirthday,
                                      @RequestParam(required = false) LocalDate endBirthday,
                                      @RequestParam(required = false) Boolean isEnabled,
                                      @RequestParam(required = false) Boolean isDeleted,
                                      @RequestParam(required = false) EnumRole role,
                                      @RequestParam(required = false) Position position,
                                      @RequestParam(value = "offset", defaultValue = "0") @Min(0) Integer offset,
                                      @RequestParam(value = "limit", defaultValue = "20") @Min(1) @Max(100) Integer limit) {
        return userService.showAllUsers(startBirthday, endBirthday, isEnabled, isDeleted, role, position, offset, limit);
    }

    @GetMapping("/search")
    public List<UserDTO> findUsers(@RequestParam(required = false) String username,
                                   @RequestParam(required = false) String phone,
                                   @RequestParam(required = false)String email,
                                   @RequestParam(required = false)LocalDate birthday) {
        return userService.findUsers(username, phone, email, birthday);
    }

    @PostMapping("/{id}/change")
    public UserDTO changeUser(@AuthenticationPrincipal UserDetailsImpl currentUser, @PathVariable UUID id,
                              @RequestBody UserDTO userDTO) {
        return userService.changeUser(currentUser, id, userDTO);
    }
}