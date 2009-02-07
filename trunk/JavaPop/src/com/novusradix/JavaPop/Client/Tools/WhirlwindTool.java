package com.novusradix.JavaPop.Client.Tools;

import com.novusradix.JavaPop.Client.Client;
import java.awt.Point;

/**
 *
 * @author gef
 */
public class WhirlwindTool extends Tool {

    public WhirlwindTool(ToolGroup tg, Client c) {
        super(tg, c);
    }

    @Override
    public void PrimaryDown(Point p) {
    }

    public String getIconName() {
        return "/com/novusradix/JavaPop/icons/Whirlwind.png";
    }

    public String getToolTip() {
        return "Whirlwind";
    }
}
