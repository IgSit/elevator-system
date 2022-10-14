package com.example.elevatorsystem;

import com.example.elevatorsystem.models.Elevator;
import com.example.elevatorsystem.models.ElevatorMove;
import com.example.elevatorsystem.services.ElevatorMoveManagerService;
import com.example.elevatorsystem.services.ElevatorMoveService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ElevatorTest {

    @Mock
    ElevatorMoveManagerService moveManagerService;

    @Mock
    ElevatorMoveService moveService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void step_EmptyElevator_DoesNothing() {
        Elevator elevator = new Elevator();
        elevator.setCurrentFloor(3);

        elevator.step(moveManagerService);

        assertEquals(3, elevator.getCurrentFloor());
    }

    @Test
    void step_FinishingCurrentMoveElevator_PicksFirstPlannedMove() {
        int currentFloor = 2;
        int currentMoveFloor = 3;
        int plannedMoveFloor = 5;

        Elevator elevator = new Elevator();
        elevator.setCurrentFloor(currentFloor);
        elevator.setCurrentMove(currentMoveFloor);

        ElevatorMove plannedMove = new ElevatorMove(plannedMoveFloor, 0, elevator);

        elevator.setPlannedMoves(List.of(plannedMove));

        when(moveManagerService.popFirstMove(elevator.getId())).thenReturn(plannedMove);

        elevator.step(moveManagerService);

        assertEquals(currentMoveFloor, elevator.getCurrentFloor());
        assertEquals(plannedMoveFloor, elevator.getCurrentMove());
    }

    @Test
    void addMove_FreeElevator_SetsCurrentMove() {
        int moveFloor = 4;

        Elevator elevator = new Elevator();

        ElevatorMove move = new ElevatorMove(moveFloor, 0, elevator);

        elevator.addMove(move, moveService);

        assertEquals(moveFloor, elevator.getCurrentMove());
    }

    @Test
    void isBusy_BusyElevator_true() {
        Elevator elevator = new Elevator();
        elevator.setCurrentMove(4);

        boolean result = elevator.isBusy();

        assertTrue(result);
    }

    @Test
    void isFree_FreeElevator_true() {
        Elevator elevator = new Elevator();

        boolean result = elevator.isFree();

        assertTrue(result);
    }

    @Test
    void passesBy_SameDirectionElevator_true() {
        int pendingFloor = 5;

        Elevator elevator = new Elevator();
        elevator.setCurrentMove(6);

        assertTrue(elevator.passesBy(pendingFloor));
    }

    @Test
    void passesBy_FreeElevator_ThrowsException() {
        int pendingFloor = 3;

        Elevator elevator = new Elevator();

        assertThrows(RuntimeException.class, () -> elevator.passesBy(pendingFloor));
    }
}