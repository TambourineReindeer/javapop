
package com.novusradix.JavaPop.Server;

import com.novusradix.JavaPop.Messaging.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author erinhowie
 */
public class Player implements Runnable{
    public Server s;
    public Socket socket;
    public Game currentGame;
    
    public Player(Server s, Socket socket)
    {
        this.s = s;
        this.socket = socket;
        (new Thread(this)).start();
    }

    public void run() {
        
        //Message loop
        
        try
        {
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        
        Message message;
        while(socket.isConnected())
        {
            message = (Message)ois.readObject();
            message.server = s;
            message.serverGame = currentGame;
            message.execute();
        }
        }catch (IOException ioe)
        { }catch (ClassNotFoundException cnfe)
        { }
        //Disconnect
    }
}
