package com.example.elevatorsystem.services;

import com.example.elevatorsystem.models.Elevator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class StepService {

    private final ElevatorService elevatorService;
    private final ElevatorMoveManagerService moveManagerService;

    @Autowired
    public StepService(ElevatorService elevatorService, ElevatorMoveManagerService moveManagerService) {
        this.elevatorService = elevatorService;
        this.moveManagerService = moveManagerService;
    }

    @Transactional
    public void runStep() {
        elevatorService.getElevators().forEach(e -> e.step(moveManagerService));
    }
}
