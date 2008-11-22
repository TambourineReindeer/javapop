/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Client;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.media.opengl.GLCapabilities;
import javax.swing.JFrame;

/**
 *
 * @author mom
 */
public class GameFrame extends JFrame implements WindowListener {

    ControlFrame cf;
Client client;
    GameFrame(HeightMap h, Client c) {
        client = c;
        GLCapabilities caps = new GLCapabilities();
        caps.setSampleBuffers(true);
        caps.setNumSamples(8);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1024, 768);
        setTitle("JavaPop");
        MainCanvas mc = new MainCanvas(h, caps, client);
        add(mc);
        setVisible(true);
        cf = new ControlFrame();
        cf.setBounds(1024, 0, cf.getWidth(), cf.getHeight());
        cf.setVisible(true);
    }

    public void windowOpened(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
        this.dispose();
        cf.dispose();
        client.quit();
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }
}
