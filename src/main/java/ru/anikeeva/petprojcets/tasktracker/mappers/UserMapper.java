package ru.anikeeva.petprojcets.tasktracker.mappers;

import org.mapstruct.Mapper;
import ru.anikeeva.petprojcets.tasktracker.dto.UserDTO;
import ru.anikeeva.petprojcets.tasktracker.models.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO userToUserDTO(User user);
}