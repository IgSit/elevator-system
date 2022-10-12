package com.example.elevatorsystem.services;

import com.example.elevatorsystem.models.ElevatorMove;
import com.example.elevatorsystem.repositories.ElevatorMoveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

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

    public List<ElevatorMove> getElevatorMovesByElevatorId(Long elevatorId) {
        return moveRepository.getElevatorMovesByElevatorId(elevatorId);
    }

    public ElevatorMove getElevatorMoveAtIndex(Long elevatorId, int index) {
        return moveRepository.getElevatorMovesByElevatorId(elevatorId).get(index);
    }

    public void removeElevatorMoveAtIndex(Long elevatorId, int index) {
        moveRepository.removeElevatorMoveAtIndex(elevatorId, index);
    }

    public void updateFutureElevatorMoves(Long elevatorId, int startingIndex, int value) {
        Stream<ElevatorMove> futureElevatorMoves = getElevatorMovesByElevatorId(elevatorId)
                .stream().filter(em -> em.getIndex() >= startingIndex);
        futureElevatorMoves.forEach(em -> em.setIndex(em.getIndex() + value));
    }
}
