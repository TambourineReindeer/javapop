package com.novusradix.JavaPop.Messaging.Tools;

import com.novusradix.JavaPop.Effects.Effect;
import com.novusradix.JavaPop.Effects.LightningEffect;
import com.novusradix.JavaPop.Messaging.Message;
import java.util.Map;

/**
 *
 * @author gef
 */
public class StopLightning extends Message {

    @Override
    public void execute() {
        Map<Class, Effect> myEffects = serverGame.persistentEffects.get(serverPlayer);
        
        if (myEffects.containsKey(LightningEffect.class)) {
            Effect e = myEffects.get(LightningEffect.class);
            serverGame.deleteEffect(e);
            myEffects.remove(LightningEffect.class);
        }
    }
}
