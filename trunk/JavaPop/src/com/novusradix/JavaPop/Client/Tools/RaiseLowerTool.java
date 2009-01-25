package com.novusradix.JavaPop.Client.Tools;

import com.novusradix.JavaPop.Client.Client;
import com.novusradix.JavaPop.House;
import com.novusradix.JavaPop.Messaging.Tools.Sprog;
import com.novusradix.JavaPop.Messaging.Tools.UpDown;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;

/**
 *
 * @author gef
 */
public class RaiseLowerTool extends Tool {

    Cursor cursor;

    public RaiseLowerTool(ToolGroup tg, Client c) {
        super(tg, c);
        Toolkit tk = Toolkit.getDefaultToolkit();
        cursor = tk.createCustomCursor(tk.getImage(getClass().getResource("/com/novusradix/JavaPop/cursors/raiselower.png")), new Point(0, 0), "raiselower");
    }

    @Override
    public void PrimaryAction(Point p) {
        client.sendMessage(new UpDown(p, true));
    }

    @Override
    public void SecondaryAction(Point p) {
        if (overMyHouse(p)) {
            client.sendMessage(new Sprog(p));
        } else {
            client.sendMessage(new UpDown(p, false));
        }
    }

    public String getIconName() {
        return "/com/novusradix/JavaPop/icons/UpDown.png";
    }

    public String getToolTip() {
        return "Raise/Lower land";
    }

    @Override
    public Cursor getCursor(Point p) {
        return overMyHouse(p) ? super.getCursor():cursor;
    }

    boolean overMyHouse(Point p) {
        House h =client.game.houses.getHouse(p.x, p.y);

        if(h == null)
            return false;
        return (h.player == client.game.me);
    }
}
