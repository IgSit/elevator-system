package com.example.elevatorsystem.repositories;

import com.example.elevatorsystem.models.Elevator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElevatorRepository extends JpaRepository<Elevator, Long> {
}
