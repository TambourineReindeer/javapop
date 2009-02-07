/*
 * This class handles the networking functions of the client on the server side. 
 * It also (somewhat akwardly) contains per player in-game data.
 */
package com.novusradix.JavaPop.Server;

import com.novusradix.JavaPop.Messaging.Lobby.GameList;
import com.novusradix.JavaPop.Messaging.Message;
import com.novusradix.JavaPop.Messaging.PlayerUpdate;
import java.awt.Point;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gef
 */
public class ServerPlayer implements Runnable, com.novusradix.JavaPop.Player {

    private static float[][] defaultColors = {{0, 0, 1}, {1, 0, 0}, {0, 1, 0}};
    boolean human;

    public enum PeonMode {

        SETTLE, ANKH, FIGHT, GROUP
    }
    public Server s;
    public Socket socket;
    public ServerGame currentGame;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private static int nextId = 1;
    private int id;
    public boolean ready = false;
    private String name;
    private int ankhx,  ankhy;
    private float[] colour;
    double houseMana;
    double peonMana;
    private double spentMana;
    private Point magnet;
    public PeonMode peonMode;

    public ServerPlayer(Server s, Socket sock) {
        this.s = s;
        this.socket = sock;
        id = nextId++;
        name = "Player " + id;
        magnet = new Point(0, 0);
        colour = defaultColors[id % 3];
        houseMana = 0;
        peonMana = 0;
        spentMana = 0;
        peonMode = PeonMode.SETTLE;
        try {
            socket.setTcpNoDelay(true);
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeInt(id);
            oos.flush();
            ois = new ObjectInputStream(socket.getInputStream());

            human = ois.readBoolean();
            (new Thread(this, "Server Player")).start();
            sendMessage(new GameList(s.getGames()));
        } catch (IOException ioe) {
            Logger.getLogger(ServerPlayer.class.getName()).log(Level.SEVERE, null, ioe);
        }
    }

    public void setPapalMagnet(int x, int y) {
        magnet.setLocation(x, y);
        currentGame.sendAllPlayers(new PlayerUpdate(this));
    }

    public int getId() {
        return id;
    }

    private Message getMessage() throws IOException, ClassNotFoundException //method only exists to sepearate out blocking call for profiling purposes
    {
        return (Message) ois.readObject();
    }

    public void run() {
        //Message loop
        try {

            Message message;
            do {
                message = getMessage();
                if (message != null) {
                    message.setServer(s, currentGame, this);
                    message.execute();
                }
            } while (message != null);
        } catch (IOException ioe) {
        } catch (ClassNotFoundException cnfe) {
        }

        if (currentGame != null) {
            currentGame.removePlayer(this);
        }
        s.removePlayer(this);
        try {
            socket.close();
        //Disconnect
        } catch (IOException ex) {
            Logger.getLogger(ServerPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public synchronized void sendMessage(Message m) {
        try {
            oos.writeObject(m);
            oos.flush();
            oos.reset();
        //System.out.println("Server sent " + m.getClass().getName());
        } catch (IOException ex) {
            Logger.getLogger(ServerPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    synchronized void sendMessage(byte[] bytes) {
        try {
            socket.getOutputStream().write(bytes);
            socket.getOutputStream().flush();
            oos.reset();
        } catch (IOException ex) {
            Logger.getLogger(ServerPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getName() {
        return name;
    }

    public float[] getColour() {
        return colour;
    }

    public Point getPapalMagnet() {
        return magnet;
    }

    public double getMana() {
        return peonMana + houseMana - spentMana;
    }
}
