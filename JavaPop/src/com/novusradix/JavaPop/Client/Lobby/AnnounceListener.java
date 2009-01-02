/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Client.Lobby;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mom
 */
public class AnnounceListener implements Runnable {

    Lobby lobby;
    boolean keepAlive = true;
    MulticastSocket socket;
    InetAddress group = null;

    AnnounceListener(Lobby l) {
        lobby = l;
        try {
            socket = new MulticastSocket(4446);
            group = InetAddress.getByName("230.0.0.1");
            socket.joinGroup(group);
            new Thread(this, "Announce Listener").start();
        } catch (IOException ex) {
            Logger.getLogger(AnnounceListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void kill() {
        keepAlive = false;
        try {
            socket.leaveGroup(group);
        } catch (IOException ex) {
            Logger.getLogger(AnnounceListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        socket.close();
    }

    public void run() {
        try {
            DatagramPacket packet;
            while (keepAlive) {
                byte[] buf = new byte[256];
                packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                int received = (int)packet.getData()[0] * 256 + (int)packet.getData()[1];
                lobby.addServer(packet.getAddress().getHostName());
            }

        } catch (IOException ex) {
            //no problems, almost certainly caused by kill()
        }

        //but just in case let's leave the group and close the socket.
        if (!socket.isClosed()) {
            try {
                socket.leaveGroup(group);
            } catch (IOException ex) {
                Logger.getLogger(AnnounceListener.class.getName()).log(Level.SEVERE, null, ex);
            }
            socket.close();
        }
    }
}
