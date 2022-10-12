package com.example.elevatorsystem.services;

import com.example.elevatorsystem.models.ElevatorMove;
import com.example.elevatorsystem.repositories.ElevatorMoveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ElevatorMoveService {

    private final ElevatorMoveRepository moveRepository;

    @Autowired
    public ElevatorMoveService(ElevatorMoveRepository moveRepository) {
        this.moveRepository = moveRepository;
    }

    public void addElevatorMove(ElevatorMove move) {
        moveRepository.save(move);
    }

    public List<ElevatorMove> getElevatorMoves() {
        return moveRepository.findAll();
    }
}
