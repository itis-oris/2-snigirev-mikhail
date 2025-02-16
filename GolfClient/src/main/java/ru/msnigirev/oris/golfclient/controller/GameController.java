package ru.msnigirev.oris.golfclient.controller;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import lombok.Setter;
import ru.msnigirev.oris.golfclient.Ball;
import ru.msnigirev.oris.golfclient.Game;
import ru.msnigirev.oris.golfclient.Hole;
import ru.msnigirev.oris.golfclient.Player;
import ru.msnigirev.oris.golfclient.obstactle.Obstacle;
import ru.msnigirev.oris.golfclient.server.NetworkClient;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class GameController implements Initializable {
    @FXML
    private Canvas gameCanvas;
    @FXML
    private ProgressBar powerBar;

    private double mouseX;
    private double mouseY;
    private double powerValue = 0.0;
    private final double powerSpeed = 0.5;
    private final static double MAX_POWER = 300.0d;

    private GraphicsContext gc;

    @Setter
    private NetworkClient networkClient;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gc = gameCanvas.getGraphicsContext2D();

        gameCanvas.setOnMouseMoved(e -> {
            mouseX = e.getX();
            mouseY = e.getY();
        });

        gameCanvas.setFocusTraversable(true);
        gameCanvas.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE) {
                onHit();
            }
        });

        startAnimation();
    }

    private boolean gameIsEnded(){
        Game game = networkClient.getGame();
        if (game == null) {
            return false;
        }
        return game.isEnded();
    }
    private void startAnimation() {
        AnimationTimer timer = new AnimationTimer() {
            long prevTime = 0;
            @Override
            public void handle(long now) {
                if (gameIsEnded()) {
                    gameOver();
                    this.stop();
                    return;
                }
                if (prevTime == 0) {
                    prevTime = now;
                    return;
                }
                double deltaTime = (now - prevTime) / 1_000_000_000.0;
                prevTime = now;
                updatePowerBar(deltaTime);

                render();
            }
        };
        timer.start();
    }

    private void updatePowerBar(double dt) {
        powerValue += powerSpeed * dt;
        if (powerValue > 1.0) {
            powerValue = 0.0;
        }
        powerBar.setProgress(powerValue);
    }

    private void onHit() {
        double power = powerBar.getProgress() * MAX_POWER;
        if (networkClient != null) {
            networkClient.shot(mouseX, mouseY, power);
        }
    }

    private void render() {
        gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
        Game game = networkClient.getGame();
        if (game == null || !game.isFull()) {
            gc.setFill(Color.GRAY);
            gc.fillText("Waiting for server...", 50, 50);
            return;
        }

        gc.setFill(Color.LIGHTGREEN);
        gc.fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        draw(game);
    }

    private void drawObstacles(Game game) {
        for (Obstacle obstacle : game.getObstacles()) {
            obstacle.write(gc);
        }

    }

    private void drawHole(Game game) {
        Hole hole = game.getHole();
        if (hole == null) return;

        double hx = hole.getCenterX();
        double hy = hole.getCenterY();
        double hr = hole.getRadius();

        gc.setFill(Color.BLACK);
        gc.fillOval(hx - hr, hy - hr, hr*2, hr*2);
    }

    private void drawBalls(Game game) {
        List<Ball> balls = game.getBalls();
        if (balls == null) return;

        for (int i = 0; i < balls.size(); i++) {
            double bx = balls.get(i).getX();
            double by = balls.get(i).getY();
            double r = balls.get(i).getRadius();

            if (i == 0) {
                gc.setFill(Color.WHITE);
            } else {
                gc.setFill(Color.RED);
            }
            gc.fillOval(bx - r, by - r, 2*r, 2*r);
        }
    }

    private void drawDirection(Game game) {
        if (!networkClient.isPlaying()) return;

        Ball ball = game.getBalls().get(game.getThisPlayerId());


        double length = 50;
        double angle = Math.atan2(mouseY - ball.getY(), mouseX - ball.getX());
        double x2 = ball.getX() + Math.cos(angle) * length;
        double y2 = ball.getY() + Math.sin(angle) * length;

        gc.setStroke(Color.BROWN);
        gc.setLineWidth(4);
        gc.strokeLine(ball.getX(), ball.getY(), x2, y2);
    }
    private void gameOver(){
        if (networkClient.getGame().getWinnerIndex() == networkClient.getGame().getThisPlayerId()) {
            openTheFxml("/view/winner.fxml");
            networkClient.disconnect();
        } else {
            openTheFxml("/view/loser.fxml");
            networkClient.disconnect();
        }
    }
    private void drawPlayersList(Game game) {
        gc.setFill(Color.BLACK);           // цвет текста
        gc.setFont(new Font(16));          // шрифт и размер
        List<Player> players = game.getPlayers();
        int currentPlayerId = game.getCurrentPlayerId();

        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            // Если этот игрок — текущий, добавим "-> " для наглядности
            String prefix = (i == currentPlayerId) ? "-> " : "";
            String text = prefix + p.getName() + ": " + p.getScore();

            // Координаты, где будет рисоваться строка
            double x = 10;
            double y = 20 + i * 20;  // каждая строка смещается на 20px вниз

            gc.fillText(text, x, y);
        }
    }
    private void openTheFxml(String fxml){
        Stage currentStage = (Stage) gameCanvas.getScene().getWindow();
        currentStage.close();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Конец игры для " + networkClient.getGame().getPlayers().get(networkClient.getGame().getThisPlayerId()).getName());
        stage.show();
    }
    private void draw(Game game) {
        drawObstacles(game);

        drawHole(game);

        drawBalls(game);

        drawDirection(game);

        drawPlayersList(game);
    }
}
