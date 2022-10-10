package com.example.elevatorsystem;

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
    @Column(name = "indexOrder", nullable = false)
    private int indexOrder;

    public ElevatorMove(int floor, int indexOrder) {
        this.floor = floor;
        this.indexOrder = indexOrder;
    }

    public ElevatorMove() {
    }

    public int getFloor() {
        return floor;
    }

    public int getIndex() {
        return indexOrder;
    }

    public void setIndex(int index) {
        this.indexOrder = index;
    }
}
