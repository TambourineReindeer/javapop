package com.novusradix.JavaPop.Messaging.Tools;

import com.novusradix.JavaPop.Effects.BatholithEffect;
import com.novusradix.JavaPop.Effects.Effect;
import com.novusradix.JavaPop.Messaging.Message;
import java.util.Map;

/**
 *
 * @author gef
 */
public class StopBatholith extends Message {

    @Override
    public void execute() {
        Map<Class, Effect> myEffects = serverGame.persistentEffects.get(serverPlayer);
        
        if (myEffects.containsKey(BatholithEffect.class)) {
            Effect e = myEffects.get(BatholithEffect.class);
            serverGame.deleteEffect(e);
            myEffects.remove(BatholithEffect.class);
        }
    }
}
