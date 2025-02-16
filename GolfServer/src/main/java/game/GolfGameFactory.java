package game;

import entity.Ball;
import entity.Hole;
import obstacle.Wall;

public class GolfGameFactory {
    public static GolfGame createSimpleGolfGame(){
        GolfMap map = new GolfMap(800, 600, new Hole(700, 550, 20));
        map.addObstacle(new Wall(100, 100, 200, 200));
        map.addObstacle(new Wall(200, 200, 300, 100));
        map.addObstacle(new Wall(300, 100, 400, 200));
        map.addObstacle(new Wall(400, 200, 500, 100));
        map.addObstacle(new Wall(500, 100, 600, 200));

        map.addObstacle(new Wall(250, 300, 250, 400));
        map.addObstacle(new Wall(450, 300, 450, 400));

        map.addObstacle(new Wall(100, 500, 300, 500));
        map.addObstacle(new Wall(400, 500, 600, 500));
        Ball[] balls = new Ball[]{
                new Ball(50, map.getHeight() / 2),
                new Ball(70, map.getHeight() / 2)
        };
//        GolfMap map = new GolfMap(800, 600, new Hole(700, 300, 10));
//        map.addObstacle(new Wall(10,100, 100,500));
//        map.addObstacle(new Wall(20,100, 700,100));
//        map.addObstacle(new Wall(300,100, 700,500));
//        map.addObstacle(new Wall(20,200, 700,500));
//        Ball[] balls = new Ball[]{
//                new Ball(50, map.getHeight() / 2),
//                new Ball(70, map.getHeight() / 2)
//        };
        return new GolfGame(map, balls, 3);

    }
}
