package ru.msnigirev.oris.golfclient.obstactle;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Wall implements Obstacle {
    private double x1, y1, x2, y2;

    public Wall(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    @Override
    public void write(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3);
        gc.strokeLine(x1, y1, x2, y2);
    }
}
