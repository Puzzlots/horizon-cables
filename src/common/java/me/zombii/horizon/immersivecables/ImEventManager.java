package me.zombii.horizon.immersivecables;

import finalforeach.cosmicreach.blocks.BlockPosition;
import finalforeach.cosmicreach.blocks.blockentities.BlockEntity;
import finalforeach.cosmicreach.singletons.GameSingletons;
import finalforeach.cosmicreach.util.constants.Direction;
import finalforeach.cosmicreach.world.Zone;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ImEventManager {

    public static final int PER_TICK_EVENT_BUDGET = HorizonCablesConfig.INSTANCE.perTickEventBudget;
    private static final Object2ObjectMap<Zone, Queue<Runnable>> PER_ZONE_QUEUE = new Object2ObjectArrayMap<>();
    private static final ThreadLocal<BlockPosition> BP = ThreadLocal.withInitial(BlockPosition::new);

    public static void register() {
        GameSingletons.updateObservers.add((_) -> runQueued());
    }

    public static void runQueued(Zone zone) {
        Queue<Runnable> zoneQueue = PER_ZONE_QUEUE.get(zone);
        if (zoneQueue == null) return;

        int budget = PER_TICK_EVENT_BUDGET;
        while (!zoneQueue.isEmpty()) {
            if (budget <= 0) break;
            zoneQueue.poll().run();
            budget--;
        }
    }

    public static void runQueued() {
        for (Zone value : PER_ZONE_QUEUE.keySet()) {
            runQueued(value);
        }
    }

    public static void resetWorld() {
        for (Zone value : PER_ZONE_QUEUE.keySet()) {
            resetZone(value);
        }
        PER_ZONE_QUEUE.clear();
    }

    public static void queueEvent(Zone zone, int gX, int gY, int gZ, boolean turnOn, Direction direction) {
        if (!PER_ZONE_QUEUE.containsKey(zone)) PER_ZONE_QUEUE.put(zone, new ConcurrentLinkedDeque<>());

//        System.err.println("queued " + zone + " ( " + gX + ", " + gY + ", " + gZ + ") " + direction + " " + turnOn);

        PER_ZONE_QUEUE.get(zone).add(() -> {
            BlockPosition position = BP.get().setGlobal(zone, gX, gY, gZ);
            BlockEntity entity = position.getBlockEntity();
            if (!(entity instanceof IEnergyBE energyBE)) return;
//            System.err.println("running " + zone + " ( " + gX + ", " + gY + ", " + gZ + ") " + direction + " " + turnOn + " " + entity);
            if (turnOn) {
                energyBE.turnOn(direction);
            } else {
                energyBE.turnOff(direction);
            }
        });

//        if (position.getBlockEntity() instanceof IEnergyBE energyBE) {
//            energyBE.turnOn();
//        }
    }

    public static void resetZone(Zone z) {
        Queue<Runnable> runnables = PER_ZONE_QUEUE.get(z);
        if (runnables == null) return;
        runnables.clear();
    }
}
