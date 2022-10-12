package com.example.elevatorsystem.controllers;

import com.example.elevatorsystem.services.ElevatorService;
import com.example.elevatorsystem.models.Elevator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("elevators")
public class ElevatorController {

    private final ElevatorService elevatorService;

    @Autowired
    public ElevatorController(ElevatorService elevatorService) {
        this.elevatorService = elevatorService;
    }

    @GetMapping
    public List<String> getElevators() {
        return elevatorService.getElevators().stream().map(Elevator::toString).toList();
    }

    @PostMapping
    public void addElevators(@RequestBody int amount) {
        elevatorService.addElevators(amount);
    }
}
