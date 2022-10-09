package com.example.elevatorsystem;

public enum Direction {
    UP(1),
    DOWN(-1);

    public final int value;

    Direction(int value) {
        this.value = value;
    }
}
