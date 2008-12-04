/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Client;

import com.novusradix.JavaPop.Messaging.LeaveGame;
import com.sun.opengl.util.Animator;
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
    Game game;
    private Animator a;

    GameFrame(Game g) {
        game = g;
        GLCapabilities caps = new GLCapabilities();
        caps.setSampleBuffers(true);
        caps.setNumSamples(8);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1024, 768);
        setTitle("JavaPop");
        MainCanvas mc = new MainCanvas(caps, game);
        add(mc);
        addWindowListener(this);
        setVisible(true);
        cf = new ControlFrame();
        cf.setBounds(1024, 0, cf.getWidth(), cf.getHeight());
        cf.setVisible(true);

        a = new Animator(mc);

        a.start();

    }

    public void windowOpened(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
        a.stop();
        this.dispose();
        cf.dispose();
        game.client.sendMessage(new LeaveGame());
        game.client.lobby.show();
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
