package me.zombii.horizon.common;

import dev.puzzleshq.puzzleloader.cosmic.game.GameRegistries;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.event.BlockEventGenerator;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.event.Trigger;
import dev.puzzleshq.puzzleloader.cosmic.game.blockloader.generation.event.TriggerGroup;
import dev.puzzleshq.puzzleloader.cosmic.game.events.block.EventModBlockRegister;
import dev.puzzleshq.puzzleloader.cosmic.game.events.block.EventRegisterBlockEvent;
import dev.puzzleshq.puzzleloader.cosmic.game.events.command.EventRegisterCommand;
import dev.puzzleshq.puzzleloader.cosmic.game.events.net.EventRegisterPacket;
import dev.puzzleshq.puzzleloader.loader.mod.entrypoint.common.ModInit;
import dev.puzzleshq.puzzleloader.loader.mod.entrypoint.common.PostModInit;
import finalforeach.cosmicreach.blocks.Block;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.singletons.GameSingletons;
import finalforeach.cosmicreach.util.Identifier;
import io.github.puzzle.cosmic.item.AbstractCosmicItem;
import me.zombii.horizon.common.cc.blocks.bios.BlockBiosFlasher;
import me.zombii.horizon.common.cc.blocks.bios.BlockEntityBiosFlasher;
import me.zombii.horizon.common.cc.blocks.computer.BlockDevComputer;
import me.zombii.horizon.common.cc.blocks.computer.BlockEntityDevComputer;
import me.zombii.horizon.common.cc.commands.CommandCheckBiosChip;
import me.zombii.horizon.common.cc.computer.storage.nonportable.BiosChip;
import me.zombii.horizon.common.cc.computer.storage.portable.BasicStorage;
import me.zombii.horizon.common.cc.items.BiosChipItem;
import me.zombii.horizon.common.cc.items.CompactDiscItem;
import me.zombii.horizon.common.cc.items.FloppyDiskItem;
import me.zombii.horizon.common.cc.items.HardDiskDriveItem;
import me.zombii.horizon.common.cc.packets.PacketFlashBIOS;
import me.zombii.horizon.common.cc.packets.PacketOpenScreen;
import me.zombii.horizon.common.cc.packets.PacketScreenState;
import me.zombii.horizon.common.cc.packets.PacketSetComputerPower;
import me.zombii.horizon.common.wired.be.energy.EnergyNetworkHubBlockEntity;
import me.zombii.horizon.common.wired.be.energy.SwitchBlockEntity;
import me.zombii.horizon.common.wired.blocks.energy.BatteryBlock;
import me.zombii.horizon.common.wired.blocks.energy.EnergyHubBlock;
import me.zombii.horizon.common.wired.blocks.energy.EnergyCableBlock;
import me.zombii.horizon.common.wired.blocks.imcables.*;
import me.zombii.horizon.common.wired.network.energy.nodes.EnergyNode;
import net.neoforged.bus.api.SubscribeEvent;
import org.hjson.JsonObject;

public class HorizonCommon implements ModInit, PostModInit {

    public static final String NAMESPACE = "horizon";

    public HorizonCommon() {
        GameRegistries.COSMIC_EVENT_BUS.register(this);
        GameRegistries.NETWORK_EVENT_BUS.register(this);
    }

    @Override
    public void onInit() {
//        PowerNetworkHubBlockEntity.register();
        SwitchBlockEntity.register();
//        EnergyNetworkHubBlockEntity.register();

        // Computer Mod
//        BlockEntityBiosFlasher.register();
//        BlockEntityDevComputer.register();

//        AbstractCosmicItem.register(new BiosChipItem());
//        AbstractCosmicItem.register(new FloppyDiskItem());
//        AbstractCosmicItem.register(new CompactDiscItem());
//        AbstractCosmicItem.register(new HardDiskDriveItem());

//        BiosChip.register();
//        BasicStorage.register();
    }

    @Override
    public void onPostInit() {
//        for (BlockState value : Block.getById("base:light").blockStates.values()) {
//            value.tags.add(HorizonTags.TAG_IMMERSIVE_RECEIVER);
//        }
    }

    @SubscribeEvent
    public void onEvent(EventRegisterCommand event) {
//        event.register(CommandCheckBiosChip::new, "check_bios_chip");
    }

    @SubscribeEvent
    public void onEvent(EventRegisterPacket e) {
//        e.registerPacket("open-screen", 9200, PacketOpenScreen.class);
//        e.registerPacket("flash-bios", 9201, PacketFlashBIOS.class);
//        e.registerPacket("set-computer-power", 9202, PacketSetComputerPower.class);
//        e.registerPacket("screen-update", 9203, PacketScreenState.class);
    }

    public static final String[] colors = {
            "azure", "black", "blue", "cyan",
            "dark-gray", "gray", "green", "light-gray",
            "lime", "magenta", "orange", "red",
            "rose", "spring-green", "violet", "white",
            "yellow"
    };

    public static final int UPDATES_PER_TICK = 30;

    @SubscribeEvent
    public void onEvent(EventModBlockRegister e) {
//        e.register(new EnergyCableBlock());
//        e.register(new EnergyHubBlock());
//        e.register(new BatteryBlock());

        // --
        for (String cableColor : colors) {
            e.register(new ColoredCableBlock(cableColor));
        }
        e.register(new PulseConverterBlock());
        e.register(new SwitchBlock());
        e.register(new InverterBlock());
        e.register(new DiodeBlock());
        e.register(new DelayBlock());
        // --

        // Computer Mod
//        e.register(new BlockBiosFlasher());
//        e.register(new BlockDevComputer());

        GameSingletons.updateObservers.add((d) -> {
            int budget = UPDATES_PER_TICK;
            while (!EnergyNode.buffer.isEmpty()) {
                if (budget == 0) return;
                Runnable r = EnergyNode.buffer.removeFirst();
                try {
                    r.run();
                } catch (Exception _) {}
                budget--;
            }
        });
    }

}
