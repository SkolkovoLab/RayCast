package ru.skolengine.raycast.entity;

import net.minestom.server.coordinate.Vec;
import org.apache.commons.geometry.euclidean.threed.rotation.QuaternionRotation;

/**
 * @author sidey383
 **/
public interface HitBox {

    enum HitBoxType {
        BLOCK_DISPLAY, ITEM_DISPLAY_NONE
    }

    Vec getHitBoxPosition();

    QuaternionRotation getHitBoxLeftRotation();

    Vec getHitBoxScale();

    QuaternionRotation getHitBoxRightRotation();

    default HitBoxType getHitBoxType() {
        return HitBoxType.ITEM_DISPLAY_NONE;
    }

}
