package ru.anikeeva.petprojcets.tasktracker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.anikeeva.petprojcets.tasktracker.models.Position;
import ru.anikeeva.petprojcets.tasktracker.models.enums.EnumPosition;

import java.util.UUID;

@Repository
public interface PositionRepository extends JpaRepository<Position, UUID> {
    Position findByPosition(EnumPosition position);
}
