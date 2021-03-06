/*
 * GamesPanel.java
 *
 * Created on November 17, 2008, 11:58 AM
 */
package com.novusradix.JavaPop.Client.Lobby;

import com.novusradix.JavaPop.Messaging.Lobby.JoinGame;
import com.novusradix.JavaPop.Messaging.Lobby.LobbyNewGame;
import com.novusradix.JavaPop.Server.GameInfo;

/**
 *
 * @author gef
 */
public class GamesPanel extends javax.swing.JPanel {

   public Lobby lobby;

    /** Creates new form GamesPanel */
    public GamesPanel() {
        initComponents();
    }

   

    public void setLobby(Lobby lobby) {
        this.lobby = lobby;
        lstGames.setModel(lobby.getGameList());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        lstGames = new javax.swing.JList();
        btnNewGame = new javax.swing.JButton();
        btnJoinGame = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Lobby"));

        lstGames.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstGamesValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(lstGames);

        btnNewGame.setText("New Game");
        btnNewGame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewGameActionPerformed(evt);
            }
        });

        btnJoinGame.setText("Join Game");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(btnNewGame)
                    .add(btnJoinGame))
                .addContainerGap(55, Short.MAX_VALUE))
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(btnNewGame)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnJoinGame))
        );
    }// </editor-fold>//GEN-END:initComponents

private void btnNewGameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewGameActionPerformed
    LobbyNewGame lng = new LobbyNewGame();
    lobby.client.sendMessage(lng);

}//GEN-LAST:event_btnNewGameActionPerformed

private void lstGamesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstGamesValueChanged
    if (!evt.getValueIsAdjusting()) {
        GameInfo g = (GameInfo) lstGames.getSelectedValue();
        if (g != null) {
            lobby.client.sendMessage(new JoinGame(g.id));
        }
    }
}//GEN-LAST:event_lstGamesValueChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnJoinGame;
    private javax.swing.JButton btnNewGame;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList lstGames;
    // End of variables declaration//GEN-END:variables
}
