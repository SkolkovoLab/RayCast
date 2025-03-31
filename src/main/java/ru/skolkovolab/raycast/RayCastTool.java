package ru.skolkovolab.raycast;

import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.skolkovolab.raycast.block.RayCastBlock;
import ru.skolkovolab.raycast.entity.HitBox;
import ru.skolkovolab.raycast.entity.HitBoxGroup;
import ru.skolkovolab.raycast.entity.fallback.HitBoxGroupInternal;
import ru.skolkovolab.raycast.entity.tool.RayCastHitBox;
import ru.skolkovolab.raycast.shared.collision.BlockCollision;
import ru.skolkovolab.raycast.shared.collision.HitBoxCollision;
import ru.skolkovolab.raycast.shared.collision.RayCastCollision;

import java.util.Collection;
import java.util.Iterator;

/**
 * Raycast tool class that provides raycasting methods.
 *
 * @author danirod12, sidey383
 */
public class RayCastTool {
    // default max distance is 8 chunks
    private static final int DEFAULT_MAX_DISTANCE = 8 * 16;

    /**
     * Utility class, no public constructor
     * **/
    private RayCastTool() {}

    /**
     * Raycast method. You can provide instance or entities collection as null to disable it
     * Null instance represents disabled blocks check
     * <p>
     * Sets max raycast length to default value of 8 chunks (128 blocks)
     *
     * @param instance        The instance (world) to raycast in.
     * @param hitBoxGroups    The iterator that provides the raycast collisions (entities).
     * @param originBase      The origin location of the ray.
     * @param originDirection The direction of the ray.
     * @param request         The request to handle the raycast.
     * @param <T>             The type of the hitbox.
     * @return Whether the raycast was successful (true - finished by handler, false - finished by max distance).
     */
    public static <T extends HitBox> boolean rayCast(@Nullable Block.Getter instance,
                                                     @Nullable Collection<HitBoxGroup<T>> hitBoxGroups,
                                                     Vec originBase, Vec originDirection,
                                                     RayCastRequest<T> request) {
        return RayCastTool.rayCast(instance, hitBoxGroups, originBase, originDirection, DEFAULT_MAX_DISTANCE, request);
    }

