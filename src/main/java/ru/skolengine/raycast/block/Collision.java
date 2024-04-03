package ru.skolengine.raycast.block;

import net.minestom.server.instance.block.Block;
import ru.skolengine.raycast.shared.VecRel;
import ru.skolengine.raycast.shared.collision.BlockCollision;

public record Collision(VecRel in, VecRel out, Block target, boolean isInlet) implements BlockCollision {
}
