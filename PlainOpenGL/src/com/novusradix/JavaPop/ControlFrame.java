/*
 * ControlFrame.java
 *
 * Created on November 13, 2008, 8:55 PM
 */

package com.novusradix.JavaPop;

/**
 *
 * @author  mom
 */
public class ControlFrame extends javax.swing.JFrame {

    /** Creates new form ControlFrame */
    public ControlFrame() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        controlPalette1 = new com.novusradix.JavaPop.ControlPalette();
        controlPalette2 = new com.novusradix.JavaPop.ControlPalette();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("JavaPop"); // NOI18N
        setFocusableWindowState(false);
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridLayout());
        getContentPane().add(controlPalette2);

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.novusradix.JavaPop.ControlPalette controlPalette1;
    private com.novusradix.JavaPop.ControlPalette controlPalette2;
    // End of variables declaration//GEN-END:variables

}