    /**
     * Raycast method. You can provide instance or entities collection as null to disable it
     * Null instance represents disabled blocks check
     *
     * @param instance        The instance (world) to raycast in.
     * @param hitBoxGroups    The iterator that provides the raycast collisions (entities).
     * @param originBase      The origin location of the ray.
     * @param originDirection The direction of the ray.
     * @param maxDistance     The max distance our ray cast may be processed on.
     * @param request         The request to handle the raycast.
     * @param <T>             The type of the hitbox.
     * @return Whether the raycast was successful (true - finished by handler, false - finished by max distance).
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T extends HitBox> boolean rayCast(@Nullable Block.Getter instance,
                                                     @Nullable Collection<HitBoxGroup<T>> hitBoxGroups,
                                                     Vec originBase, Vec originDirection, double maxDistance,
                                                     RayCastRequest<T> request) {
        if (isNaN(originBase)) throw new IllegalArgumentException("Origin base is NaN");
        if (isNaN(originDirection)) throw new IllegalArgumentException("Origin direction is NaN");
        if (instance == null && hitBoxGroups == null) {
            throw new IllegalArgumentException("You should provide at least one of instance (world) and hitbox entities");
        }

        RayCastBlock rcb = instance == null ? null : RayCastBlock.createRaycast(instance, originBase, originDirection);
        if (rcb != null && hitBoxGroups == null) {
            return rayCast((Iterator) rcb.iterator(), maxDistance, request);
        }

        RayCastHitBox<T> rchb = RayCastHitBox.createRaycast(hitBoxGroups,
                minestomToEuclidean(originBase), minestomToEuclidean(originDirection));
        if (rcb == null) {
            if (hitBoxGroups.isEmpty()) {
                return false;
            }
            return rayCast((Iterator) rchb.iterator(), maxDistance, request);
        }
        return RayCastTool.rayCast(rcb, rchb, maxDistance, request);
    }

    public static boolean isNaN(Vec vec) {
        var x = Double.isNaN(vec.x()) || Double.isInfinite(vec.x());
        var y = Double.isNaN(vec.y()) || Double.isInfinite(vec.y());
        var z = Double.isNaN(vec.z()) || Double.isInfinite(vec.z());

        return x || y || z;
    }

    /**
     * Raycast method
     *
     * @param rayCastBlock  The ray cast setup over blocks
     * @param rayCastHitBox The ray cast setup over hitboxes
     * @param maxDistance   The maximum distance of the ray.
     * @param request       The request to handle the raycast.
     * @param <T>           The type of the hitbox.
     * @return Whether the raycast was successful (true - finished by handler, false - finished by max distance).
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T extends HitBox> boolean rayCast(@NotNull RayCastBlock rayCastBlock,
                                                     @NotNull RayCastHitBox<T> rayCastHitBox,
                                                     double maxDistance, RayCastRequest<T> request) {
        Iterator<BlockCollision> iterator1 = rayCastBlock.iterator();
        Iterator<HitBoxCollision<T>> iterator2 = rayCastHitBox.iterator();

        RayCastCollision<?> collision1 = iterator1.next();
        RayCastCollision<?> collision2 = iterator2.hasNext() ? iterator2.next() : null;

        do {
            if (collision2 == null) {
                return applyCollision(collision1, request) || rayCast((Iterator) iterator1, maxDistance, request);
            } else {
                if (collision1.distance() < collision2.distance()) {
                    if (collision1.distance() > maxDistance) {
                        request.onRayCastFinish(RayCastRequest.FinishReason.MAX_DISTANCE);
                        return false;
                    }
                    if (applyCollision(collision1, request)) {
                        return true;
                    }
                    collision1 = iterator1.next();
                } else {
                    if (collision2.distance() > maxDistance) {
                        request.onRayCastFinish(RayCastRequest.FinishReason.MAX_DISTANCE);
                        return false;
                    }
                    if (applyCollision(collision2, request)) {
                        return true;
                    }
                    collision2 = iterator2.hasNext() ? iterator2.next() : null;
                }
            }
        } while (true);
    }

    private static <T extends HitBox> boolean rayCast(@NotNull Iterator<RayCastCollision<?>> iterator,
                                                      double maxDistance, RayCastRequest<T> request) {
        while (iterator.hasNext()) {
            RayCastCollision<?> collision = iterator.next();
            if (collision.distance() > maxDistance) {
                break;
            }
            if (applyCollision(collision, request)) {
                return true;
            }
        }
        request.onRayCastFinish(RayCastRequest.FinishReason.MAX_DISTANCE);
        return false;
    }

    private static <T extends HitBox> boolean applyCollision(RayCastCollision<?> collision,
                                                            RayCastRequest<T> request) {
        if (collision instanceof BlockCollision blockCollision) {
            if (blockCollision.isStep()) {
                if (request.onBlockStep(blockCollision.blockPos(), blockCollision.target())) {
                    request.onRayCastFinish(RayCastRequest.FinishReason.BLOCK_STEP);
                    return true;
                }
            } else {
                if (blockCollision.isInlet()) {
                    if (
                            request.onBlockIn(
                                    blockCollision.in(), blockCollision.inNormal(),
                                    blockCollision.out(), blockCollision.outNormal(),
                                    blockCollision.target())
                    ) {
                        request.onRayCastFinish(RayCastRequest.FinishReason.BLOCK_IN);
                        return true;
                    }
                } else {
                    if (
                            request.onBlockOut(
                                    blockCollision.in(), blockCollision.inNormal(),
                                    blockCollision.out(), blockCollision.outNormal(),
                                    blockCollision.target())
                    ) {
                        request.onRayCastFinish(RayCastRequest.FinishReason.BLOCK_OUT);
                        return true;
                    }
                }
            }
        } else if (collision instanceof HitBoxCollision<?> hitBoxCollision) {
            //noinspection unchecked
            HitBoxCollision<T> tHitBoxCollision = (HitBoxCollision<T>) hitBoxCollision;
            if (hitBoxCollision.isInlet()) {
                if (request.onHitBoxIn(hitBoxCollision.in(), hitBoxCollision.out(),
                        tHitBoxCollision.parent(), tHitBoxCollision.target())) {
                    request.onRayCastFinish(RayCastRequest.FinishReason.HITBOX_IN);
                    return true;
                }
            } else {
                if (request.onHitBoxOut(hitBoxCollision.in(), hitBoxCollision.out(),
                        tHitBoxCollision.parent(), tHitBoxCollision.target())) {
                    request.onRayCastFinish(RayCastRequest.FinishReason.HITBOX_OUT);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Creates a HitBoxGroup from a collection of HitBoxes.
     *
     * @param entities The entities to group.
     * @param <T>      The type of the HitBox.
     * @return The HitBoxGroup.
     */
    public static <T extends HitBox> HitBoxGroup<T> makeGroup(Collection<T> entities) {
        return new HitBoxGroupInternal<>(entities);
    }

    /**
     * Converts a Minestom Vec to a Euclidean Vector3D.
     *
     * @param pos The Minestom Vec to convert.
     * @return The Euclidean Vector3D.
     */
    public static Vector3D minestomToEuclidean(Vec pos) {
        return Vector3D.of(pos.x(), pos.y(), pos.z());
    }

    public static Vec euclideanToMinestorm(Vector3D vector3D) {
        return new Vec(vector3D.getX(), vector3D.getY(), vector3D.getZ());
    }
}
