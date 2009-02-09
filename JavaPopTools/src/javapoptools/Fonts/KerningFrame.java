package javapoptools.Fonts;

import com.sun.opengl.util.Animator;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.media.opengl.GLCapabilities;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import sun.awt.WindowClosingListener;

/**
 *
 * @author gef
 */
public class KerningFrame extends JFrame implements WindowListener {

    private KerningPanel kp;
    private Animator a;
    private JTextField tf;

    KerningFrame() {
        this.setSize(800, 800);
        this.setLayout(new BorderLayout());
        GLCapabilities caps = new GLCapabilities();
        caps.setSampleBuffers(true);
        caps.setNumSamples(4);
        JPanel p = new JPanel();
        p.setPreferredSize(new Dimension(750,750));
        kp = new KerningPanel(caps);
        kp.setPreferredSize(new Dimension(750,750));
        add(p, BorderLayout.CENTER);
p.add(kp);
        a = new Animator(kp);
        a.start();
        this.addWindowListener(this);

        tf = new JTextField("Test me");
        add(tf, BorderLayout.SOUTH);
        tf.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                kp.setText(tf.getText());
            }
        });
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
