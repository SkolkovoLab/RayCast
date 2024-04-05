package ru.skolengine.raycast.test.benchmark;

import net.minestom.server.coordinate.Vec;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.apache.commons.geometry.euclidean.threed.rotation.QuaternionRotation;
import ru.skolengine.raycast.entity.HitBox;

import java.util.Random;

public class BenchmarkHitbox implements HitBox {

    private final QuaternionRotation rr;

    private final QuaternionRotation lr;

    private final Vec pos;

    private final Vec scale;

    public BenchmarkHitbox(Random r, double maxSize, double x, double y, double z) {
        rr = QuaternionRotation.fromAxisAngle(
                Vector3D.of(r.nextDouble() + 0.1, r.nextDouble() + 0.1, r.nextDouble() + 0.1),
                r.nextDouble(360)
        );
        lr = QuaternionRotation.fromAxisAngle(
                Vector3D.of(r.nextDouble() + 0.1, r.nextDouble() + 0.1, r.nextDouble() + 0.1),
                r.nextDouble(360)
        );
        pos = new Vec(x, y, z);
        maxSize /= 2;
        scale = new Vec(r.nextDouble(maxSize), r.nextDouble(maxSize), r.nextDouble(maxSize));
    }

    @Override
    public Vec getHitBoxPosition() {
        return pos;
    }

    @Override
    public QuaternionRotation getHitBoxLeftRotation() {
        return lr;
    }

    @Override
    public Vec getHitBoxScale() {
        return scale;
    }

    @Override
    public QuaternionRotation getHitBoxRightRotation() {
        return rr;
    }
}
