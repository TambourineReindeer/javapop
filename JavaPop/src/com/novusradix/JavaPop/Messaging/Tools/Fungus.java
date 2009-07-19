package com.novusradix.JavaPop.Messaging.Tools;

import com.novusradix.JavaPop.Effects.Effect;
import com.novusradix.JavaPop.Effects.FungusEffect;
import com.novusradix.JavaPop.Messaging.Message;
import java.awt.Point;
import java.util.Map;

/**
 *
 * @author gef
 */
public class Fungus extends Message {

    Point target;

    public Fungus(Point p) {
        target = p;
    }

    @Override
    public void execute() {
        FungusEffect fe;
        Map<Class, Effect> myEffects = serverGame.persistentEffects.get(null);
        if (myEffects.containsKey(FungusEffect.class)) {

            fe = (FungusEffect) myEffects.get(FungusEffect.class);
        } else {
            fe = new FungusEffect(serverGame);

            serverGame.addEffect(fe);
            myEffects.put(FungusEffect.class, fe);
        }

        fe.addFungus(target.x, target.y);
    }
}
