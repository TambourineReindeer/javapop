package com.novusradix.JavaPop.Client.Tools;

import com.novusradix.JavaPop.Client.Client;
import com.novusradix.JavaPop.Client.GLToolButton;
import java.awt.Point;

/**
 *
 * @author gef
 */
public abstract class BaseTool implements Tool {

    protected ToolGroup toolGroup;
    protected Client client;
    
    private BaseTool() {
    }

    protected BaseTool(ToolGroup tg, Client c) {
        toolGroup = tg;
        client = c;
    }

    public void PrimaryAction(Point p) {
    }

    public void SecondaryAction(Point p) {
        GLToolButton.selectDefault();
    }

    public void ButtonDown(Point p) {
    }

    public void ButtonUp(Point p) {
    }

    public ToolGroup getGroup() {
        return toolGroup;
    }
}
