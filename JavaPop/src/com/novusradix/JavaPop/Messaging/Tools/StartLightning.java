package com.novusradix.JavaPop.Messaging.Tools;

import com.novusradix.JavaPop.Effects.Effect;
import com.novusradix.JavaPop.Messaging.Message;
import com.novusradix.JavaPop.Effects.LightningEffect;
import java.awt.Point;
import java.util.Map;

/**
 *
 * @author gef
 */
public class StartLightning extends Message {

    Point target;

    public StartLightning(Point p) {
        target = p;
    }

    @Override
    public void execute() {
        LightningEffect le = new LightningEffect(target);
        Map<Class, Effect> myEffects = serverGame.persistentEffects.get(serverPlayer);
        if (myEffects.containsKey(LightningEffect.class)){
            
            serverGame.deleteEffect(myEffects.get(LightningEffect.class));
        }

        serverGame.addEffect(le);
        myEffects.put(LightningEffect.class, le);
    }
}
