package ru.skolengine.raycast.shared.collision;

import ru.skolengine.raycast.entity.HitBox;
import ru.skolengine.raycast.entity.HitBoxGroup;

public non-sealed interface HitBoxCollision<T extends HitBox> extends RayCastCollision<T> {
    HitBoxGroup<T> parent();
}
