/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abstractllc.hipparcos;

import com.sun.opengl.util.Animator;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.media.opengl.GLCapabilities;
import javax.swing.JFrame;

/**
 *
 * @author gef
 */
public class MainFrame extends JFrame implements WindowListener {


    private Animator a;
    boolean fullscreen;
    MainCanvas mc;
    HipparcosInstance hi;

    MainFrame(HipparcosInstance hi) {
        this.hi = hi;
        GLCapabilities caps = new GLCapabilities();
        setTitle("Hipparcos");
        mc = new MainCanvas(caps, hi);
        add(mc);
        addWindowListener(this);

        init(false);

        a = new Animator(mc);

        a.start();

    }

    private void init(boolean bFullScreen) {
        fullscreen = bFullScreen;
        setUndecorated(bFullScreen);
        setSize(1024, 768);
        setVisible(true);
        GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(bFullScreen ? this : null);
        mc.requestFocus();

    }

    public void setFullscreen(boolean bFullscreen) {
        if (fullscreen != bFullscreen) {
            this.dispose();
            init(bFullscreen);
        }
    }

    public void close() {
        GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(null);
        a.stop();
        this.dispose();
    }

    public void windowOpened(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
        close();
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
