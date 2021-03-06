/*
 * MainMenu.java
 *
 * Created on January 5, 2009, 5:07 PM
 */
package com.novusradix.JavaPop.Client.Lobby;

import com.novusradix.JavaPop.Messaging.Lobby.JoinGame;
import com.novusradix.JavaPop.Messaging.Lobby.LobbyNewGame;
import com.novusradix.JavaPop.Messaging.Lobby.Ready;
import com.novusradix.JavaPop.Server.GameInfo;
import java.awt.DisplayMode;

/**
 *
 * @author gef
 */
public class MainMenu extends javax.swing.JFrame {

    Lobby l;

    /** Creates new form MainMenu */
    public MainMenu() {
        initComponents();
        l = new Lobby();
        DisplayMode dm = 
        this.getGraphicsConfiguration().getDevice().getDisplayMode();
        
        
        this.setLocation(dm.getWidth()/2 - this.getWidth()/2, dm.getHeight()/2 - this.getHeight()/2);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        quickGameButton = new javax.swing.JButton();
        multiPlayerButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.FlowLayout());

        quickGameButton.setText("Quick Game");
        quickGameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quickGameButtonActionPerformed(evt);
            }
        });
        getContentPane().add(quickGameButton);

        multiPlayerButton.setText("Multiplayer");
        multiPlayerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                multiPlayerButtonActionPerformed(evt);
            }
        });
        getContentPane().add(multiPlayerButton);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void quickGameButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quickGameButtonActionPerformed
    new com.novusradix.JavaPop.Server.Server(13579);
    l.newClient("localhost");
    LobbyNewGame lng = new LobbyNewGame();
    l.client.sendMessage(lng);
    while (l.games == null || l.games.size() == 0) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
        }
    }
    GameInfo g = l.games.firstElement();
    if (g != null) {
        l.client.sendMessage(new JoinGame(g.id));
        l.client.sendMessage(new Ready());
    }
    this.dispose();
}//GEN-LAST:event_quickGameButtonActionPerformed

private void multiPlayerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_multiPlayerButtonActionPerformed

    this.dispose();
    l.show();
//GEN-LAST:event_multiPlayerButtonActionPerformed
}

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainMenu().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton multiPlayerButton;
    private javax.swing.JButton quickGameButton;
    // End of variables declaration//GEN-END:variables

}
