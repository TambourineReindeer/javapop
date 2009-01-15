package com.novusradix.JavaPop.Messaging.Tools;

import com.novusradix.JavaPop.Messaging.Message;
import com.novusradix.JavaPop.Effects.SwampEffect;
import java.awt.Point;

/**
 *
 * @author gef
 */
public class Swamp extends Message {

    Point target;

    public Swamp(Point p) {
        target = p;
    }

    @Override
    public void execute() {
        serverGame.addEffect(new SwampEffect(target));
    }
}
