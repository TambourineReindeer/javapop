package com.novusradix.JavaPop.Messaging.Tools;

import com.novusradix.JavaPop.Direction;
import com.novusradix.JavaPop.Effects.BasaltEffect;
import com.novusradix.JavaPop.Messaging.Message;
import java.awt.Point;

/**
 *
 * @author gef
 */
public class Basalt extends Message {

    Point target;
    Direction direction;

    public Basalt(Point p, Direction d) {
        target = p;
        direction = d;
    }

    @Override
    public void execute() {
        serverGame.addEffect(new BasaltEffect(target, direction));
    }
}
