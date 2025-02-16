package server;

import game.GolfGameFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GolfServer {
    private final static int port = 9999;
    private List<GameSession> sessions;
    private ServerSocket server;
    private boolean running;

    public void start() throws IOException {
        sessions = new ArrayList<>();
        server = new ServerSocket(port);
        running = true;
        new Thread(() -> {
            while (running) {
                try {
                    Socket socket = server.accept();
                    System.out.println("New client connected: " + socket);

                    ClientHandler clientHandler = new ClientHandler(socket);
                    GameSession tempSession = null;
                    for (GameSession session : sessions) {
                        if (session.addClient(clientHandler)) {
                            tempSession = session;
                            break;
                        }
                    }
                    if (tempSession == null) {
                        tempSession = new GameSession(GolfGameFactory.createSimpleGolfGame());
                        tempSession.addClient(clientHandler);
                        sessions.add(tempSession);
                    }
                    clientHandler.setSession(tempSession);
                    clientHandler.start();
                } catch (IOException e) {
                    if (running) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void stop() throws IOException {
        running = false;
        if (server != null && !server.isClosed()) {
            server.close();
        }
        for (GameSession gs : sessions) {
            if (!gs.isClosed()) {
                gs.close();
            }
        }
        sessions.clear();
        System.out.println("Server stopped.");
    }
}
