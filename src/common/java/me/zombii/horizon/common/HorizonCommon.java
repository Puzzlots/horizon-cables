package me.zombii.horizon.common;

import dev.puzzleshq.puzzleloader.cosmic.game.GameRegistries;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.block.IModBlock;
import dev.puzzleshq.puzzleloader.cosmic.game.events.block.EventModBlockRegister;
import dev.puzzleshq.puzzleloader.cosmic.game.events.block.EventRegisterBlockEvent;
import dev.puzzleshq.puzzleloader.loader.mod.entrypoint.common.ModInit;
import finalforeach.cosmicreach.blocks.Block;
import finalforeach.cosmicreach.util.GameTag;
import me.zombii.horizon.common.be.energy.EnergyNetworkHubBlockEntity;
import me.zombii.horizon.common.be.power.PowerNetworkHubBlockEntity;
import me.zombii.horizon.common.blocks.energy.BatteryBlock;
import me.zombii.horizon.common.blocks.energy.EnergyHubBlock;
import me.zombii.horizon.common.blocks.power.PowerCableBlock;
import me.zombii.horizon.common.blocks.power.BatteryPowerBlock;
import me.zombii.horizon.common.blocks.power.PowerNetworkHubBlock;
import me.zombii.horizon.common.network.energy.nodes.EnergyBatteryNode;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;

public class HorizonCommon implements ModInit {

    public static final String NAMESPACE = "horizon";

    public HorizonCommon() {
    }

    @Override
    public void onInit() {
        GameRegistries.COSMIC_EVENT_BUS.register(this);

        PowerNetworkHubBlockEntity.register();
        EnergyNetworkHubBlockEntity.register();
    }

    @SubscribeEvent
    public void onEvent(EventModBlockRegister e) {
        e.register(new PowerCableBlock());
        e.register(new PowerNetworkHubBlock());
        e.register(new BatteryPowerBlock());

//        e.register(new FlatWireBlock());
//        e.register(new ItemPipeBlock());
//        e.register(new DataCableBlock());
        e.register(new EnergyHubBlock());
        e.register(new BatteryBlock());
    }

    @SubscribeEvent
    public void onEvent(EventRegisterBlockEvent e) {
//        e.getBlockEventsGenerator().
//        e.getBlockEventsGenerator().getTriggerMap()
    }

}
