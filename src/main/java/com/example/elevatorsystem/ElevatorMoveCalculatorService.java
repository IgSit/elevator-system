package com.example.elevatorsystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ElevatorMoveCalculatorService {

    private final ElevatorService elevatorService;

    @Autowired
    public ElevatorMoveCalculatorService(ElevatorService elevatorService) {
        this.elevatorService = elevatorService;
    }

    public ElevatorMoveCalculatorHelper findOptimalElevator(int pendingFloor) {
        ElevatorMoveCalculatorHelper result = new ElevatorMoveCalculatorHelper(Integer.MAX_VALUE, null, 0, null);
        List<Elevator> elevators = elevatorService.getElevators();

        ElevatorMoveCalculatorHelper freeElevatorHelper = findClosestFreeElevator(elevators, pendingFloor);
        if (freeElevatorHelper.elevator() != null) return freeElevatorHelper;

        for (Elevator elevator : elevators) {
            if (elevator.hasSameDirection(pendingFloor))
                return new ElevatorMoveCalculatorHelper(0, elevator, 0, Flag.CHANGE_CURRENT_MOVE);

            int currentFloor = elevator.getCurrentFloor();
            int nextFloor = elevator.getCurrentMove();
            int currentMoveChange = calculateCurrentMoveChange(currentFloor, nextFloor, pendingFloor);
            int futureMovesChange = calculateFutureMovesChange(elevator.getPlannedMoves().size(), currentMoveChange);
            int reachTime = Math.abs(pendingFloor - currentFloor);
            int totalChange = currentMoveChange + futureMovesChange + reachTime;
            result = relaxResult(result, totalChange, elevator, 0, Flag.CHANGE_CURRENT_MOVE);

            result = findResultInPlannedRows(result, elevator, pendingFloor);
        }
        return result;
    }

    private ElevatorMoveCalculatorHelper findClosestFreeElevator(List<Elevator> elevators, int pendingFloor) {
        int minDistance = Integer.MAX_VALUE;
        Elevator result = null;
         for (Elevator elevator: elevators) {
             if (elevator.isFree()) {
                 int currentDistance = Math.abs(elevator.getCurrentFloor() - pendingFloor);
                 if (currentDistance < minDistance) {
                     minDistance = currentDistance;
                     result = elevator;
                 }
             }
         }
         return new ElevatorMoveCalculatorHelper(minDistance, result, 0, Flag.ADD_CURRENT_MOVE);
    }

    private ElevatorMoveCalculatorHelper findResultInPlannedRows(ElevatorMoveCalculatorHelper result,
                                                                 Elevator elevator,
                                                                 int pendingFloor) {
        List<ElevatorMove> plannedMoves = elevator.getPlannedMoves().stream().toList();
        int reachTime = Math.abs(pendingFloor - elevator.getCurrentFloor());

        int nextFloor = elevator.getCurrentMove();
        for (int i = 0; i < plannedMoves.size(); i++) {
            int currentFloor = nextFloor;
            nextFloor = plannedMoves.get(i).getFloor();
            int currentMoveChange = calculateCurrentMoveChange(currentFloor, nextFloor, pendingFloor);
            int futureMovesChange = calculateFutureMovesChange(elevator.getPlannedMoves().size(), currentMoveChange);
            reachTime += Math.abs(nextFloor - currentFloor);
            int totalChange = currentMoveChange + futureMovesChange + reachTime;
            result = relaxResult(result, totalChange, elevator, i + 1, Flag.ADD_TO_PLANNED_MOVES);
        }
        return result;
    }

    private int calculateCurrentMoveChange(int currentFloor, int nextFloor, int pendingFloor) {
        if (currentFloor <= pendingFloor && pendingFloor <= nextFloor) return 0;
        if (currentFloor >= pendingFloor && pendingFloor >= nextFloor) return 0;
        return Math.min(Math.abs(currentFloor - pendingFloor), Math.abs(nextFloor - pendingFloor));
    }

    private int calculateFutureMovesChange(int futureMovesNumber, int currentMoveChange) {
        return futureMovesNumber * currentMoveChange;
    }

    private ElevatorMoveCalculatorHelper relaxResult(ElevatorMoveCalculatorHelper result,
                                                     int currentDifference,
                                                     Elevator elevator,
                                                     int index,
                                                     Flag flag) {
        if (result.difference() <= currentDifference) return result;
        return new ElevatorMoveCalculatorHelper(currentDifference, elevator, index, flag);

    }
}
