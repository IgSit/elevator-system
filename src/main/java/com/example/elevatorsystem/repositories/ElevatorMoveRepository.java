package com.example.elevatorsystem.repositories;

import com.example.elevatorsystem.models.ElevatorMove;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElevatorMoveRepository extends JpaRepository<ElevatorMove, Long> {

    @Query("select em from ElevatorMove em where em.elevator.id = ?1 order by em.indexOrder")
    List<ElevatorMove> getElevatorMovesByElevatorId(Long elevatorId);

    @Modifying
    @Query("delete from ElevatorMove em where em.elevator.id = ?1 and em.indexOrder = ?2")
    void removeElevatorMoveAtIndex(Long elevatorId, int index);
}
