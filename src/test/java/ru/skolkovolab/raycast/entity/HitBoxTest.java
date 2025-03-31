package ru.skolkovolab.raycast.entity;

import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.apache.commons.geometry.euclidean.threed.rotation.QuaternionRotation;
import org.jetbrains.annotations.NotNull;

/**
 * @author sidey383
 */
public class HitBoxTest extends Entity implements HitBox {
    private final HitBoxType type;

    public HitBoxTest(EntityType type) {
        super(type);
        if (type.equals(EntityType.ITEM_DISPLAY)) {
            this.type = HitBoxType.ITEM_DISPLAY_NONE;
            ItemDisplayMeta m = (ItemDisplayMeta) super.getEntityMeta();
            m.setItemStack(ItemStack.of(Material.DIRT));
        } else if (type.equals(EntityType.BLOCK_DISPLAY)) {
            this.type = HitBoxType.BLOCK_DISPLAY;
            BlockDisplayMeta m = (BlockDisplayMeta) super.getEntityMeta();
            m.setBlockState(Block.DIRT);
        } else {
            throw new IllegalArgumentException();
        }
        getEntityMeta().setHasNoGravity(true);
    }

    @Override
    public @NotNull AbstractDisplayMeta getEntityMeta() {
        return (AbstractDisplayMeta) super.getEntityMeta();
    }

    @Override
    public Vec getHitBoxPosition() {
        return getPosition().add(getEntityMeta().getTranslation()).asVec();
    }

    @Override
    public Vec getHitBoxScale() {
        return getEntityMeta().getScale();
    }

    @Override
    public QuaternionRotation getHitBoxLeftRotation() {
        float[] floats = getEntityMeta().getLeftRotation();
        return QuaternionRotation.of(floats[3], floats[0], floats[1], floats[2]);
    }

    @Override
    public QuaternionRotation getHitBoxRightRotation() {
        float[] floats = getEntityMeta().getRightRotation();
        return QuaternionRotation.of(floats[3], floats[0], floats[1], floats[2]);
    }

    @Override
    public HitBoxType getHitBoxType() {
        return type;
    }
}
