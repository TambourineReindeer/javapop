package com.novusradix.JavaPop.Client.Tools;

import com.novusradix.JavaPop.Client.Client;
import com.novusradix.JavaPop.Direction;
import com.novusradix.JavaPop.Messaging.Tools.Basalt;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import javax.naming.directory.DirContext;

/**
 *
 * @author gef
 */
public class BasaltTool extends Tool {

    private Cursor[] cursors;
    
    public BasaltTool(ToolGroup tg, Client c) {
        super(tg, c);
        Toolkit tk = Toolkit.getDefaultToolkit();
        cursors = new Cursor[4];
        cursors[0] = tk.createCustomCursor(tk.getImage(getClass().getResource("/com/novusradix/JavaPop/cursors/north.png")), new Point(0,0), "north");
        cursors[1] = tk.createCustomCursor(tk.getImage(getClass().getResource("/com/novusradix/JavaPop/cursors/east.png")), new Point(0,0), "east");
        cursors[2] = tk.createCustomCursor(tk.getImage(getClass().getResource("/com/novusradix/JavaPop/cursors/south.png")), new Point(0,0), "south");
        cursors[3] = tk.createCustomCursor(tk.getImage(getClass().getResource("/com/novusradix/JavaPop/cursors/west.png")), new Point(0,0), "west");
    }

    @Override
    public void PrimaryAction(Point p) {
       int dir = (int) ((System.currentTimeMillis() / 500) % 4);
        client.sendMessage(new Basalt(p, Direction.values()[dir]));
       
    }

    public String getIconName() {
        return "/com/novusradix/JavaPop/icons/Basalt.png";
    }

    public String getToolTip() {
        return "Basalt";
    }
    
    @Override
    public Cursor getCursor()
    {
    int dir = (int) ((System.currentTimeMillis() / 500) % 4);
           return cursors[dir];
    }

}
