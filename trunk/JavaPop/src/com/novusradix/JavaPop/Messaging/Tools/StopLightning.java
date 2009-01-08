package com.novusradix.JavaPop.Messaging.Tools;

import com.novusradix.JavaPop.Messaging.Message;

/**
 *
 * @author erinhowie
 */
public class StopLightning extends Message {

    @Override
    public void execute() {
        if (serverGame.lightningEffects.containsKey(serverPlayer)) {
            serverGame.deleteEffect(serverGame.lightningEffects.get(serverPlayer));
            serverGame.lightningEffects.remove(serverPlayer);
        }
    }
}
