package com.novusradix.JavaPop.Client.Tools;

import com.novusradix.JavaPop.Client.Client;
import java.awt.Point;

/**
 *
 * @author gef
 */
public class TidalWaveTool extends Tool {

    public TidalWaveTool(ToolGroup tg, Client c) {
        super(tg, c);
    }

    @Override
    public void PrimaryDown(Point p) {
    }

    public String getIconName() {
        return "/com/novusradix/JavaPop/icons/Tidalwave.png";
    }

    public String getToolTip() {
        return "TidalWave";
    }

}
