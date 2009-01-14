package com.novusradix.JavaPop.Messaging.Tools;

import com.novusradix.JavaPop.Messaging.Message;
import com.novusradix.JavaPop.Effects.LightningEffect;
import java.awt.Point;

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
        if (serverGame.lightningEffects.containsKey(serverPlayer)) {
            serverGame.deleteEffect(serverGame.lightningEffects.get(serverPlayer));
        }

        serverGame.addEffect(le);
        serverGame.lightningEffects.put(serverPlayer, le);
    }
}
