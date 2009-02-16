package com.novusradix.JavaPop.Client.Tools;

import com.novusradix.JavaPop.Client.Client;
import com.novusradix.JavaPop.Messaging.Tools.MoveAnkh;
import java.awt.Point;

/**
 *
 * @author gef
 */
public class MoveAnkhTool extends Tool{

    public MoveAnkhTool(ToolGroup tg, Client c)
    {
        super(tg,c);
    }
    
    @Override
    public void PrimaryDown(Point p) {
        client.sendMessage(new MoveAnkh(p));
    }
    
    public String getIconName() {
        return "/com/novusradix/JavaPop/icons/GoToAnkh.png";
    }

    public String getToolTip() {
        return "MoveAnkh";
    }
    
}
