package ru.skolkovolab.raycast.shared.collision;

import ru.skolkovolab.raycast.shared.VecRel;

public sealed interface RayCastCollision<T> permits BlockCollision, HitBoxCollision {
    default double distance() {
        return (this.isInlet() ? in() : out()).dist();
    }

    VecRel in();

    VecRel out();

    T target();

    boolean isInlet();

    default boolean isOutlet() {
        return !isInlet();
    }
}
