package com.novusradix.JavaPop.Client.Tools;

import com.novusradix.JavaPop.Client.Client;
import java.awt.Point;

/**
 *
 * @author gef
 */
public class TidalWaveTool extends BaseTool {

    public TidalWaveTool(ToolGroup tg, Client c) {
        super(tg, c);
    }

    @Override
    public void PrimaryAction(Point p) {
    }

    public String getIconName() {
        return "/com/novusradix/JavaPop/icons/Tidalwave.png";
    }

    public String getToolTip() {
        return "TidalWave";
    }

    public Point getPosition() {
        return new Point(250, -25);
    }
}
