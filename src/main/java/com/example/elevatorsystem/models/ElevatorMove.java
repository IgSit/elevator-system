package com.example.elevatorsystem.models;

import javax.persistence.*;

@Entity
@Table(name = "elevator_moves")
public class ElevatorMove {
    @Id
    @SequenceGenerator(
            name = "elevator_move_sequence",
            sequenceName = "elevator_move_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "elevator_move_sequence"
    )
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "elevator_id")
    private Elevator elevator;
    @Column(name = "floor", nullable = false)
    private int floor;
    @Column(name = "index_order", nullable = false)
    private int indexOrder;

    public ElevatorMove(int floor, int indexOrder, Elevator elevator) {
        this.floor = floor;
        this.indexOrder = indexOrder;
        this.elevator = elevator;
    }

    public ElevatorMove() {
    }

    public int getFloor() {
        return floor;
    }

    public int getIndex() {
        return indexOrder;
    }

    public Elevator getElevator() {
        return elevator;
    }

    public void setIndex(int index) {
        this.indexOrder = index;
    }

    @Override
    public String toString() {
        return "ElevatorMove{" +
                "id=" + id +
                ", elevatorID=" + elevator.getId() +
                ", floor=" + floor +
                ", indexOrder=" + indexOrder +
                '}';
    }
}
