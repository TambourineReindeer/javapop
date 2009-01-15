package com.novusradix.JavaPop.Messaging.Tools;

import com.novusradix.JavaPop.Effects.Effect;
import com.novusradix.JavaPop.Messaging.Message;

/**
 *
 * @author gef
 */
public class StopLightning extends Message {

    @Override
    public void execute() {
        if (serverGame.lightningEffects.containsKey(serverPlayer)) {
            Effect e = serverGame.lightningEffects.get(serverPlayer);
            serverGame.deleteEffect(e);
            serverGame.lightningEffects.remove(serverPlayer);
        }
    }
}
