package ru.skolengine.raycast.test.benchmark;

import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;
import ru.skolengine.raycast.RayCastRequest;
import ru.skolengine.raycast.entity.HitBox;
import ru.skolengine.raycast.entity.HitBoxGroup;
import ru.skolengine.raycast.shared.VecRel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.LongFunction;
import java.util.function.ToLongFunction;

public class BenchmarkRequest implements RayCastRequest<HitBox> {

    private static class Results {
        private long startTime = 0;

        private boolean hasIntersection = false;

        private long firstIntersection = 0;

        private long finishTime = 0;

        private long blockIn = 0;

        private long blockOut = 0;

        private long hitboxIn = 0;

        private long hitboxOut = 0;

        private long airCross = 0;

        public void start() {
            this.startTime = System.nanoTime();
            this.hasIntersection = false;
            this.finishTime = -1;
            this.firstIntersection = -1;
        }

        public long getBlockIn() {
            return blockIn;
        }

        public long getBlockOut() {
            return blockOut;
        }

        public long getHitboxIn() {
            return hitboxIn;
        }

        public long getHitboxOut() {
            return hitboxOut;
        }

        public long getAirCross() {
            return airCross;
        }

        public long prepareNanoTime() {
            return firstIntersection - startTime;
        }

        public long runNanoTime() {
            return finishTime - firstIntersection;
        }

        public long totalNanoTime() {
            return finishTime - startTime;
        }

    }

    private Results currentResults;

    private final List<Results> allResults = new ArrayList<>();

    private static String toStr(List<Results> r, ToLongFunction<Results> getter, LongFunction<String> map) {
        return "Min " + r.stream().mapToLong(getter).min().stream().mapToObj(map).findAny().orElse("No result") + "\t" +
               "Max " + r.stream().mapToLong(getter).max().stream().mapToObj(map).findAny().orElse("No result") + "\t" +
               "Avg " + r.stream().mapToLong(getter).average().stream().mapToLong(i -> (long)i).mapToObj(map).findAny().orElse("No result");
    }

    private static String toCountStr(List<Results> r, ToLongFunction<Results> getter) {
        return toStr(r, getter, (i) -> String.format("%5d", i));
    }

    private static String toTimeStr(List<Results> r, ToLongFunction<Results> getter) {
        return toStr(r, getter, BenchmarkRequest::timeToString);
    }

    public void printResult() {
        System.out.println(
                "\t Block in   " + toCountStr(allResults, Results::getBlockIn) + "\n" +
                "\t Block out  " + toCountStr(allResults, Results::getBlockOut) + "\n" +
                "\t Block air  " + toCountStr(allResults, Results::getAirCross) + "\n" +
                "\t Hitbox in  " + toCountStr(allResults, Results::getHitboxIn) + "\n" +
                "\t Hitbox out " + toCountStr(allResults, Results::getHitboxOut) + "\n" +
                "\t Prepare in " + toTimeStr(allResults, Results::prepareNanoTime) + "\n" +
                "\t Run in     " + toTimeStr(allResults, Results::runNanoTime) + "\n" +
                "\t Total in   " + toTimeStr(allResults, Results::totalNanoTime) + "\n"
        );
    }

    public void clear() {
        this.allResults.clear();
        this.currentResults = null;
    }

    public void start() {
        currentResults = new Results();
        currentResults.start();
    }

    private static String timeToString(long nano) {
        return String.format("%02d,%06d ms", (nano / 1_000_000), (nano % 1_000_000));
    }

    private void intersection() {
        if (!currentResults.hasIntersection) {
            currentResults.hasIntersection = true;
            currentResults.firstIntersection = System.nanoTime();
        }
    }

    @Override
    public boolean onBlockIn(@NotNull VecRel in, @NotNull VecRel out, Block block) {
        if (currentResults == null)
            return false;
        intersection();
        currentResults.blockIn++;
        return false;
    }

    @Override
    public boolean onBlockOut(@NotNull VecRel in, @NotNull VecRel out, Block block) {
        if (currentResults == null)
            return false;
        intersection();
        currentResults.blockOut++;
        return false;
    }

    public boolean onBlockAirCross(@NotNull VecRel in, @NotNull VecRel out, Block block) {
        if (currentResults == null)
            return false;
        intersection();
        currentResults.airCross++;
        return false;
    }

    public boolean onHitBoxIn(@NotNull VecRel in, @NotNull VecRel out, HitBoxGroup<HitBox> parent, HitBox box) {
        if (currentResults == null)
            return false;
        intersection();
        currentResults.hitboxIn++;
        return false;
    }

    public boolean onHitBoxOut(@NotNull VecRel in, @NotNull VecRel out, HitBoxGroup<HitBox> parent, HitBox box) {
        if (currentResults == null)
            return false;
        intersection();
        currentResults.hitboxOut++;
        return false;
    }

    @Override
    public void onRayCastFinish(FinishReason reason) {
        if (currentResults == null)
            return;
        currentResults.finishTime = System.nanoTime();
        allResults.add(currentResults);
        currentResults = null;
    }
}
