package ru.skolkovolab.raycast.entity.tool;

import org.jetbrains.annotations.NotNull;
import ru.skolkovolab.raycast.entity.HitBox;
import ru.skolkovolab.raycast.entity.HitBoxGroup;
import ru.skolkovolab.raycast.shared.VecRel;
import ru.skolkovolab.raycast.shared.collision.HitBoxCollision;

public record Collision<TR extends HitBox>(HitBoxGroup<TR> parent, TR target,
                                           VecRel in, VecRel out, boolean isInlet)
        implements HitBoxCollision<TR>, Comparable<Collision<TR>> {

    @Override
    public int compareTo(@NotNull Collision<TR> o) {
        return Double.compare(distance(), o.distance());
    }
}
