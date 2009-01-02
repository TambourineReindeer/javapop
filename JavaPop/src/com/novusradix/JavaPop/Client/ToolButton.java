/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Client;

import com.novusradix.JavaPop.Client.Tools.BaseTool;
import com.novusradix.JavaPop.Client.Tools.Tool;
import java.awt.event.ActionEvent;
import javax.swing.JToggleButton;

/**
 *
 * @author mom
 */
public class ToolButton extends JToggleButton {

    Tool tool;

    public ToolButton(Tool t) {
        super();
        tool = t;
        setIcon(new javax.swing.ImageIcon(getClass().getResource(tool.getIconName())));
        setToolTipText(tool.getToolTip());
        setPreferredSize(new java.awt.Dimension(64, 64));
        addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(ActionEvent e) {
                BaseTool.setTool(tool.getClass());
            }
        });
    }
}
