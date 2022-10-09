package com.example.elevatorsystem;

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
    public List<Elevator> getElevators() {
        return elevatorService.getElevators();
    }

    @PostMapping
    public void addElevators(@RequestBody int amount) {
        elevatorService.addElevators(amount);
    }
}
