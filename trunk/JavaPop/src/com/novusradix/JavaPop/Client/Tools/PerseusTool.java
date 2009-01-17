package com.novusradix.JavaPop.Client.Tools;

import com.novusradix.JavaPop.Client.Client;
import java.awt.Point;

/**
 *
 * @author gef
 */
public class PerseusTool extends Tool {

    public PerseusTool(ToolGroup tg, Client c) {
        super(tg, c);
    }

    @Override
    public void PrimaryAction(Point p) {
    }

    public String getIconName() {
        return "/com/novusradix/JavaPop/icons/Perseus.png";
    }

    public String getToolTip() {
        return "Perseus";
    }

}
