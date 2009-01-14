/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Messaging;

import com.novusradix.JavaPop.Effects.Effect;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 *
 * @author erinhowie
 */
public class EffectUpdate extends Message implements Externalizable {

    private static final long serialVersionUID = 1L;
    Map<Integer, Effect> newEffects;
    Collection<Integer> deletedEffects;

    public EffectUpdate(Map<Integer, Effect> newEffects, Collection<Integer> deletedEffects) {
        this.newEffects = newEffects;
        this.deletedEffects = deletedEffects;
    }

    @Override
    public void execute() {
        if (clientGame.effects != null) {
            synchronized (clientGame.effects) {
                clientGame.effects.putAll(newEffects);
                clientGame.effects.keySet().removeAll(deletedEffects);
            }
        }
    }

    public EffectUpdate() {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(newEffects.size());
        for (Map.Entry<Integer, Effect> e : newEffects.entrySet()) {
            out.writeInt(e.getKey());
            out.writeObject(e.getValue());
        }
        out.writeInt(deletedEffects.size());
        for (Integer i : deletedEffects) {
            out.writeInt(i);
        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        newEffects = new HashMap<Integer, Effect>();
        deletedEffects = new HashSet<Integer>();
        int i = in.readInt();
        int k;
        Effect v;
        for (; i > 0; i--) {
            k = in.readInt();
            v = (Effect) in.readObject();
            newEffects.put(k, v);
        }
        i = in.readInt();
        for (; i > 0; i--) {
            deletedEffects.add(in.readInt());
        }
    }
}
