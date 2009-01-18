/*
 * MainWindow.java
 *
 * Created on January 17, 2009, 1:39 PM
 */
package javapoptools;

import com.sun.opengl.util.Animator;
import java.io.File;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GLCapabilities;
import javax.swing.JFileChooser;

/**
 *
 * @author  mom
 */
public class MainWindow extends javax.swing.JFrame {

    MainPanel mp;

    /** Creates new form MainWindow */
    public MainWindow() {
        initComponents();
        GLCapabilities caps = new GLCapabilities();
        caps.setSampleBuffers(true);
        caps.setNumSamples(8);
        mp = new MainPanel(caps);
        add(mp);
        Animator a =new Animator(mp);
        a.start();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Menu = new javax.swing.JMenuBar();
        FileMenu = new javax.swing.JMenu();
        OpenMenuItem = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setName("JavaPop tools"); // NOI18N
        getContentPane().setLayout(new java.awt.FlowLayout());

        FileMenu.setText("File");

        OpenMenuItem.setText("Open...");
        OpenMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OpenMenuItemActionPerformed(evt);
            }
        });
        FileMenu.add(OpenMenuItem);

        Menu.add(FileMenu);

        jMenu2.setText("Edit");
        Menu.add(jMenu2);

        setJMenuBar(Menu);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void OpenMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OpenMenuItemActionPerformed
    final JFileChooser fc = new JFileChooser();


    int returnVal = fc.showOpenDialog(this);
    File f = fc.getSelectedFile();
    XImporter i;
   
    try {
        i = new XImporter(f.toURL());
        mp.setData(i.getModel());
    } catch (MalformedURLException ex) {
        Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
    }

}//GEN-LAST:event_OpenMenuItemActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu FileMenu;
    private javax.swing.JMenuBar Menu;
    private javax.swing.JMenuItem OpenMenuItem;
    private javax.swing.JMenu jMenu2;
    // End of variables declaration//GEN-END:variables

}
