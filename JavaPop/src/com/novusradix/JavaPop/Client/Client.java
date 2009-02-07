/*
 * This class performs the network functions of the local client.
 */
package com.novusradix.JavaPop.Client;

import com.novusradix.JavaPop.Server.ServerPlayer.PeonMode;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.novusradix.JavaPop.Client.Lobby.Lobby;
import com.novusradix.JavaPop.Messaging.Lobby.Bye;
import com.novusradix.JavaPop.Messaging.Lobby.GameStarted;
import com.novusradix.JavaPop.Messaging.Message;
import com.novusradix.JavaPop.Messaging.Tools.SetBehaviour;
import java.net.SocketException;

/**
 *
 * @author gef
 */
public class Client implements Runnable {

    protected Socket socket;
    protected ObjectInputStream ois;
    protected ObjectOutputStream oos;
    private boolean connected;
    public Lobby lobby;
    public Game game;
    protected int playerID;
    public PeonMode behaviour;

    protected Client() {
    }

    public Client(String host, Lobby l) {
        lobby = l;
        connected = false;

        try {
            socket = new Socket(host, 13579);
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
            oos.writeBoolean(true);// indicate we're human
            oos.flush();
            playerID = ois.readInt();

            connected = true;
            (new Thread(this, "Client Player")).start();
        } catch (IOException ioe) {
            return;
        }
    }

    public int getPlayerID() {
        return playerID;
    }

    public boolean isConnected() {
        return connected;
    }

    public void run() {
        Message message;
        try {
            while (socket.isConnected()) {
                message = getMessage();
                message.setClient(this);

                message.execute();
            }
        } catch (SocketException ex) {//disconnected
        } catch (IOException ioe) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ioe);
        } catch (ClassNotFoundException cnfe) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, cnfe);
        }

    }

    private Message getMessage() throws IOException, ClassNotFoundException //method only exists to separate out blocking call for profiling purposes
    {
        return (Message) ois.readObject();
    }

    public synchronized void sendMessage(Message m) {
        try {
            oos.writeObject(m);
            oos.flush();
            oos.reset();
        //System.out.println("Client sending " + m.getClass().getSimpleName());
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void newGame(GameStarted g) {
        if (lobby != null) {
            lobby.hide();
        }
        game = new Game(g, this);
    }

    public void quit() {
        sendMessage(new Bye());
        try {
            socket.close();
        } catch (IOException ex) {
            //probably already quit...
        }
    }

    public void setBehaviour(PeonMode m) {
        behaviour = m;
        sendMessage(new SetBehaviour(m));
    }
}