package ru.skolkovolab.raycast.shared;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * @author sidey383
 */
public interface RayCastIterator<T> extends Iterator<T>, Comparable<RayCastIterator<?>> {
    /**
     * @return current distance
     * -1 on start
     * Double.MAX_VALUE after last
     **/
    double distance();

    /**
     * @return current element
     **/
    T current();

    boolean hasCurrent();

    @Override
    default int compareTo(@NotNull RayCastIterator<?> o) {
        return Double.compare(this.distance(), o.distance());
    }

    static int compare(@NotNull RayCastIterator<?> o1, @NotNull RayCastIterator<?> o2) {
        return Double.compare(o1.distance(), o2.distance());
    }
}
