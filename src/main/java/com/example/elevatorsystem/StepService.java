package com.example.elevatorsystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class StepService {

    private final ElevatorService elevatorService;

    @Autowired
    public StepService(ElevatorService elevatorService) {
        this.elevatorService = elevatorService;
    }

    @Transactional
    public void runStep() {
        elevatorService.getElevators().forEach(Elevator::step);
    }
}
