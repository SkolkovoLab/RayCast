package ru.skolengine.raycast.test.benchmark;

import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;
public class BlockProducer implements Block.Getter {

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public @UnknownNullability Block getBlock(int x, int y, int z, @NotNull Condition condition) {
        if (z < 0)
            return Block.VOID_AIR;
        int v = x % 3 + y % 3;
        return switch (v) {
            case 0 -> Block.SANDSTONE_STAIRS;
            case 1 -> Block.AIR;
            case 2 -> Block.BIRCH_SLAB;
            case 3 -> Block.SAND;
            default -> Block.STONE;
        };
    }
}