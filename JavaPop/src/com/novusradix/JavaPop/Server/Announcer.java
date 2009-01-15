package com.novusradix.JavaPop.Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gef
 */
public class Announcer implements Runnable {

    byte[] buf;
    boolean keepAlive = true;
    DatagramSocket socket;
    Thread t;
    
    Announcer(int port) {
        buf = new byte[2];
        buf[0] = (byte) (port >> 8);
        buf[1] = (byte) (port);
        try {
            socket = new DatagramSocket();
            t = new Thread(this, "Server Announce");
            t.start();
        } catch (SocketException ex) {
            Logger.getLogger(Announcer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    void kill() {
        keepAlive = false;
        socket.close();
        t.interrupt();
    }

    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket();
            InetAddress group = InetAddress.getByName("230.0.0.1");
            DatagramPacket packet;
            packet = new DatagramPacket(buf, buf.length, group, 4446);
            while (keepAlive) {
                socket.send(packet);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(Announcer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(Announcer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Announcer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
