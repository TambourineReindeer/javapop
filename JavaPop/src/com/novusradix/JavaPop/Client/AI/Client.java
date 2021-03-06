package com.novusradix.JavaPop.Client.AI;

import com.novusradix.JavaPop.Messaging.Lobby.GameStarted;
import com.novusradix.JavaPop.Messaging.Lobby.JoinGame;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.*;

/**
 *
 * @author gef
 */
public class Client extends com.novusradix.JavaPop.Client.Client {

    public Client(int port, int gameId) {

        try {
            socket = new Socket("localhost", port);
            socket.setTcpNoDelay(true);
        } catch (UnknownHostException ex) {
            return;
        } catch (IOException ex) {
            return;
        }
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush();
            ois = new ObjectInputStream(socket.getInputStream());
            oos.writeBoolean(false); //indicate we're not human
            playerID = ois.readInt();
            (new Thread(this, "AI player")).start();
        } catch (IOException ioe) {
            return;
        }

        sendMessage(new JoinGame(gameId));

    }

    @Override
    public void newGame(GameStarted g) {
        if (lobby != null) {
            lobby.hide();
        }
        game = new Game(g, this);
    }

    @Override
    public void quit() {
        super.quit();
        game.kill();
    }
}
