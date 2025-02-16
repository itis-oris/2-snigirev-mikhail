package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import entity.Player;
import lombok.Getter;
import lombok.Setter;


public class ClientHandler extends Thread {
    @Setter@Getter
    private int clientId;
    private final Socket socket;
    @Setter
    private GameSession session;
    private PrintWriter out;
    private BufferedReader in;
    @Setter@Getter
    private Player player;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                session.handleMessage(line, this);
            }
        } catch (IOException e) {
            System.out.println("Client disconnected: " + socket);
        } finally {
            close();
        }
    }


    public void close() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String msg) {
        if (out != null) {
            out.println(msg);
        }
    }
}
