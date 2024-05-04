package ru.skolkovolab.raycast.shared.collision;

import ru.skolkovolab.raycast.entity.HitBox;
import ru.skolkovolab.raycast.entity.HitBoxGroup;

public non-sealed interface HitBoxCollision<T extends HitBox> extends RayCastCollision<T> {
    HitBoxGroup<T> parent();
}
