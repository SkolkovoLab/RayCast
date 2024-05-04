package ru.skolkovolab.raycast.entity;

import org.apache.commons.geometry.euclidean.threed.Vector3D;
import ru.skolkovolab.raycast.entity.tool.RayCastHitBox;
import ru.skolkovolab.raycast.entity.tool.RayCastHitBoxGroup;

import java.util.Collection;
import java.util.Optional;

/**
 * @author sidey383
 */
public interface HitBoxGroup<T extends HitBox> {
    Collection<T> getHitBoxCollection();

    Vector3D getHitBoxGroupCenter();

    Double getHitBoxGroupRadius();

    default Optional<RayCastHitBoxGroup<T>> startRayCast(Vector3D position, Vector3D direction) {
        return RayCastHitBoxGroup.createGroup(this, position, direction);
    }

    static <TR extends HitBox> RayCastHitBox<TR> startRayCast(Collection<HitBoxGroup<TR>> hitboxes, Vector3D position, Vector3D direction) {
        return RayCastHitBox.createRaycast(hitboxes, position, direction);
    }
}
