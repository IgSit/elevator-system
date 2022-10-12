package com.example.elevatorsystem.models;

import com.example.elevatorsystem.services.ElevatorMoveManagerService;
import com.example.elevatorsystem.services.ElevatorMoveService;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "elevators")
public class Elevator {
    @Id
    @SequenceGenerator(
            name = "elevator_sequence",
            sequenceName = "elevator_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "elevator_sequence"
    )
    private Long id;
    private int currentFloor;
    private int currentMove;
    @OneToMany(mappedBy = "elevator")
    private final List<ElevatorMove> plannedMoves;

    public Elevator() {
        currentFloor = 0;
        currentMove = -1;
        plannedMoves = new ArrayList<>() {
        };
    }

    public void step(ElevatorMoveManagerService moveManagerService) {
        if (isFree()) return;
        if (isGoingUp()) currentFloor++;
        else currentFloor--;

        if (shouldFinishCurrentMove()) finishCurrentMove(moveManagerService);
    }

    public void addMove(ElevatorMove move, ElevatorMoveService moveService) {
        if (isFree()) currentMove = move.getFloor();
        else {
            moveService.updateFutureElevatorMoves(this.id, move.getIndex(), 1);
            moveService.addElevatorMove(move);
        }
    }

    public boolean isBusy() {
        return currentMove != -1;
    }

    public boolean isFree() {
        return !isBusy();
    }

    public boolean hasSameDirection(int pendingFloor) throws RuntimeException {
        if (isFree()) throw new RuntimeException("Elevator has no current move");
        return (currentFloor <= pendingFloor && pendingFloor <= currentMove) ||
                (currentFloor >= pendingFloor && pendingFloor >= currentMove);
    }

    public boolean hasOppositeDirection(int pendingFloor) throws RuntimeException{
        return !hasSameDirection(pendingFloor);
    }

    public Long getId() {
        return id;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }

    public int getCurrentMove() {
        return currentMove;
    }

    public void setCurrentMove(int currentMove) {
        this.currentMove = currentMove;
    }

    public List<ElevatorMove> getPlannedMoves() {
        return plannedMoves;
    }

    @Override
    public String toString() {
        return "Elevator{" +
                "id=" + id +
                ", currentFloor=" + currentFloor +
                ", currentMove=" + currentMove +
                ", plannedMoves=" + plannedMoves.stream().sorted(Comparator.comparingInt(ElevatorMove::getIndex))
                                    .map(ElevatorMove::getFloor).toList() +
                '}';
    }

    private boolean isGoingUp() {
        return currentMove != -1 && currentMove > currentFloor;
    }

    private boolean isGoingDown() {
        return currentMove != -1 && currentMove < currentFloor;
    }

    private boolean shouldFinishCurrentMove() {
        if (isFree()) return false;
        return currentFloor == currentMove;
    }

    private void finishCurrentMove(ElevatorMoveManagerService moveManagerService) {
        if (plannedMoves.isEmpty()) currentMove = -1;
        else {
            currentMove = moveManagerService.popFirstMove(this.id).getFloor();
        }
    }
}
