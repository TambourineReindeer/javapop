package javapoptools.Fonts;

import com.sun.opengl.util.Animator;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.media.opengl.GLCapabilities;
import javax.swing.Action;
import javax.swing.JButton;
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
    private JTextField row,  column;

    KerningFrame() {
        this.setSize(800, 820);
        this.setLayout(new BorderLayout());
        GLCapabilities caps = new GLCapabilities();
        caps.setSampleBuffers(true);
        caps.setNumSamples(4);
        JPanel p = new JPanel();
        p.setPreferredSize(new Dimension(750, 750));
        kp = new KerningPanel(caps);
        kp.setPreferredSize(new Dimension(750, 750));
        add(p, BorderLayout.CENTER);
        p.add(kp);
        a = new Animator(kp);
        a.start();
        this.addWindowListener(this);

        JPanel controls = new JPanel();
        add(controls, BorderLayout.SOUTH);
        tf = new JTextField("Test me");
        tf.setPreferredSize(new Dimension(150, 24));
        controls.add(tf);
        tf.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                kp.setText(tf.getText());
            }
        });

        row = new JTextField(20);
        column = new JTextField(20);
        row.setText("ABCDEFGHIJKLM");
        column.setText("NOPQRTUVWXYZ");


        controls.add(row);
        controls.add(column);
        ActionListener al = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                kp.setRowAndColumn(row.getText(), column.getText());
            }
        };
        row.addActionListener(al);
        column.addActionListener(al);

        al.actionPerformed(null);
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
