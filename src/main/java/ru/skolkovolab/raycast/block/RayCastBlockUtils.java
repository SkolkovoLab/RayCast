package ru.skolkovolab.raycast.block;

import net.minestom.server.collision.BoundingBox;
import net.minestom.server.collision.ShapeImpl;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import ru.skolkovolab.raycast.shared.VecRel;
import ru.skolkovolab.raycast.shared.collision.BlockCollision;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author danirod12 - NTD STUDIOS
 */
public class RayCastBlockUtils {

    private record Intersection(
            VecRel in,
            Vec inNormal,
            VecRel out,
            Vec outNormal
    ) {}

    private static Intersection getIntersection(Point boxStart, Point boxEnd, Point[] from, Point[] dir) {
        double[] boxRaw = new double[]{boxStart.x(), boxStart.y(), boxStart.z(), boxEnd.x(), boxEnd.y(), boxEnd.z()};
        VecRel prev = null;
        Vec prevNormal = null;
        for (int i = 0; i < 6; i++) {
            int axis = i % 3;
            boolean isPositive = i >= 3;
            VecRel point = getPlaneIntersection(boxRaw[i], from[axis], dir[axis]).rotateNegative(axis);
            if (isInside(boxStart, boxEnd, point.vec())) {
                double nx = 0, ny = 0, nz = 0;
                if (axis == 0) nx = isPositive ? 1 : -1;
                if (axis == 1) ny = isPositive ? 1 : -1;
                if (axis == 2) nz = isPositive ? 1 : -1;
                Vec normal = new Vec(nx, ny, nz);
                if (prev == null) {
                    prev = point;
                    prevNormal = normal;
                } else {
                    if (prev.dist() > point.dist()) {
                        return new Intersection(point, normal, prev, prevNormal);
                    } else {
                        return new Intersection(prev, prevNormal, point, normal);
                    }
                }
            }
        }
        return prev == null ? null : new Intersection(prev, prevNormal, prev, prevNormal);
    }

    public static boolean isInside(Point boxStart, Point boxEnd, Point point) {
        return boxStart.x() <= point.x() && boxStart.y() <= point.y() && boxStart.z() <= point.z()
                && boxEnd.x() >= point.x() && boxEnd.y() >= point.y() && boxEnd.z() >= point.z();
    }

    public static VecRel getPlaneIntersection(double i, Point originRot, Point directionRot) {
        double t = (i - originRot.x()) / directionRot.x();
        double y = originRot.y() + directionRot.y() * t;
        double z = originRot.z() + directionRot.z() * t;
        return new VecRel(new Vec(i, y, z), t);
    }

    public static VecRel getBoundaryIntersection(Vec originRot, Point directionRot, long phaseAge) {
        int side = (int) Math.signum(directionRot.x());
        if (side == 0) {
            return null;
        }

        double x = Math.floor(originRot.x()) + (side == 1 ? 1 : 0) + side * phaseAge;
        return getPlaneIntersection(x, originRot, directionRot);
    }

    public static Vec rotate(Vec vec, int age) {
        return switch (age % 3) {
            case 2 -> new Vec(vec.z(), vec.x(), vec.y());
            case 1 -> new Vec(vec.y(), vec.z(), vec.x());
            default -> vec;
        };
    }

    public static Vec midBlockPos(Point vec1, Point vec2) {
        return new Vec(
                Math.floor((vec1.x() + vec2.x()) / 2),
                Math.floor((vec1.y() + vec2.y()) / 2),
                Math.floor((vec1.z() + vec2.z()) / 2)
        );
    }

    public static <T> void swap(T[] arr, int i, int j) {
        T temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static double getStartGrow(double origin, double direction) {
        double d = Math.abs(origin) % 1;
        if (d == 0) {
            return Math.signum(direction) == -1 ? 1 : 0;
        } else {
            return Math.signum(origin) == Math.signum(direction) ? d : (1 - d);
        }
    }

    public static BlockCollision buildBlockStep(
            Block.Getter getter,
            VecRel vec1,
            VecRel vec2
    ) {
        Vec mid = midBlockPos(vec1.vec(), vec2.vec());
        Block block = getter.getBlock(mid);
        return new Collision(vec1, null, vec2, null, mid, block, false, true);
    }

    public static List<BlockCollision> buildBlockCollisions(
            Block.Getter getter,
            Vec[] dir,
            VecRel vec1,
            VecRel vec2,
            boolean half
    ) {
        Vec mid = midBlockPos(vec1.vec(), vec2.vec());
        Block block = getter.getBlock(mid);

        if (block.isAir()) {
            return Collections.emptyList();
        }

        Vec loc = new Vec(mid.blockX(), mid.blockY(), mid.blockZ());
        Vec[] from = new Vec[]{
                vec1.vec(),
                rotate(vec1.vec(), 1),
                rotate(vec1.vec(), 2)
        };

        List<BoundingBox> boundingBoxes = ((ShapeImpl) block.registry().collisionShape()).boundingBoxes();

        return boundingBoxes.stream()
                .map(shape -> getIntersection(shape.relativeStart().add(loc), shape.relativeEnd().add(loc), from, dir))
                .filter(Objects::nonNull)
                .peek(intersection -> {
                    intersection.in.b += vec1.b;
                    intersection.out.b += vec1.b;
                })
                .filter(intersection -> !half || (intersection.in.b >= 0 && /* assertion */ intersection.out.b >= 0))
                .flatMap(intersection -> Stream.of(
                        (BlockCollision) new Collision(intersection.in, intersection.inNormal, intersection.out, intersection.outNormal, mid, block, true, false),
                        new Collision(intersection.in, intersection.inNormal, intersection.out, intersection.outNormal, mid, block, false, false)
                ))
                .sorted(Comparator.comparingDouble(c -> (c.distance() * 2) + (c.isOutlet() ? 1 : 0)))
                .toList();
    }

}
