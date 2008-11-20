/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.novusradix.JavaPop.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.novusradix.JavaPop.Client.Lobby.Lobby;
import com.novusradix.JavaPop.Messaging.GameStarted;
import com.novusradix.JavaPop.Messaging.Message;

/**
 *
 * @author erinhowie
 */
public class Client implements Runnable {

    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    public Player player;
    public Lobby lobby;
    public Game game;
    
    public Client(String host, Lobby l) {
        lobby =l;
       
        try {

            socket = new Socket(host, 13579);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
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
                message.client=this;
                
                message.execute();
            }
        } catch (IOException ioe) {
        } catch (ClassNotFoundException cnfe) {
        }
        //disconnected
        
    }

    public synchronized void sendMessage(Message m) {
        try {
            oos.writeObject(m);
            oos.flush();
            System.out.println("Client sending " + m.getClass().getSimpleName());
        } catch (IOException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void newGame(GameStarted g){
        lobby.hide();
        game = new Game(g, this);
        
        
    }
}