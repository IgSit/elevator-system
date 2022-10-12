package com.example.elevatorsystem.services;

import com.example.elevatorsystem.models.Elevator;
import com.example.elevatorsystem.models.ElevatorMove;
import com.example.elevatorsystem.models.ElevatorMoveCalculatorHelper;
import com.example.elevatorsystem.models.Flag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class ElevatorMoveManagerService {

    private final ElevatorMoveCalculatorService moveCalculatorService;
    private final ElevatorMoveService moveService;

    @Autowired
    ElevatorMoveManagerService(ElevatorMoveCalculatorService moveCalculatorService, ElevatorMoveService moveService) {
        this.moveCalculatorService = moveCalculatorService;
        this.moveService = moveService;
    }

    @Transactional
    public void addNewElevatorMove(int pendingFloor) {
        ElevatorMoveCalculatorHelper helper = moveCalculatorService.findOptimalElevator(pendingFloor);
        if (helper.flag() != Flag.ADD_TO_PLANNED_MOVES) addAsCurrentDestination(helper, pendingFloor, helper.flag());
        else {
            ElevatorMove move = new ElevatorMove(pendingFloor, helper.index(), helper.elevator());
            addToPlannedMoves(helper, move);
        }
    }

    private void addAsCurrentDestination(ElevatorMoveCalculatorHelper helper, int pendingFloor, Flag flag) {
        Elevator elevator = helper.elevator();
        if (flag == Flag.CHANGE_CURRENT_MOVE) {
            ElevatorMove move = new ElevatorMove(elevator.getCurrentFloor(), 0, helper.elevator());
            elevator.addMove(move, moveService);
        }
        elevator.setCurrentMove(pendingFloor);
    }

    private void addToPlannedMoves(ElevatorMoveCalculatorHelper helper, ElevatorMove move) {
        Elevator elevator = helper.elevator();
        elevator.addMove(move, moveService);
    }
}
