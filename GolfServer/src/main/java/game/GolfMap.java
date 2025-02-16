package game;

import entity.Hole;
import lombok.Getter;
import obstacle.Obstacle;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GolfMap {
    private final double width;
    private final double height;
    private final Hole hole;
    private final List<Obstacle> obstacles;
    public GolfMap(double width, double height, Hole hole) {
        this.width = width;
        this.height = height;
        this.hole = hole;
        this.obstacles = new ArrayList<>();
    }

    public void addObstacle(Obstacle obstacle) {
        obstacles.add(obstacle);
    }
}
