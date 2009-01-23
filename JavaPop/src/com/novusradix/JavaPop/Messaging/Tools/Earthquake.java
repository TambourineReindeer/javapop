package com.novusradix.JavaPop.Messaging.Tools;

import com.novusradix.JavaPop.Direction;
import com.novusradix.JavaPop.Effects.EarthquakeEffect;
import com.novusradix.JavaPop.Messaging.*;
import java.awt.Point;



/**
 *
 * @author gef
 */
public class Earthquake extends Message {

    /**
     * 
     */
    Point target;
    Direction direction;

    public Earthquake(Point p, Direction d) {
        target = p;
        direction = d;
    }

    @Override
    public void execute() {
        serverGame.addEffect(new EarthquakeEffect(target, direction));
    }
}
