/*
 * LobbyFrame.java
 *
 * Created on November 17, 2008, 11:09 AM
 */
package com.novusradix.JavaPop.Client.Lobby;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;


/**
 *
 * @author gef
 */
public class LobbyFrame extends javax.swing.JFrame implements WindowListener {

    public Lobby lobby;
   // public PlayerState player;
    
    /** Creates new form LobbyFrame */
    public LobbyFrame(Lobby l) {
        this.addWindowListener(this);
        initComponents();
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        lobby = l;
        serverPanel1.setLobby(lobby);
        gamesPanel1.setLobby(lobby);
        gamePanel1.setLobby(lobby);
        gamesPanel1.setEnabled(false);
        gamePanel1.setEnabled(false);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        serverPanel1 = new com.novusradix.JavaPop.Client.Lobby.ServerPanel();
        gamesPanel1 = new com.novusradix.JavaPop.Client.Lobby.GamesPanel();
        gamePanel1 = new com.novusradix.JavaPop.Client.Lobby.GamePanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(serverPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 166, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(gamesPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 201, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(gamePanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, gamePanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                    .add(gamesPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                    .add(serverPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public com.novusradix.JavaPop.Client.Lobby.GamePanel getGamePanel() {
        return gamePanel1;
    }

    public com.novusradix.JavaPop.Client.Lobby.GamesPanel getGamesPanel() {
        return gamesPanel1;
    }

    public com.novusradix.JavaPop.Client.Lobby.ServerPanel getServerPanel() {
        return serverPanel1;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.novusradix.JavaPop.Client.Lobby.GamePanel gamePanel1;
    private com.novusradix.JavaPop.Client.Lobby.GamesPanel gamesPanel1;
    private com.novusradix.JavaPop.Client.Lobby.ServerPanel serverPanel1;
    // End of variables declaration//GEN-END:variables

    public void windowOpened(WindowEvent e) {
        }

    public void windowClosing(WindowEvent e) {
        lobby.quit();
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
