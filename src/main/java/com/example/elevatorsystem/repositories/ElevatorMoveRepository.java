package com.example.elevatorsystem.repositories;

import com.example.elevatorsystem.models.ElevatorMove;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElevatorMoveRepository extends JpaRepository<ElevatorMove, Long> {
}
