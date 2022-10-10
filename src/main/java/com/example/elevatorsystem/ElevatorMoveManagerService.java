package com.example.elevatorsystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class ElevatorMoveManagerService {

    private final ElevatorMoveCalculatorService moveCalculatorService;

    @Autowired
    ElevatorMoveManagerService(ElevatorMoveCalculatorService moveCalculatorService) {
        this.moveCalculatorService = moveCalculatorService;
    }

    @Transactional
    public void addNewElevatorMove(int pendingFloor) {
        ElevatorMoveCalculatorHelper helper = moveCalculatorService.findOptimalElevator(pendingFloor);
        if (helper.flag() != Flag.ADD_TO_PLANNED_MOVES) addAsCurrentDestination(helper, pendingFloor, helper.flag());
        else {
            ElevatorMove move = new ElevatorMove(pendingFloor, helper.index());
            addToPlannedMoves(helper, move);
        }
    }

    private void addAsCurrentDestination(ElevatorMoveCalculatorHelper helper, int pendingFloor, Flag flag) {
        Elevator elevator = helper.elevator();
        if (flag == Flag.CHANGE_CURRENT_MOVE) {
            ElevatorMove move = new ElevatorMove();
            elevator.addMove(move);
        }
        elevator.setCurrentMove(pendingFloor);
    }

    private void addToPlannedMoves(ElevatorMoveCalculatorHelper helper, ElevatorMove move) {
        Elevator elevator = helper.elevator();
        elevator.addMove(move);
    }
}
