package com.example.elevatorsystem.services;

import com.example.elevatorsystem.models.Elevator;
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
