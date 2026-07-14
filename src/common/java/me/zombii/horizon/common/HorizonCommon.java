package me.zombii.horizon.common;

import dev.puzzleshq.puzzleloader.cosmic.game.GameRegistries;
import dev.puzzleshq.puzzleloader.cosmic.game.events.block.EventModBlockRegister;
import dev.puzzleshq.puzzleloader.cosmic.game.events.block.EventRegisterBlockEvent;
import dev.puzzleshq.puzzleloader.loader.mod.entrypoint.common.ModInit;
import me.zombii.horizon.common.cc.blocks.BiosFlasherBlock;
import me.zombii.horizon.common.wired.be.energy.EnergyNetworkHubBlockEntity;
import me.zombii.horizon.common.wired.blocks.energy.BatteryBlock;
import me.zombii.horizon.common.wired.blocks.energy.EnergyHubBlock;
import me.zombii.horizon.common.wired.blocks.energy.PowerCableBlock;
import net.neoforged.bus.api.SubscribeEvent;

public class HorizonCommon implements ModInit {

    public static final String NAMESPACE = "horizon";

    public HorizonCommon() {
    }

    @Override
    public void onInit() {
        GameRegistries.COSMIC_EVENT_BUS.register(this);

//        PowerNetworkHubBlockEntity.register();
        EnergyNetworkHubBlockEntity.register();
    }

    @SubscribeEvent
    public void onEvent(EventModBlockRegister e) {
        e.register(new PowerCableBlock());
        e.register(new EnergyHubBlock());
        e.register(new BatteryBlock());
        e.register(new BiosFlasherBlock());
    }

    @SubscribeEvent
    public void onEvent(EventRegisterBlockEvent e) {
//        e.getBlockEventsGenerator().
//        e.getBlockEventsGenerator().getTriggerMap()
    }

}
