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

    ServerPanel serverPanel;
    boolean keepAlive = true;
    MulticastSocket socket;

    public AnnounceListener(ServerPanel sp) {
        serverPanel = sp;
        try {
            socket = new MulticastSocket(4446);
            new Thread(this, "Announce Listener").start();
        } catch (IOException ex) {
            Logger.getLogger(AnnounceListener.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    void kill() {
        keepAlive = false;
        socket.close();
    }

    public void run() {
        InetAddress group = null;
        try {
            group = InetAddress.getByName("230.0.0.1");
            socket.joinGroup(group);

            DatagramPacket packet;
            while (keepAlive) {
                byte[] buf = new byte[256];
                packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                int received = packet.getData()[0] * 255 + packet.getData()[1];
                System.out.println("Announce: " + received);
                serverPanel.addServer(packet.getAddress().getHostName());
            }

        } catch (IOException ex) {
            Logger.getLogger(AnnounceListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            if(group!=null)
            socket.leaveGroup(group);
        } catch (IOException ex) {
            Logger.getLogger(AnnounceListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        socket.close();

    }
}
