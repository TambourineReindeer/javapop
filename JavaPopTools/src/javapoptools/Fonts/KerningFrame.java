package javapoptools.Fonts;

import com.sun.opengl.util.Animator;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.media.opengl.GLCapabilities;
import javax.swing.JFrame;
import sun.awt.WindowClosingListener;

/**
 *
 * @author gef
 */
public class KerningFrame extends JFrame implements WindowListener {

    private KerningPanel kp;
    private Animator a;

    KerningFrame() {
        GLCapabilities caps = new GLCapabilities();
        caps.setSampleBuffers(true);
        caps.setNumSamples(4);
        kp = new KerningPanel(caps);


        kp.setPreferredSize(new Dimension(128, 128));
        add(kp);

        this.setSize(800, 800);
        a = new Animator(kp);
        a.start();
        this.addWindowListener(this);
    }

    public static void main(String[] args) {
        new KerningFrame().setVisible(true);
    }

    public void windowOpened(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
        a.stop();
        dispose();
    }

    public void windowClosed(WindowEvent e) {
        a.stop();
        dispose();
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
