package com.example.elevatorsystem.services;

import com.example.elevatorsystem.models.Elevator;
import com.example.elevatorsystem.models.ElevatorMove;
import com.example.elevatorsystem.models.ElevatorMoveCalculatorHelper;
import com.example.elevatorsystem.models.AddMoveHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class ElevatorMoveManagerService {

    private final ElevatorMoveCalculatorService moveCalculatorService;
    private final ElevatorMoveService moveService;

    @Autowired
    ElevatorMoveManagerService(
            ElevatorMoveCalculatorService moveCalculatorService,
            ElevatorMoveService moveService) {
        this.moveCalculatorService = moveCalculatorService;
        this.moveService = moveService;
    }

    @Transactional
    public void addNewElevatorMove(int pendingFloor) {
        ElevatorMoveCalculatorHelper helper = moveCalculatorService.findOptimalElevator(pendingFloor);

        if (helper.moveHandler() != AddMoveHandler.ADD_TO_PLANNED_MOVES) {
            addAsCurrentDestination(helper, pendingFloor, helper.moveHandler());
        }
        else {
            ElevatorMove move = new ElevatorMove(pendingFloor, helper.index(), helper.elevator());
            addToPlannedMoves(helper, move);
        }
    }

    @Transactional
    public ElevatorMove popFirstMove(Long elevatorId) {
        ElevatorMove firstMove = moveService.getElevatorMoveAtIndex(elevatorId, 0);
        removeFirstMove(elevatorId);
        return firstMove;
    }


    private void removeFirstMove(Long elevatorId) {
        moveService.removeElevatorMoveAtIndex(elevatorId, 0);
        moveService.updateFutureElevatorMoves(elevatorId, 1, -1);
    }

    private void addAsCurrentDestination(
            ElevatorMoveCalculatorHelper helper,
            int pendingFloor,
            AddMoveHandler addMoveHandler) {

        Elevator elevator = helper.elevator();
        if (addMoveHandler == AddMoveHandler.CHANGE_CURRENT_MOVE) {
            ElevatorMove move = new ElevatorMove(elevator.getCurrentMove(), 0, helper.elevator());
            elevator.addMove(move, moveService);
        }
        elevator.setCurrentMove(pendingFloor);
    }

    private void addToPlannedMoves(ElevatorMoveCalculatorHelper helper, ElevatorMove move) {
        Elevator elevator = helper.elevator();
        elevator.addMove(move, moveService);
    }
}
