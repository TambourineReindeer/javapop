package com.novusradix.JavaPop.Messaging.Tools;

import com.novusradix.JavaPop.Effects.BatholithEffect;
import com.novusradix.JavaPop.Effects.Effect;
import com.novusradix.JavaPop.Messaging.Message;
import java.awt.Point;
import java.util.Map;

/**
 *
 * @author gef
 */
public class StartBatholith extends Message {

    Point target;

    public StartBatholith(Point p) {
        target = p;
    }

    @Override
    public void execute() {
        BatholithEffect be = new BatholithEffect(target);
        Map<Class, Effect> myEffects = serverGame.persistentEffects.get(serverPlayer);
        if (myEffects.containsKey(BatholithEffect.class)){
            
            serverGame.deleteEffect(myEffects.get(BatholithEffect.class));
        }

        serverGame.addEffect(be);
        myEffects.put(BatholithEffect.class, be);
    }
}
