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
    private Integer currentMove;
    @OneToMany(mappedBy = "elevator")
    private List<ElevatorMove> plannedMoves;

    public Elevator() {
        currentFloor = 0;
        currentMove = null;
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

    public boolean passesBy(int pendingFloor) throws RuntimeException {
        if (isFree()) throw new RuntimeException("Elevator has no current move");
        return (currentFloor <= pendingFloor && pendingFloor <= currentMove) ||
                (currentFloor >= pendingFloor && pendingFloor >= currentMove);
    }

    public Long getId() {
        return id;
    }

    public Integer getCurrentFloor() {
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

    public void setPlannedMoves(List<ElevatorMove> plannedMoves) {
        this.plannedMoves = plannedMoves;
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
        return isBusy() && currentMove > currentFloor;
    }

    private boolean isGoingDown() {
        return isBusy() && currentMove < currentFloor;
    }

    private boolean shouldFinishCurrentMove() {
        if (isFree()) return false;
        return currentFloor == currentMove;
    }

    private void finishCurrentMove(ElevatorMoveManagerService moveManagerService) {
        if (plannedMoves.isEmpty()) currentMove = null;
        else {
            currentMove = moveManagerService.popFirstMove(this.id).getFloor();
        }
    }
}
