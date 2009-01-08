/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Messaging;

import com.novusradix.JavaPop.Server.Effects.Effect;
import java.util.Collection;
import java.util.Map;

/**
 *
 * @author erinhowie
 */
public class EffectUpdate extends Message {

    Map<Integer, Effect> newEffects;
    Collection<Integer> deletedEffects;

    public EffectUpdate(Map<Integer, Effect> newEffects, Collection<Integer> deletedEffects) {
        this.newEffects = newEffects;
        this.deletedEffects = deletedEffects;
    }

    @Override
    public void execute() {
        if (clientGame.effects != null) {
            clientGame.effects.putAll(newEffects);
            clientGame.effects.keySet().removeAll(deletedEffects);
        }
    }
}
