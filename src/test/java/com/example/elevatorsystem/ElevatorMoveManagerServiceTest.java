package com.example.elevatorsystem;

import com.example.elevatorsystem.models.Elevator;
import com.example.elevatorsystem.models.ElevatorMove;
import com.example.elevatorsystem.models.ElevatorMoveCalculatorHelper;
import com.example.elevatorsystem.models.Flag;
import com.example.elevatorsystem.services.ElevatorMoveCalculatorService;
import com.example.elevatorsystem.services.ElevatorMoveManagerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ElevatorMoveManagerServiceTest {

    @InjectMocks
    private ElevatorMoveManagerService elevatorMoveManagerService;

    @Mock
    private ElevatorMoveCalculatorService elevatorMoveCalculatorService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    void whenPromptedChangesCurrentMoveAndAddsItToBeginningOfPlannedMoves() {
        int pendingFloor = 5;

        Elevator passingElevator = new Elevator();
        passingElevator.setCurrentMove(6);

        Elevator otherElevator = new Elevator();
        otherElevator.setCurrentMove(1);

        ElevatorMove passingMove = new ElevatorMove(6, 0, passingElevator);

        ElevatorMoveCalculatorHelper helper = new ElevatorMoveCalculatorHelper(0, passingElevator,
                                                                                  0, Flag.CHANGE_CURRENT_MOVE);

        when(elevatorMoveCalculatorService.findOptimalElevator(pendingFloor)).thenReturn(helper);

        elevatorMoveManagerService.addNewElevatorMove(pendingFloor);

        assertEquals(passingElevator.getCurrentMove(), 5);
        assertEquals(passingElevator.getPlannedMoves(), List.of(passingMove));

        assertEquals(otherElevator.getCurrentMove(), 1);
        assertEquals(otherElevator.getPlannedMoves(), List.of());
    }
}