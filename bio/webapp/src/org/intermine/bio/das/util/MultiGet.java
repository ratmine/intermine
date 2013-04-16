package org.intermine.bio.das.util;

import java.util.Collection;

public interface MultiGet<K, V> {

    Collection<V> getAll(K key);
}
