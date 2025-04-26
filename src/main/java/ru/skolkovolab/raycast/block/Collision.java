package ru.skolkovolab.raycast.block;

import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import ru.skolkovolab.raycast.shared.VecRel;
import ru.skolkovolab.raycast.shared.collision.BlockCollision;

public record Collision(
        VecRel in,
        Vec inNormal,
        VecRel out,
        Vec outNormal,
        Vec blockPos,
        Block target,
        boolean isInlet,
        boolean isStep
) implements BlockCollision {
}
