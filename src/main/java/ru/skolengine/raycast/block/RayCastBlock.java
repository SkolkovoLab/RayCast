package ru.skolengine.raycast.block;

import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;
import ru.skolengine.raycast.shared.RayCastIterator;
import ru.skolengine.raycast.shared.VecRel;
import ru.skolengine.raycast.shared.collision.BlockCollision;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

/**
 * @author danirod12 - NTD STUDIOS
 */
public class RayCastBlock implements Iterable<BlockCollision> {
    private final Block.Getter getter;
    private final Vec origin;
    private final Vec direction;

    private RayCastBlock(Block.Getter getter, Vec origin, Vec direction) {
        this.getter = getter;
        this.origin = origin;
        this.direction = direction;
    }

    public static RayCastBlock createRaycast(Block.Getter getter, Vec origin, Vec direction) {
        return new RayCastBlock(getter, origin, direction);
    }

    @NotNull
    @Override
    public Iterator<BlockCollision> iterator() {
        return new RayCastBlockIterator(this.getter, this.origin, this.direction);
    }

    private static class RayCastBlockIterator implements RayCastIterator<BlockCollision> {
        private final Block.Getter getter;
        private final Vec[] origin;
        private final Vec[] direction;

        private final double[] dirRaw;
        private final double[] xyz;
        private final long[] lastXyz = new long[3];

        private final VecRel[] pairs = new VecRel[3];
        private VecRel prev = null;

        private final Deque<BlockCollision> deque = new ArrayDeque<>(3);
        private BlockCollision current = null;

        public RayCastBlockIterator(Block.Getter getter, Vec originBase, Vec originDirection) {
            this.getter = getter;

            this.origin = new Vec[3];
            this.origin[0] = originBase;
            this.origin[1] = RayCastBlockUtils.rotate(this.origin[0], 1);
            this.origin[2] = RayCastBlockUtils.rotate(this.origin[0], 2);

            this.direction = new Vec[3];
            this.direction[0] = originDirection.normalize();
            this.direction[1] = RayCastBlockUtils.rotate(this.direction[0], 1);
            this.direction[2] = RayCastBlockUtils.rotate(this.direction[0], 2);

            this.dirRaw = new double[]{
                    Math.abs(this.direction[0].x()),
                    Math.abs(this.direction[0].y()),
                    Math.abs(this.direction[0].z())
            };

            this.xyz = new double[]{
                    RayCastBlockUtils.getStartGrow(this.origin[0].x(), this.direction[0].x()),
                    RayCastBlockUtils.getStartGrow(this.origin[0].y(), this.direction[0].y()),
                    RayCastBlockUtils.getStartGrow(this.origin[0].z(), this.direction[0].z())
            };
        }

        @Override
        public double distance() {
            return this.current == null ? -1 : current().distance();
        }

        @Override
        public BlockCollision current() {
            return this.current;
        }

        @Override
        public boolean hasCurrent() {
            return this.current != null;
        }

        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public BlockCollision next() {
            while (this.deque.isEmpty()) {
                int index = 0;
                for (int ordinal = 0; ordinal < 3; ordinal++) {
                    this.xyz[ordinal] += this.dirRaw[ordinal];
                    if ((long) this.xyz[ordinal] > this.lastXyz[ordinal]) {
                        long age = (long) this.xyz[ordinal];
                        this.lastXyz[ordinal] = age;

                        VecRel pair = RayCastBlockUtils.getBoundaryIntersection(
                                this.origin[ordinal],
                                this.direction[ordinal],
                                age - 1
                        );
                        assert pair != null && pair.dist() >= 0;
                        pair.rotateNegative(ordinal);
                        this.pairs[index++] = pair;
                    }
                }

                if (index == 2) {
                    if (this.pairs[0].dist() < this.pairs[1].dist()) {
                        this.pairs[2] = this.pairs[1];
                        this.pairs[1] = this.pairs[0];
                        this.pairs[0] = this.pairs[2];
                    }
                } else if (index == 3) {
                    if (this.pairs[0].dist() < this.pairs[1].dist()) {
                        RayCastBlockUtils.swap(this.pairs, 0, 1);
                    }
                    if (this.pairs[1].dist() < this.pairs[2].dist()) {
                        RayCastBlockUtils.swap(this.pairs, 1, 2);
                    }
                    if (this.pairs[0].dist() < this.pairs[1].dist()) {
                        RayCastBlockUtils.swap(this.pairs, 0, 1);
                    }
                }

                while (index-- > 0) {
                    boolean half = false;
                    VecRel pair = this.prev;
                    this.prev = this.pairs[index];

                    if (pair == null) {
                        half = true;
                        pair = new VecRel(this.origin[0], 0);
                    }
                    this.deque.addAll(RayCastBlockUtils.buildBlockCollisions(this.getter, this.direction,
                            pair, this.prev, half));
                }
            }
            return this.current = this.deque.pop();
        }
    }
}
