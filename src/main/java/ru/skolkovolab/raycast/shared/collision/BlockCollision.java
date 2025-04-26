package ru.skolkovolab.raycast.shared.collision;

import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;

public non-sealed interface BlockCollision extends RayCastCollision<Block> {
    Vec blockPos();
    Vec inNormal();
    Vec outNormal();
    boolean isStep();
}
