package ru.skolengine.raycast.entity.tool;

import net.minestom.server.coordinate.Vec;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.apache.commons.geometry.euclidean.threed.rotation.QuaternionRotation;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * @author sidey383
 */
public class RayCastHitBoxUtils {

    public static <T extends L, L extends Comparable<? super T>> void sortedInsert(List<L> list, T value) {
        int n = Collections.binarySearch(list, value);
        if (n >= 0)
            list.add(n, value);
        else
            list.add(-(n + 1), value);
    }

    public static void applyRotation(Vector3D[] v, QuaternionRotation r) {
        for (int i = 0; i < v.length; i++)
            v[i] = r.apply(v[i]);
    }

    public static void applyScale(Vector3D[] va, Vec scale) {
        for (int i = 0; i < va.length; i++) {
            Vector3D v = va[i];
            va[i] = Vector3D.of(v.getX() * scale.x(), v.getY() * scale.y(), v.getZ() * scale.z());
        }
    }

    public static double @Nullable [] rhombohedronDownIntersection(Vector3D lStart, Vector3D lDir, Vector3D[] rDirection, Vector3D rStart) {
        Vector3D rEnd = rStart.add(rDirection[0]).add(rDirection[1]).add(rDirection[2]);
        Double val1 = null;
        double val2;
        Double cur;
        for (int i = 1; i < 3; i++) {
            for (int j = 0; j < i; j++) {
                cur = rhomboidDownIntersection(lStart, lDir, rStart, rDirection[i], rDirection[j]);
                if (cur != null) {
                    if (val1 == null) {
                        val1 = cur;
                    } else {
                        val2 = cur;
                        if (val1 < val2)
                            return new double[]{val1, val2};
                        else
                            return new double[]{val2, val1};
                    }
                }
                cur = rhomboidDownIntersection(lStart, lDir, rEnd, rDirection[i].multiply(-1), rDirection[j].multiply(-1));
                if (cur != null) {
                    if (val1 == null) {
                        val1 = cur;
                    } else {
                        val2 = cur;
                        if (val1 < val2)
                            return new double[]{val1, val2};
                        else
                            return new double[]{val2, val1};
                    }
                }
            }
        }
        return null;
    }

    public static double @Nullable [] rhombohedronCenterIntersection(Vector3D lStart, Vector3D lDir, Vector3D[] rDirection, Vector3D rStart) {
        Double val1 = null;
        double val2;
        Double cur;
        for (int i = 0; i < 3; i++) {
            cur = rhomboidCenterIntersection(lStart, lDir, rStart.add(rDirection[i]), rDirection[(i + 1) % 3], rDirection[(i + 2) % 3]);
            if (cur != null) {
                if (val1 == null) {
                    val1 = cur;
                } else {
                    val2 = cur;
                    if (val1 < val2)
                        return new double[]{val1, val2};
                    else
                        return new double[]{val2, val1};
                }
            }
            cur = rhomboidCenterIntersection(lStart, lDir, rStart.add(-1, rDirection[i]), rDirection[(i + 1) % 3], rDirection[(i + 2) % 3]);
            if (cur != null) {
                if (val1 == null) {
                    val1 = cur;
                } else {
                    val2 = cur;
                    if (val1 < val2)
                        return new double[]{val1, val2};
                    else
                        return new double[]{val2, val1};
                }
            }
        }
        return null;
    }

    // l0 + ld * d
    // r0 + rd1 * u + rd2 * v
    // d = ((rd1 X rd2)(l0 - ro))/ (-ld (rd1xrd2))
    // u = ((rd2 X (-ld))(l0 - ro))/ (-ld (rd1xrd2))
    // v = (((-ld) X rd1)(l0 - ro))/ (-ld (rd1xrd2))
    public static @Nullable Double rhomboidDownIntersection(Vector3D l0, Vector3D ld, Vector3D r0, Vector3D rd1, Vector3D rd2) {
        Vector3D rd1xrd2 = rd1.cross(rd2);
        Vector3D mld = ld.multiply(-1);
        double divider = mld.dot(rd1xrd2);
        if (divider == 0)
            return null;
        Vector3D l0r0 = l0.add(-1, r0);
        double u = rd2.cross(mld).dot(l0r0) / divider;
        double v = mld.cross(rd1).dot(l0r0) / divider;
        if (u < 0 || u > 1 || v < 0 || v > 1)
            return null;
        double d = rd1xrd2.dot(l0r0) / divider;
        return d;
    }

    public static @Nullable Double rhomboidCenterIntersection(Vector3D l0, Vector3D ld, Vector3D r0, Vector3D rd1, Vector3D rd2) {
        Vector3D rd1xrd2 = rd1.cross(rd2);
        Vector3D mld = ld.multiply(-1);
        double divider = mld.dot(rd1xrd2);
        if (divider == 0)
            return null;
        Vector3D l0r0 = l0.add(-1, r0);
        double u = rd2.cross(mld).dot(l0r0) / divider;
        double v = mld.cross(rd1).dot(l0r0) / divider;
        if (u < -1 || u > 1 || v < -1 || v > 1)
            return null;
        double d = rd1xrd2.dot(l0r0) / divider;
        return d;
    }

}
