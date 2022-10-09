package com.example.elevatorsystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("elevator-moves")
public class ElevatorMoveController {

    private final ElevatorMoveManagerService moveManagerService;

    @Autowired
    public ElevatorMoveController(ElevatorMoveManagerService moveManagerService) {
        this.moveManagerService = moveManagerService;
    }

    @PostMapping
    public void addElevatorMove(@RequestBody ElevatorMove move) {
        moveManagerService.addNewElevatorMove(move.floor());
    }
}
