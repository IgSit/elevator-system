package com.example.elevatorsystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

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
    public Set<ElevatorMove> getElevatorMoves(@RequestParam Long elevator_id) {
        Optional<Elevator> elevator = elevatorService.findElevatorById(elevator_id);
        if (elevator.isEmpty()) throw new IllegalArgumentException("Elevator does not exist");
        return elevator.get().getPlannedMoves();
    }
}
