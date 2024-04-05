package ru.skolengine.raycast;

import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;
import ru.skolengine.raycast.entity.HitBox;
import ru.skolengine.raycast.entity.HitBoxGroup;
import ru.skolengine.raycast.shared.VecRel;

/**
 * @author danirod12 - NTD STUDIOS
 */
public interface RayCastRequest<H extends HitBox> {
    /**
     * Called when the raycast hits a block (join).
     *
     * @param in    The position of the raycast when it hits the block on join.
     * @param block The block that was hit.
     * @return Whether the raycast should finish.
     */
    default boolean onBlockIn(@NotNull VecRel in, @NotNull VecRel out, Block block) {
        return false;
    }

    /**
     * Called when the raycast hits a block (leave).
     *
     * @param in    The position of the raycast when it hits the block on join.
     * @param out   The position of the raycast when it hits the block on leave.
     * @param block The block that was hit.
     * @return Whether the raycast should finish.
     */
    default boolean onBlockOut(@NotNull VecRel in, @NotNull VecRel out, Block block) {
        return false;
    }

    /**
     * Called when the raycast cross an air block
     *
     * @param in    The position of the raycast when it hits the block on join.
     * @param out   The position of the raycast when it hits the block on leave.
     * @param block The block that was hit.
     * @return Whether the raycast should finish.
     */
    default boolean onBlockAirCross(@NotNull VecRel in, @NotNull VecRel out, Block block) {
        return false;
    }

    /**
     * Called when the raycast hits a hitbox (join).
     *
     * @param in  The position of the raycast when it hits the hitbox on join.
     * @param box The hitbox that was hit.
     * @return Whether the raycast should finish.
     */
    default boolean onHitBoxIn(@NotNull VecRel in, @NotNull VecRel out, HitBoxGroup<H> parent, H box) {
        return false;
    }

    /**
     * Called when the raycast hits a hitbox (leave).
     *
     * @param in  The position of the raycast when it hits the hitbox on join.
     * @param out The position of the raycast when it hits the hitbox on leave.
     * @param box The hitbox that was hit.
     * @return Whether the raycast should finish.
     */
    default boolean onHitBoxOut(@NotNull VecRel in, @NotNull VecRel out, HitBoxGroup<H> parent, H box) {
        return false;
    }

    /**
     * Called when the raycast finishes.
     *
     * @param reason The reason why the raycast finished.
     */
    default void onRayCastFinish(FinishReason reason) {
    }

    enum FinishReason {
        BLOCK_IN,
        BLOCK_OUT,
        AIR_CROSS,
        HITBOX_IN,
        HITBOX_OUT,
        MAX_DISTANCE
    }
}
