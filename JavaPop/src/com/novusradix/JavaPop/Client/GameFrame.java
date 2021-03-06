
package com.novusradix.JavaPop.Client;

import com.novusradix.JavaPop.Messaging.Lobby.LeaveGame;
import com.sun.opengl.util.Animator;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.media.opengl.GLCapabilities;
import javax.swing.JFrame;

/**
 * The main window for the active game.
 * @author gef
 */
public class GameFrame extends JFrame implements WindowListener {

    Game game;
    private Animator a;
    boolean fullscreen;
    MainCanvas mc;

    GameFrame(Game g) {
        game = g;
        GLCapabilities caps = new GLCapabilities();
        caps.setSampleBuffers(true);
        caps.setNumSamples(8);

        setTitle("JavaPop");
        mc = new MainCanvas(caps, game);
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
        game.client.sendMessage(new LeaveGame());
        game.client.lobby.show();
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
