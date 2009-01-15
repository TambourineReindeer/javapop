package com.novusradix.JavaPop.Messaging.Tools;

import com.novusradix.JavaPop.Messaging.*;
import java.awt.Point;

/**
 *
 * @author gef
 */
public class MoveAnkh extends Message {

    private Point where;

    public MoveAnkh(Point p) {
        where = p;
    }

    @Override
    public void execute() {
           serverPlayer.MoveAnkh(where);    
    }
}
