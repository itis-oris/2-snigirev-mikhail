package ru.msnigirev.oris.golfclient;

import lombok.Getter;

@Getter
public class Ball {
    private double x;
    private double y;
    private double radius = 5;
    public void setLocation(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
