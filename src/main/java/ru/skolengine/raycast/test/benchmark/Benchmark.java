package ru.skolengine.raycast.test.benchmark;

import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import ru.skolengine.raycast.RayCastTool;
import ru.skolengine.raycast.entity.HitBox;
import ru.skolengine.raycast.entity.HitBoxGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class Benchmark {

    private static final Vec pose = new Vec(0, 0, 0);

    private static final Vec dir = new Vec(1, 0, 3).normalize();

    /**
     * block 100 256 hitbox 100 256 512 20 10 5000 10 all 100 256 512 20 10 5000 10
     * */
    public static void main(String[] args) {
        int i = 0;
        while (i < args.length ) {
            switch (args[i]) {
                case "hitbox" -> i = hitBoxTest(args, i);
                case "block" -> i = blockTest(args, i);
                case "all" -> i = allTest(args, i);
                default -> i++;
            }
        }
    }

    /**
     * Запускает тест с блоками со следующими параметрами. <br/>
     * count - количество повторов. <br/>
     * distance - расстояние рейкаста. <br/>
     * **/
    private static int blockTest(String[] params, int pi) {
        int count;
        double distance;
        try {
            count = Integer.parseInt(params[++pi]);
            distance = Double.parseDouble(params[++pi]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e ) {
            return pi;
        }

        Block.Getter blockProducer = createBlockProducer();
        BenchmarkRequest request = new BenchmarkRequest();

        //Prepared run
        for (int i = 0; i < 100; i++) {
            request.start();
            RayCastTool.rayCast(blockProducer, null, pose, dir, distance, request);
            request.clear();
        }
        for (int i = 0; i < count; i++) {
            request.start();
            RayCastTool.rayCast(blockProducer, null, pose, dir, distance, request);
        }
        System.out.println("Hitbox for\t" +
                           "\titerations " + count +
                           "\tdistance " + distance
        );
        request.printResult();
        request.clear();
        return pi + 1;
    }

    /**
     * Запускает тест с хитбоксами со следующими параметрами. <br/>
     * count - количество повторов. <br/>
     * distance - расстояние рейкаста. <br/>
     * hitBoxDistance - расстояние до которого будут располагаться хитбоксы. <br/>
     * activeCount - количество активных групп хитбоксом. <br/>
     * activeSize - количество хитбоксов в активных группах. <br/>
     * inactiveCount - количество неактивных групп хитбоксом. <br/>
     * inactiveSize - количество хитбоксов в неактивных группах. <br/>
     * Неактивные группы потенциально не могут быть достижимы, должны быть отсеяны на этапе инициальизации. <br/>
     * Активные группы могут быть пересечены, будут проверяться. <br/>
     * **/
    private static int hitBoxTest(String[] params, int pi) {
        int count;
        double distance;
        double hitBoxDistance;
        int activeCount;
        int activeSize;
        int inactiveCount;
        int inactiveSize;
        try {
            count = Integer.parseInt(params[++pi]);
            distance = Double.parseDouble(params[++pi]);
            hitBoxDistance = Double.parseDouble(params[++pi]);
            activeCount = Integer.parseInt(params[++pi]);
            activeSize = Integer.parseInt(params[++pi]);
            inactiveCount = Integer.parseInt(params[++pi]);
            inactiveSize = Integer.parseInt(params[++pi]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e ) {
            return pi;
        }

        Collection<HitBoxGroup<HitBox>> hitBoxGroups = createHitBoxes(hitBoxDistance, activeCount, activeSize, inactiveCount, inactiveSize);
        BenchmarkRequest request = new BenchmarkRequest();

        //Prepared run
        for (int i = 0; i < 100; i++) {
            request.start();
            RayCastTool.rayCast(null, hitBoxGroups, pose, dir, distance, request);
            request.clear();
        }
        for (int i = 0; i < count; i++) {
            request.start();
            RayCastTool.rayCast(null, hitBoxGroups, pose, dir, distance, request);
        }
        System.out.println("Hitbox for\t" +
                           "\titerations " + count +
                           "\tdistance " + distance +
                           "\thitBoxDistance " + hitBoxDistance +
                           "\tactiveCount " + activeCount +
                           "\tactiveSize " + activeSize +
                           "\tinactiveCount " + inactiveCount +
                           "\tinactiveSize " + inactiveSize
                           );
        request.printResult();
        request.clear();
        return pi + 1;
    }

    /**
     * Запускает тест с хитбоксами и блоками со следующими параметрами. <br/>
     * count - количество повторов. <br/>
     * distance - расстояние рейкаста. <br/>
     * hitBoxDistance - расстояние до которого будут располагаться хитбоксы. <br/>
     * activeCount - количество активных групп хитбоксом. <br/>
     * activeSize - количество хитбоксов в активных группах. <br/>
     * inactiveCount - количество неактивных групп хитбоксом. <br/>
     * inactiveSize - количество хитбоксов в неактивных группах. <br/>
     * Неактивные группы потенциально не могут быть достижимы, должны быть отсеяны на этапе инициальизации. <br/>
     * Активные группы могут быть пересечены, будут проверяться. <br/>
     * **/
    private static int allTest(String[] params, int pi) {
        int count;
        double distance;
        double hitBoxDistance;
        int activeCount;
        int activeSize;
        int inactiveCount;
        int inactiveSize;
        try {
            count = Integer.parseInt(params[++pi]);
            distance = Double.parseDouble(params[++pi]);
            hitBoxDistance = Double.parseDouble(params[++pi]);
            activeCount = Integer.parseInt(params[++pi]);
            activeSize = Integer.parseInt(params[++pi]);
            inactiveCount = Integer.parseInt(params[++pi]);
            inactiveSize = Integer.parseInt(params[++pi]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e ) {
            return pi;
        }

        Block.Getter blockProducer = createBlockProducer();
        Collection<HitBoxGroup<HitBox>> hitBoxGroups = createHitBoxes(hitBoxDistance, activeCount, activeSize, inactiveCount, inactiveSize);
        BenchmarkRequest request = new BenchmarkRequest();

        //Prepared run
        for (int i = 0; i < 100; i++) {
            request.start();
            RayCastTool.rayCast(blockProducer, hitBoxGroups, pose, dir, distance, request);
            request.clear();
        }
        for (int i = 0; i < count; i++) {
            request.start();
            RayCastTool.rayCast(blockProducer, hitBoxGroups, pose, dir, distance, request);
        }
        System.out.println("All for\t" +
                           "\titerations " + count +
                           "\tdistance " + distance +
                           "\thitBoxDistance " + hitBoxDistance +
                           "\tactiveCount " + activeCount +
                           "\tactiveSize " + activeSize +
                           "\tinactiveCount " + inactiveCount +
                           "\tinactiveSize " + inactiveSize
        );
        request.printResult();
        request.clear();
        return pi + 1;
    }

    private static Block.Getter createBlockProducer() {
        return new BlockProducer();
    }

    private static Collection<HitBoxGroup<HitBox>> createHitBoxes(double distance, int activeCount, int activeSize, int inactiveCount, int inactiveSize) {
        List<HitBoxGroup<HitBox>> list = new ArrayList<>();
        Random r = new Random(0);
        for (int i = 0; i < activeCount; i++) {
            list.add(new BenchmarkHitboxGroup(r, activeSize, 5,
                    pose.x() + dir.x() * distance * i / activeCount,
                    pose.y() + dir.y() * distance * i / activeCount,
                    pose.z() + dir.z() * distance * i / activeCount)
            );
        }
        for (int i = 0; i < inactiveCount; i++) {
            list.add(new BenchmarkHitboxGroup(r, inactiveSize, 5,
                    pose.x() + dir.x() * distance * (-i) / inactiveCount,
                    pose.y() + dir.y() * distance * (-i) / inactiveCount,
                    pose.z() + dir.z() * distance * (-i) / inactiveCount)
            );
        }
        return list;
    }

}
