package com.example.elevatorsystem.controllers;

import com.example.elevatorsystem.services.ElevatorMoveManagerService;
import com.example.elevatorsystem.services.ElevatorMoveService;
import com.example.elevatorsystem.models.ElevatorMove;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("elevator-moves")
public class ElevatorMoveController {

    private final ElevatorMoveManagerService moveManagerService;
    private final ElevatorMoveService elevatorMoveService;

    @Autowired
    public ElevatorMoveController(ElevatorMoveManagerService moveManagerService, ElevatorMoveService elevatorMoveService) {
        this.moveManagerService = moveManagerService;
        this.elevatorMoveService = elevatorMoveService;
    }

    @PostMapping
    public void addElevatorMove(@RequestBody ElevatorMove move) {
        moveManagerService.addNewElevatorMove(move.getFloor());
    }

    @GetMapping
    public List<String> getElevatorMovesByElevatorId(@RequestParam Long elevator_id) {
        return elevatorMoveService.getElevatorMovesByElevatorId(elevator_id)
                .stream().map(ElevatorMove::toString).toList();
    }
}
