package ru.skolkovolab.raycast;

import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.color.Color;
import net.minestom.server.command.builder.Command;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.world.DimensionType;
import org.apache.commons.geometry.euclidean.threed.rotation.QuaternionRotation;
import org.apache.commons.numbers.quaternion.Quaternion;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.skolkovolab.raycast.entity.HitBox;
import ru.skolkovolab.raycast.entity.HitBoxGroup;
import ru.skolkovolab.raycast.shared.VecRel;
import ru.skolkovolab.raycast.entity.HitBoxTest;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class RayCast {

    private static final Logger log = LoggerFactory.getLogger(RayCast.class);

    public static void main(String[] args) {
        MinecraftServer server = MinecraftServer.init();

        InstanceContainer world = MinecraftServer.getInstanceManager()
                .createInstanceContainer(DimensionType.OVERWORLD);
        world.setChunkSupplier(LightingChunk::new);
        for (int x = -10; x <= 10; x++) {
            for (int z = -10; z <= 10; z++) {
                world.setBlock(x, 10, z, Block.GRASS_BLOCK, true);
            }
        }
        world.setBlock(3, 13, 3, Block.SPRUCE_STAIRS.withProperty("facing", "north"), true);
        world.setBlock(3, 13, 4, Block.SPRUCE_STAIRS.withProperty("facing", "south"), true);
        world.setBlock(2, 15, 5, Block.SPRUCE_STAIRS.withProperty("facing", "west"), true);
        world.setBlock(2, 16, 5, Block.YELLOW_STAINED_GLASS_PANE, true);
        world.setBlock(5, 15, 5, Block.STONE_STAIRS.withProperty("facing", "east"), true);

        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            event.setSpawningInstance(world);
            event.getPlayer().setRespawnPoint(new Pos(0, 11, 0));
            event.getPlayer().setGameMode(GameMode.CREATIVE);
            event.getPlayer().getInventory().addItemStack(ItemStack.of(Material.GRASS_BLOCK));
            event.getPlayer().getInventory().addItemStack(ItemStack.of(Material.GLASS));
            event.getPlayer().getInventory().addItemStack(ItemStack.of(Material.STONE_STAIRS));
        });

        Set<HitBox> entities = randomEntitySet(world);

        try {
            Class.forName("ru.skolkovolab.raycast.block.RayCastBlock.class");
        } catch (ClassNotFoundException ignored) {
        }

        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        Map<Vec, List<Color>> map = new HashMap<>();
        Command command = new Command("ray");
        command.setDefaultExecutor((sender, context) -> {
            if (sender instanceof Player player) {
                map.clear();
                long ns = System.nanoTime();
                try {
                    RayCastTool.rayCast(world, List.of(RayCastTool.makeGroup(entities)),
                            player.getPosition().asVec().add(0, player.getEyeHeight(), 0),
                            player.getPosition().direction(), new RayCastRequest<>() {
                                @Override
                                public boolean onHitBoxOut(@NotNull VecRel in, @NotNull VecRel out,
                                                           HitBoxGroup<HitBox> p, HitBox box) {
                                    map.computeIfAbsent(in.vec(), vec -> new ArrayList<>())
                                            .add(new Color(0, 0, 255));
                                    map.computeIfAbsent(out.vec(), vec -> new ArrayList<>())
                                            .add(new Color(69, 69, 69));
                                    return false;
                                }

                                @Override
                                public boolean onBlockOut(
                                        @NotNull VecRel in, @NotNull Vec inNormal,
                                        @NotNull VecRel out, @NotNull Vec outNormal,
                                        Block block
                                ) {
                                    map.computeIfAbsent(in.vec(), vec -> new ArrayList<>())
                                            .add(new Color(255, 0, 0));
                                    map.computeIfAbsent(out.vec(), vec -> new ArrayList<>())
                                            .add(new Color(0, 255, 0));
                                    map.computeIfAbsent(in.vec().add(inNormal.mul(0.1)), vec -> new ArrayList<>())
                                            .add(new Color(255, 0, 0));
                                    map.computeIfAbsent(out.vec().add(outNormal.mul(0.1)), vec -> new ArrayList<>())
                                            .add(new Color(0, 255, 0));
                                    return false;
                                }

                                @Override
                                public void onRayCastFinish(RayCastRequest.FinishReason reason) {
                                    long ns1 = System.nanoTime() - ns;
                                    player.sendActionBar(Component.text("§6Finished in "
                                            + ns1 + " ns (" + (int) ((ns1 / 1_000_000D) * 100) / 100D + " ms)"));
                                }
                            });
                } catch (Exception exception) {
                    log.warn("RayCast exception", exception);
                }
//                player.sendMessage("Ray cast from: " + player.getPosition().asVec());
                player.sendMessage("Wrote " + map.size()
                        + " (" + map.values().stream().mapToInt(List::size).sum() + ") targets!");
            }
        });
        MinecraftServer.getCommandManager().register(command);
        command = new Command("ray2");
        command.setDefaultExecutor((sender, context) -> {
            if (sender instanceof Player player) {
                map.clear();
                Vec vec = player.getPosition().asVec().add(0, player.getEyeHeight(), 0);
                Vec dir = player.getPosition().direction().normalize().mul(0.1);
                for (int i = 0; i < 500; i++) {
                    vec = vec.add(dir);
                    map.computeIfAbsent(vec, elem -> new ArrayList<>()).add(new Color(255, 0, 0));
                }
            }
        });
        MinecraftServer.getCommandManager().register(command);
        command = new Command("ray3");
        command.setDefaultExecutor((sender, context) -> {
            if (sender instanceof Player) {
                atomicBoolean.set(!atomicBoolean.get());
            }
        });
        MinecraftServer.getCommandManager().register(command);

        MinecraftServer.getSchedulerManager().buildTask(() -> {
            if (atomicBoolean.get()) {
                for (Player player : world.getPlayers()) {
                    MinecraftServer.getCommandManager().execute(player, "ray");
                }
            }
        }).repeat(Duration.ofMillis(250)).schedule();

        MinecraftServer.getSchedulerManager().buildTask(() -> {
            for (Map.Entry<Vec, List<Color>> vec : map.entrySet()) {
                for (Player player : world.getPlayers()) {
                    for (Color color : vec.getValue()) {
                        player.sendPacket(new ParticlePacket(
                                Particle.DUST.withProperties(color, 0.5f),
                                false,
                                true,
                                vec.getKey(),
                                Vec.ZERO,
                                0,
                                1
                        ));
                    }
                }
            }
        }).repeat(Duration.ofMillis(100)).schedule();

        server.start("0.0.0.0", 25565);
    }

    public static Set<HitBox> randomEntitySet(Instance world) {
        Random r = new Random(1);
        Set<HitBox> testCollection = new HashSet<>();
        for (int i = 0; i < 5; i++) {
            HitBoxTest test = new HitBoxTest(EntityType.ITEM_DISPLAY);
            Quaternion rq = QuaternionRotation.of(r.nextFloat(), r.nextFloat(), r.nextFloat(), r.nextFloat())
                    .getQuaternion().normalize();
            Quaternion lq = QuaternionRotation.of(r.nextFloat(), r.nextFloat(), r.nextFloat(), r.nextFloat())
                    .getQuaternion().normalize();
            Vec scale = new Vec(0.5 + r.nextDouble(), 0.5 + r.nextDouble(), 0.5 + r.nextDouble());
            test.getEntityMeta().setLeftRotation(
                    new float[]{(float) rq.getX(), (float) rq.getY(), (float) rq.getZ(), (float) rq.getW()});
            test.getEntityMeta().setRightRotation(
                    new float[]{(float) lq.getX(), (float) lq.getY(), (float) lq.getZ(), (float) lq.getW()});
            test.getEntityMeta().setScale(scale);
            test.getEntityMeta().setTranslation(new Vec(r.nextDouble(), r.nextDouble(), r.nextDouble()));
            test.setInstance(world, new Pos(5 - 10 * (i % 2), 15, 5 - 10 * ((i >> 1) % 2))).join();
            testCollection.add(test);
        }
        return testCollection;
    }

}
