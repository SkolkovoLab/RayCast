package ru.skolengine.raycast.shared;

import net.minestom.server.coordinate.Vec;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import ru.skolengine.raycast.block.RayCastBlockUtils;

import static ru.skolengine.raycast.RayCastTool.euclideanToMinestorm;

public class VecRel extends Pair<Vec, Double> {
    public VecRel(Vec origin, Vec directionNormalized, double dist) {
        this.a = origin.add(directionNormalized.mul(dist));
        this.b = dist;
    }

    public VecRel(Vector3D origin, Vector3D directionNormalized, double dist) {
        this(euclideanToMinestorm(origin), euclideanToMinestorm(directionNormalized), dist);
    }

    public VecRel(Vec vec, double dist) {
        this.a = vec;
        this.b = dist;
    }

    public VecRel(Vector3D vec, double dist) {
        this(euclideanToMinestorm(vec), dist);
    }

    public VecRel rotate(int age) {
        this.a = RayCastBlockUtils.rotate(this.a, age);
        return this;
    }

    public VecRel rotateNegative(int age) {
        return this.rotate(3 - age);
    }

    public Vec vec() {
        return this.a;
    }

    public double dist() {
        return this.b;
    }
}
