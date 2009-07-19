package com.novusradix.JavaPop.Messaging.Tools;

import com.novusradix.JavaPop.Messaging.Message;
import java.awt.Point;

/**
 *
 * @author gef
 */
public class Plague extends Message{

    private static final long serialVersionUID = 1L;
    private int x,y;
    
    public Plague (Point p)
    {
        x=p.x;y=p.y;
    }

    @Override
    public void execute() {
        serverGame.peons.infectWithPlague(x,y);
        serverGame.houses.infectWithPlague(x, y);

    }

}
