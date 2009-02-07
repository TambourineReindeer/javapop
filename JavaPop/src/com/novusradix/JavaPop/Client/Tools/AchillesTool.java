package com.novusradix.JavaPop.Client.Tools;

import com.novusradix.JavaPop.Client.Client;
import java.awt.Point;

/**
 *
 * @author gef
 */
public class AchillesTool extends Tool {

    public AchillesTool(ToolGroup tg, Client c) {
        super(tg, c);
    }

    @Override
    public void PrimaryDown(Point p) {
    }

    public String getIconName() {
        return "/com/novusradix/JavaPop/icons/Achilles.png";
    }

    public String getToolTip() {
        return "Achilles";
    }
}
