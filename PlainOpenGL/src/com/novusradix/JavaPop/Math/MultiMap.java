/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Math;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author erinhowie
 */
public class MultiMap<K, V> {

    protected Map<K, HashSet<V>> m;

    public MultiMap() {
        m = new HashMap<K, HashSet<V>>();
    }

    @SuppressWarnings("unchecked")
    public void put(K key, V value) {
        if (m.containsKey(key)) {
            if (!m.get(key).add(value)) {
                System.out.print("duplicate.\n");
            }
        } else {
            HashSet l = new HashSet<V>();
            l.add(value);
            m.put(key, l);
        }
    }

    public List<V> get(K key) {
        if (m.containsKey(key)) {
            return new ArrayList<V>(m.get(key));
        }
        return new ArrayList<V>();
    }

    public void remove(K key, V value) {
        if (m.containsKey(key)) {
            Set l;
            l = m.get(key);
            l.remove(value);
            if (l.size() == 0) {
                m.remove(key);
            }
        }
    }

    public int size(K key) {
        if (m.containsKey(key)) {
            return m.get(key).size();
        }
        return 0;
    }
    
    public Collection<K> getKeys()
    {
        return m.keySet();
    }
}
