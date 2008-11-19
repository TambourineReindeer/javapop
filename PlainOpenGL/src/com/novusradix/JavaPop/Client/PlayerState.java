/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Client;

import com.novusradix.JavaPop.GamePanel;
import com.novusradix.JavaPop.GamesPanel;
import com.novusradix.JavaPop.Messaging.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mom
 */
public class PlayerState implements Runnable {

    private int mana;
    public BaseTool currentTool;
    private Socket socket;
    public GamesPanel gamesPanel;
    public GamePanel gamePanel;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    public PlayerState(String host, GamesPanel gsp, GamePanel gp) {
        gamesPanel = gsp;
        gamePanel = gp;

        try {

            socket = new Socket(host, 13579);
        } catch (UnknownHostException ex) {
            Logger.getLogger(PlayerState.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PlayerState.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush();
            ois = new ObjectInputStream(socket.getInputStream());
            (new Thread(this, "Client Player")).start();
        } catch (IOException ioe) {
        }
    }

    public void run() {
        Message message;
        try {
            while (socket.isConnected()) {
                message = (Message) ois.readObject();
                message.playerState = this;

                message.execute();
            }
        } catch (IOException ioe) {
        } catch (ClassNotFoundException cnfe) {
        }
    //disconnected
        gamesPanel.setGames(null);
        gamePanel.setGame(null);
    }

    public synchronized void sendMessage(Message m) {
        try {
            oos.writeObject(m);
            oos.flush();
        } catch (IOException ex) {
            Logger.getLogger(PlayerState.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
