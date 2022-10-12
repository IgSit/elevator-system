package com.example.elevatorsystem.controllers;

import com.example.elevatorsystem.services.ElevatorMoveManagerService;
import com.example.elevatorsystem.services.ElevatorService;
import com.example.elevatorsystem.models.Elevator;
import com.example.elevatorsystem.models.ElevatorMove;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("elevator-moves")
public class ElevatorMoveController {

    private final ElevatorMoveManagerService moveManagerService;
    private final ElevatorService elevatorService;

    @Autowired
    public ElevatorMoveController(ElevatorMoveManagerService moveManagerService,
                                  ElevatorService elevatorService) {
        this.moveManagerService = moveManagerService;
        this.elevatorService = elevatorService;
    }

    @PostMapping
    public void addElevatorMove(@RequestBody ElevatorMove move) {
        moveManagerService.addNewElevatorMove(move.getFloor());
    }

    @GetMapping
    public List<String> getElevatorMoves(@RequestParam Long elevator_id) {
        Optional<Elevator> elevator = elevatorService.findElevatorById(elevator_id);
        if (elevator.isEmpty()) throw new IllegalArgumentException("Elevator does not exist");
        return elevator.get().getPlannedMoves().stream().map(ElevatorMove::toString).toList();
    }
}
