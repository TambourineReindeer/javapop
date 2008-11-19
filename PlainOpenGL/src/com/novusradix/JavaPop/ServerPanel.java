/*
 * ServerPanel.java
 *
 * Created on November 17, 2008, 11:55 AM
 */

package com.novusradix.JavaPop;

import com.novusradix.JavaPop.Client.PlayerState;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

/**
 *
 * @author  mom
 */
public class ServerPanel extends javax.swing.JPanel {
   private DefaultListModel serverNames;
   public LobbyFrame parent;
   
    /** Creates new form ServerPanel */
    public ServerPanel() {
        initComponents();
       
        serverNames = new DefaultListModel();
        
    lstServerList.setModel(serverNames);
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
        lstServerList = new javax.swing.JList();
        lblConnect = new javax.swing.JLabel();
        txtServerName = new javax.swing.JTextField();
        btnConnect = new javax.swing.JButton();
        btnNewLocalServer = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Server"));

        lstServerList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstServerList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstServerListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(lstServerList);

        lblConnect.setText("Connect:");

        txtServerName.setText("localhost");

        btnConnect.setText("Go");
        btnConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConnectActionPerformed(evt);
            }
        });

        btnNewLocalServer.setText("New Local Server");
        btnNewLocalServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewLocalServerActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(btnNewLocalServer)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(txtServerName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnConnect))
            .add(lblConnect)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(btnNewLocalServer)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblConnect)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnConnect)
                    .add(txtServerName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );
    }// </editor-fold>//GEN-END:initComponents

private void btnNewLocalServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewLocalServerActionPerformed

    new com.novusradix.JavaPop.Server.Server(13579);
    serverNames.addElement("localhost");
    
}//GEN-LAST:event_btnNewLocalServerActionPerformed

private void btnConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConnectActionPerformed
// TODO add your handling code here:
    if(serverNames.indexOf(txtServerName.getText()) ==-1)
        serverNames.addElement(txtServerName.getText());//GEN-LAST:event_btnConnectActionPerformed
}

private void lstServerListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstServerListValueChanged
    
    if(lstServerList.getSelectedValue() != null && !evt.getValueIsAdjusting())
    {
        String s = (String)lstServerList.getSelectedValue();
        
        GamesPanel gsp = parent.getGamesPanel();
        GamePanel gp = parent.getGamePanel();
        
        parent.p = new PlayerState(s, gsp, gp);
        gsp.setEnabled(true);
    }
}//GEN-LAST:event_lstServerListValueChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConnect;
    private javax.swing.JButton btnNewLocalServer;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblConnect;
    private javax.swing.JList lstServerList;
    private javax.swing.JTextField txtServerName;
    // End of variables declaration//GEN-END:variables

}