package com.example.elevatorsystem.services;

import com.example.elevatorsystem.models.Elevator;
import com.example.elevatorsystem.models.ElevatorMove;
import com.example.elevatorsystem.models.ElevatorMoveCalculatorHelper;
import com.example.elevatorsystem.models.Flag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ElevatorMoveCalculatorService {

    private final ElevatorService elevatorService;

    @Autowired
    public ElevatorMoveCalculatorService(ElevatorService elevatorService) {
        this.elevatorService = elevatorService;
    }

    public ElevatorMoveCalculatorHelper findOptimalElevator(int pendingFloor) {
        List<Elevator> elevators = elevatorService.getElevators();
        ElevatorMoveCalculatorHelper result = new ElevatorMoveCalculatorHelper(Integer.MAX_VALUE, null, 0, null);
        ElevatorMoveCalculatorHelper freeOrPassingByHelper = findFreeOrPassingByElevator(elevators, pendingFloor);
        if (freeOrPassingByHelper != null) result = freeOrPassingByHelper;
        ElevatorMoveCalculatorHelper plannedHelper = findBestPlannedElevator(elevators, pendingFloor);
        if (plannedHelper != null && plannedHelper.difference() < result.difference()) result = plannedHelper;
        return result;
    }

    protected ElevatorMoveCalculatorHelper findFreeOrPassingByElevator(List<Elevator> elevators, int pendingFloor) {
        ElevatorMoveCalculatorHelper freeElevator = findClosestFreeElevator(elevators, pendingFloor);
        ElevatorMoveCalculatorHelper passingByElevator = findPassingByElevator(elevators, pendingFloor);
        if (freeElevator != null && passingByElevator != null) {
            if (freeElevator.difference() < passingByElevator.difference()) return freeElevator;
            return passingByElevator;
        }
        if (freeElevator != null) return freeElevator;
        return passingByElevator;
    }

    protected ElevatorMoveCalculatorHelper findBestPlannedElevator(List<Elevator> elevators, int pendingFloor) {
        ElevatorMoveCalculatorHelper result = findInPlannedMoves(elevators.get(0), pendingFloor);
        for (Elevator elevator: elevators) {
            ElevatorMoveCalculatorHelper helper = findInPlannedMoves(elevator, pendingFloor);
            if (helper != null && result != null && helper.difference() < result.difference()) result = helper;
        }
        return result;
    }

    protected ElevatorMoveCalculatorHelper findInPlannedMoves(Elevator elevator, int pendingFloor) {
        if (elevator.getPlannedMoves().isEmpty()) return null;
        int movesSize = elevator.getPlannedMoves().size();
        int differenceAsFirstMove = calculateMoveImpact(elevator.getCurrentFloor(), elevator.getCurrentMove(), pendingFloor) * calculateFutureMovesImpact(movesSize);
        System.out.println("Planned");
        System.out.println(elevator.getId());
        System.out.println(differenceAsFirstMove);
        ElevatorMoveCalculatorHelper result = new ElevatorMoveCalculatorHelper(differenceAsFirstMove, elevator, 0, Flag.ADD_TO_PLANNED_MOVES);
        List<ElevatorMove> moves = elevator.getPlannedMoves();
        for (int i = 1; i < moves.size(); i++) {
            int moveDifference = calculateMoveImpact(moves.get(i - 1).getFloor(), moves.get(i).getFloor(), pendingFloor);
            int futureMovesSize = movesSize - i;
            moveDifference += moveDifference * calculateFutureMovesImpact(futureMovesSize);
            System.out.println(i);
            System.out.println(moveDifference);
            if (moveDifference < result.difference())
                result = new ElevatorMoveCalculatorHelper(moveDifference, elevator, i, Flag.ADD_TO_PLANNED_MOVES);
        }
        return result;
    }

    protected ElevatorMoveCalculatorHelper findClosestFreeElevator(List<Elevator> elevators, int pendingFloor) {
        Optional<Elevator> optionalElevator = elevators.stream().filter(Elevator::isFree)
                .min(Comparator.comparing(e ->
                    Math.abs(e.getCurrentFloor() - pendingFloor)
                ));
        System.out.println("Free");
        return returnNullOrElevatorHelper(optionalElevator, pendingFloor, Flag.ADD_CURRENT_MOVE);
    }

    public ElevatorMoveCalculatorHelper findPassingByElevator(List<Elevator> elevators, int pendingFloor) {
        ElevatorMoveCalculatorHelper result = new ElevatorMoveCalculatorHelper(Integer.MAX_VALUE, null, 0, null);
        for (Elevator elevator: elevators) {
            if (elevator.isBusy() && elevator.hasSameDirection(pendingFloor)) {
                int currentChange = calculateMoveImpact(elevator.getCurrentFloor(), elevator.getCurrentMove(), pendingFloor) *
                        calculateFutureMovesImpact(elevator.getPlannedMoves().size());
                if (currentChange < result.difference()) {
                    result = new ElevatorMoveCalculatorHelper(currentChange, elevator, 0, Flag.CHANGE_CURRENT_MOVE);
                }
            }
        }
        return result;
    }

    private ElevatorMoveCalculatorHelper returnNullOrElevatorHelper(Optional<Elevator> optionalElevator, int pendingFloor, Flag flag) {
        if (optionalElevator.isPresent()) {
            Elevator elevator = optionalElevator.get();
            int difference = Math.abs(elevator.getCurrentFloor() - pendingFloor);
            System.out.println(elevator);
            System.out.println(difference);
            return new ElevatorMoveCalculatorHelper(difference, elevator, 0, flag);
        }
        return null;
    }

    private int calculateMoveImpact(int currentFloor, int currentMove, int pendingFloor) {
        return Math.abs(currentFloor - pendingFloor) + Math.abs(pendingFloor - currentMove);
    }

    private int calculateFutureMovesImpact(int size) {
        return (size + 1) * (size + 2) / 2;
    }
}
