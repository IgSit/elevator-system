package com.example.elevatorsystem.services;

import com.example.elevatorsystem.models.Elevator;
import com.example.elevatorsystem.models.ElevatorMove;
import com.example.elevatorsystem.models.ElevatorMoveCalculatorHelper;
import com.example.elevatorsystem.models.AddMoveHandler;
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

        ElevatorMoveCalculatorHelper result = getBiggestDifferenceHelper();

        ElevatorMoveCalculatorHelper freeHelper = findClosestFreeElevator(elevators, pendingFloor);

        if (freeHelper.difference() < result.difference()) result = freeHelper;

        ElevatorMoveCalculatorHelper passingByHelper = findPassingByElevator(elevators, pendingFloor);

        if(passingByHelper.difference() < result.difference()) result = passingByHelper;

        ElevatorMoveCalculatorHelper plannedHelper = findBestPlannedElevator(elevators, pendingFloor);

        if (plannedHelper.difference() < result.difference()) result = plannedHelper;

        return result;
    }

    private ElevatorMoveCalculatorHelper findBestPlannedElevator(List<Elevator> elevators, int pendingFloor) {
        ElevatorMoveCalculatorHelper result = getBiggestDifferenceHelper();

        for (Elevator elevator : elevators) {
            if (elevator.isFree()) continue;
            ElevatorMoveCalculatorHelper helper = findInPlannedMoves(elevator, pendingFloor);
            System.out.println(helper.difference());
            if (helper.difference() < result.difference())
                result = helper;
        }
        return result;
    }

    private ElevatorMoveCalculatorHelper findInPlannedMoves(Elevator elevator, int pendingFloor) {
        if (elevator.getPlannedMoves().isEmpty()) {
            int difference = Math.abs(elevator.getCurrentMove() - pendingFloor)
                             + Math.abs(elevator.getCurrentMove() - elevator.getCurrentFloor());
            return new ElevatorMoveCalculatorHelper(
                    difference,
                    elevator,
                    0,
                    AddMoveHandler.ADD_TO_PLANNED_MOVES
            );
        }

        int movesSize = elevator.getPlannedMoves().size();

        int differenceAsFirstMove = calculateMoveImpact(
                elevator.getCurrentFloor(),
                elevator.getCurrentMove(),
                pendingFloor) * calculateFutureMovesImpact(movesSize
        );

        ElevatorMoveCalculatorHelper result = new ElevatorMoveCalculatorHelper(
                differenceAsFirstMove,
                elevator,
                0,
                AddMoveHandler.ADD_TO_PLANNED_MOVES
        );

        List<ElevatorMove> moves = elevator.getPlannedMoves();

        for (int i = 1; i < moves.size(); i++) {
            int moveDifference = calculateMoveImpact(
                    moves.get(i - 1).getFloor(),
                    moves.get(i).getFloor(),
                    pendingFloor
            );

            int futureMovesSize = movesSize - i;

            moveDifference += moveDifference * calculateFutureMovesImpact(futureMovesSize);

            if (moveDifference < result.difference())
                result = new ElevatorMoveCalculatorHelper(
                        moveDifference,
                        elevator,
                        i,
                        AddMoveHandler.ADD_TO_PLANNED_MOVES
                );
        }
        return result;
    }

    private ElevatorMoveCalculatorHelper findClosestFreeElevator(List<Elevator> elevators, int pendingFloor) {
        ElevatorMoveCalculatorHelper result = getBiggestDifferenceHelper();

        Optional<Elevator> optionalElevator = elevators
                .stream()
                .filter(Elevator::isFree)
                .min(Comparator.comparing(e ->
                        Math.abs(e.getCurrentFloor() - pendingFloor)
                ));

        if (optionalElevator.isPresent()) {
            Elevator elevator = optionalElevator.get();

            result = new ElevatorMoveCalculatorHelper(
                    Math.abs(elevator.getCurrentFloor() - pendingFloor),
                    elevator,
                    0,
                    AddMoveHandler.ADD_CURRENT_MOVE
            );
        }
        return result;
    }

    private ElevatorMoveCalculatorHelper findPassingByElevator(List<Elevator> elevators, int pendingFloor) {
        ElevatorMoveCalculatorHelper result = getBiggestDifferenceHelper();

        for (Elevator elevator : elevators) {
            if (elevator.isBusy() && elevator.passesBy(pendingFloor)) {

                int currentChange = calculateMoveImpact(
                        elevator.getCurrentFloor(),
                        elevator.getCurrentMove(),
                        pendingFloor) * calculateFutureMovesImpact(elevator.getPlannedMoves().size());

                if (currentChange < result.difference())
                    result = new ElevatorMoveCalculatorHelper(
                            currentChange,
                            elevator,
                            0,
                            AddMoveHandler.CHANGE_CURRENT_MOVE
                    );
            }
        }
        return result;
    }

    private int calculateMoveImpact(int currentFloor, int currentMove, int pendingFloor) {
        return Math.abs(currentFloor - pendingFloor) + Math.abs(pendingFloor - currentMove);
    }

    private int calculateFutureMovesImpact(int size) {
        return (size + 1) * (size + 2) / 2;
    }

    private ElevatorMoveCalculatorHelper getBiggestDifferenceHelper() {
        int resultDifference = Integer.MAX_VALUE;
        Elevator resultElevator = null;
        int moveIndex = 0;
        AddMoveHandler addMoveHandler = null;

        return new ElevatorMoveCalculatorHelper(
                resultDifference,
                resultElevator,
                moveIndex,
                addMoveHandler
        );
    }
}
