package com.example.elevatorsystem;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

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
    private final Set<ElevatorMove> plannedMoves;

    public Elevator() {
        currentFloor = 0;
        currentMove = -1;
        plannedMoves = new HashSet<>() {
        };
    }

    public void step() {
        if (isFree()) return;
        if (isGoingUp()) currentFloor++;
        else currentFloor--;

        if (shouldFinishCurrentMove()) finishCurrentMove();
    }

    public void addMove(ElevatorMove move) {
        if (isFree()) currentMove = move.getFloor();
        else plannedMoves.add(move);
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

    public Set<ElevatorMove> getPlannedMoves() {
        return plannedMoves;
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

    private void finishCurrentMove() {
        if (plannedMoves.isEmpty()) currentMove = -1;
    }
}
