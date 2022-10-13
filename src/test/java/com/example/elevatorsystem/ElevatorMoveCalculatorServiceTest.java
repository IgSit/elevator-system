package com.example.elevatorsystem;

import com.example.elevatorsystem.models.Elevator;
import com.example.elevatorsystem.models.ElevatorMove;
import com.example.elevatorsystem.models.ElevatorMoveCalculatorHelper;
import com.example.elevatorsystem.services.ElevatorMoveCalculatorService;
import com.example.elevatorsystem.services.ElevatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


class ElevatorMoveCalculatorServiceTest {

    @InjectMocks
    private ElevatorMoveCalculatorService elevatorMoveCalculatorService;

    @Mock
    private ElevatorService elevatorService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void whenOneElevatorIsPassingByPendingFloorChoosesThatElevator() {
        int pendingFloor = 2;

        Elevator passingElevator = new Elevator();
        passingElevator.setCurrentMove(3);

        Elevator otherElevator = new Elevator();
        otherElevator.setCurrentFloor(4);
        otherElevator.setCurrentMove(5);

        List<Elevator> elevators = List.of(passingElevator, otherElevator);

        when(elevatorService.getElevators()).thenReturn(elevators);

        ElevatorMoveCalculatorHelper result = elevatorMoveCalculatorService.findOptimalElevator(pendingFloor);

        assertEquals(passingElevator, result.elevator());
    }

    @Test
    void whenOneElevatorIsPassingByAndOthersAreFreeChoosesClosestFreeElevator() {
        int pendingFloor = 2;

        Elevator passingElevator = new Elevator();
        passingElevator.setCurrentMove(3);

        Elevator freeElevator = new Elevator();
        freeElevator.setCurrentFloor(5);

        Elevator closestFreeElevator = new Elevator();
        closestFreeElevator.setCurrentFloor(1);

        List<Elevator> elevators = List.of(passingElevator, freeElevator, closestFreeElevator);

        when(elevatorService.getElevators()).thenReturn(elevators);

        ElevatorMoveCalculatorHelper result = elevatorMoveCalculatorService.findOptimalElevator(pendingFloor);

        assertEquals(closestFreeElevator, result.elevator());
    }

    @Test
    void whenOneElevatorIsPassingByButIsOverloadedChoosesLessOverloadedElevator() {
        int pendingFloor = 5;

        Elevator passingElevator = new Elevator();
        passingElevator.setCurrentMove(7);

        ElevatorMove passingMove1 = new ElevatorMove(4, 0, passingElevator);
        ElevatorMove passingMove2 = new ElevatorMove(15, 1, passingElevator);
        ElevatorMove passingMove3 = new ElevatorMove(3, 2, passingElevator);

        passingElevator.setPlannedMoves(List.of(passingMove1, passingMove2, passingMove3));

        Elevator otherElevator = new Elevator();
        otherElevator.setCurrentMove(2);

        ElevatorMove otherMove = new ElevatorMove(6, 0, otherElevator);

        otherElevator.setPlannedMoves(List.of(otherMove));

        List<Elevator> elevators = List.of(passingElevator, otherElevator, passingElevator);

        when(elevatorService.getElevators()).thenReturn(elevators);

        ElevatorMoveCalculatorHelper result = elevatorMoveCalculatorService.findOptimalElevator(pendingFloor);

        assertEquals(otherElevator, result.elevator());
    }
}