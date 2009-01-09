package com.novusradix.JavaPop.Client;

import com.novusradix.JavaPop.Client.Tools.RaiseLowerTool;
import com.novusradix.JavaPop.Client.Tools.Tool;
import java.awt.Point;
import java.awt.Polygon;
import java.util.Collection;

/**
 *
 * @author gef
 */
public class GLToolButton extends GLButton {

    private static GLToolButton selected;
    private static GLToolButton defaultToolButton;
    
    private Tool tool;
    private GLToolGroupButton groupButton;
    
    public Tool getTool()
    {
        return tool;
    }
    
    public GLToolButton(Tool t, GLToolGroupButton parent, ClickableHandler ch, Collection<GLObject> objects) {
        super(ch, objects);
        tool = t;
        int[] x, y;
        x = new int[4];
        y = new int[4];
        Point p = t.getPosition();
        if (p.y < 0) {
            p.y = -p.y;
            flipy = true;
        }
        if (p.x < 0) {
            p.x = -p.x;
            flipx = true;
        }
        x[0] = p.x;
        x[1] = p.x + 50;
        x[2] = p.x + 100;
        x[3] = p.x + 50;
        y[0] = p.y;
        y[1] = p.y + 25;
        y[2] = p.y;
        y[3] = p.y - 25;
        
        visible = false;
        texname = t.getIconName();
        buttonShape = new Polygon(x, y, 4);
        groupButton = parent;
        
        if(t.getClass() == RaiseLowerTool.class)
            defaultToolButton = this;
    }

    public static void selectDefault()
    {
        if(defaultToolButton!=null)
            defaultToolButton.select();
    }
    
    public static Tool getSelected()
    {
        return selected.tool;
    }
    
    @Override
    public void select() {
        selected = this;
        groupButton.select();
    }

    @Override
    protected boolean isSelected() {
        return selected==this;
    }

    
}
