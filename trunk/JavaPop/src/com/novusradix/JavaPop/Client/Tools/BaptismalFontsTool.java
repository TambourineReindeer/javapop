package com.novusradix.JavaPop.Client.Tools;

import com.novusradix.JavaPop.Client.Client;
import java.awt.Point;

/**
 *
 * @author gef
 */
public class BaptismalFontsTool extends Tool {

    public BaptismalFontsTool(ToolGroup tg, Client c) {
        super(tg, c);
    }

    @Override
    public void PrimaryDown(Point p) {
    }

    public String getIconName() {
        return "/com/novusradix/JavaPop/icons/BaptismalFonts.png";
    }

    public String getToolTip() {
        return "Baptismal Fonts";
    }
}
