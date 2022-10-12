package com.example.elevatorsystem;

import com.example.elevatorsystem.models.Elevator;
import com.example.elevatorsystem.models.ElevatorMoveCalculatorHelper;
import com.example.elevatorsystem.services.ElevatorMoveCalculatorService;
import com.example.elevatorsystem.services.ElevatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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

//    @Test
//    void whenOneIsGoingToPassByInPlannedMovesButOtherFinishesMuchEarlierChoosesTheElevatorThatFinishesEarlier() {
//        int pendingFloor = 7;
//
//        Elevator passingElevator = new Elevator();
//        passingElevator.setCurrentMove(3);
//
//        ElevatorMove passingMove1 = new ElevatorMove(5, 0, passingElevator);
//        ElevatorMove passingMove2 = new ElevatorMove(2, 1, passingElevator);
//        ElevatorMove passingMove3 = new ElevatorMove(8, 2, passingElevator);
//
//        passingElevator.addMove(passingMove1);
//        passingElevator.addMove(passingMove2);
//        passingElevator.addMove(passingMove3);  // time to reach floor 7: 5 + 3 + 5 = 13
//
//        Elevator earlyFinishedElevator = new Elevator();
//        earlyFinishedElevator.setCurrentMove(2);
//
//        ElevatorMove earlyMove1 = new ElevatorMove(4, 0, earlyFinishedElevator);
//        ElevatorMove earlyMove2 = new ElevatorMove(6, 1, earlyFinishedElevator);
//
//        earlyFinishedElevator.addMove(earlyMove1);
//        earlyFinishedElevator.addMove(earlyMove2);  // time to reach floor 7: 4 + 2 + 1 = 7
//
//        List<Elevator> elevators = List.of(earlyFinishedElevator, passingElevator);
//
//        when(elevatorService.getElevators()).thenReturn(elevators);
//
//        ElevatorMoveCalculatorHelper result = elevatorMoveCalculatorService.findOptimalElevator(pendingFloor);
//
//        assertEquals(earlyFinishedElevator, result.elevator());
//    }
}