package ru.skolengine.raycast.test.benchmark;

import org.apache.commons.geometry.euclidean.threed.Vector3D;
import ru.skolengine.raycast.entity.HitBox;
import ru.skolengine.raycast.entity.HitBoxGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class BenchmarkHitboxGroup implements HitBoxGroup<HitBox> {

    private final double size;

    private final Vector3D center;

    private final Collection<HitBox> hitboxes;

    public BenchmarkHitboxGroup(Random r, int hc, double size, double x, double y, double z) {
        this.size = size;
        this.center = Vector3D.of(x, y, z);
        List<HitBox> hitboxList = new ArrayList<>();
        for (int i = 0; i < hc; i++) {
            double part = r.nextDouble();
            double maxSize = size * part;
            double d = (size - maxSize) / 2;
            hitboxList.add(new BenchmarkHitbox(r, maxSize, x + r.nextDouble(d), y + r.nextDouble(d), z + r.nextDouble(d)));
        }
        this.hitboxes = hitboxList;
    }

    @Override
    public Collection<HitBox> getHitBoxCollection() {
        return hitboxes;
    }

    @Override
    public Vector3D getHitBoxGroupCenter() {
        return center;
    }

    @Override
    public Double getHitBoxGroupRadius() {
        return size;
    }
}
