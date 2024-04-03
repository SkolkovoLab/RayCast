package ru.skolengine.raycast.entity.fallback;

import org.apache.commons.geometry.euclidean.threed.Vector3D;
import ru.skolengine.raycast.entity.HitBox;
import ru.skolengine.raycast.entity.HitBoxGroup;

import java.util.Collection;

/**
 * @author sidey383
 */
public class HitBoxGroupInternal<T extends HitBox> implements HitBoxGroup<T> {
    private final Collection<T> displays;

    private final Vector3D v;

    private final double radius;

    public HitBoxGroupInternal(Collection<T> displays) {
        this.displays = displays;
        double x = displays.stream().mapToDouble(d -> d.getHitBoxPosition().x()).average().orElse(0);
        double y = displays.stream().mapToDouble(d -> d.getHitBoxPosition().y()).average().orElse(0);
        double z = displays.stream().mapToDouble(d -> d.getHitBoxPosition().z()).average().orElse(0);
        radius = displays.stream()
                .mapToDouble(d ->
                        (x - d.getHitBoxPosition().x()) +
                                (y - d.getHitBoxPosition().y()) +
                                (z - d.getHitBoxPosition().z()) +
                                Math.max(Math.abs(d.getHitBoxScale().x()), Math.max(Math.abs(d.getHitBoxScale().y()), Math.abs(d.getHitBoxScale().z()))) * 2
                ).max().orElse(0);
        v = Vector3D.of(x, y, z);
    }

    @Override
    public Collection<T> getHitBoxCollection() {
        return displays;
    }

    @Override
    public Vector3D getHitBoxGroupCenter() {
        return v;
    }

    @Override
    public Double getHitBoxGroupRadius() {
        return radius;
    }
}
