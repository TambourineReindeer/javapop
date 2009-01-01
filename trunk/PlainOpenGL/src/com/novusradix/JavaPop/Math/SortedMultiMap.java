package com.novusradix.JavaPop.Math;

import java.util.HashSet;
import java.util.TreeMap;

/**
 *
 * @author gef
 */
public class SortedMultiMap<K, V> extends MultiMap<K, V> {

    public SortedMultiMap() {
        m = new TreeMap<K, HashSet<V>>();
    }
}
