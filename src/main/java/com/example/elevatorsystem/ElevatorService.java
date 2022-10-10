package com.example.elevatorsystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ElevatorService {

    private final ElevatorRepository elevatorRepository;

    @Autowired
    public ElevatorService(ElevatorRepository elevatorRepository) {
        this.elevatorRepository = elevatorRepository;
    }

    public Optional<Elevator> findElevatorById(Long id) {
        return elevatorRepository.findById(id);
    }

    public List<Elevator> getElevators() {
        return elevatorRepository.findAll();
    }

    public void addElevators(int amount) {
        for (int i = 0; i < amount; i++) {
            elevatorRepository.save(new Elevator());
        }
    }
}
