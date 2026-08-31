package me.zombii.horizon.common;

import dev.puzzleshq.puzzleloader.cosmic.game.GameRegistries;
import dev.puzzleshq.puzzleloader.cosmic.game.events.block.EventModBlockRegister;
import dev.puzzleshq.puzzleloader.cosmic.game.events.command.EventRegisterCommand;
import dev.puzzleshq.puzzleloader.cosmic.game.events.net.EventRegisterPacket;
import dev.puzzleshq.puzzleloader.loader.mod.entrypoint.common.ModInit;
import dev.puzzleshq.puzzleloader.loader.mod.entrypoint.common.PostModInit;
import me.zombii.horizon.common.wired.blocks.imcables.*;
import me.zombii.horizon.immersivecables.ImEventManager;
import me.zombii.horizon.immersivecables.be.*;
import net.neoforged.bus.api.SubscribeEvent;

public class HorizonCommon implements ModInit, PostModInit {

    public static final String NAMESPACE = "horizon";

    public HorizonCommon() {
        GameRegistries.COSMIC_EVENT_BUS.register(this);
        GameRegistries.NETWORK_EVENT_BUS.register(this);
    }

    @Override
    public void onInit() {
        ImEventManager.register();

        WireBE.register();
        SwitchBE.register();
        DiodeBE.register();
        LaserPulserBE.register();
        LogicGateBE.register();
        ClockBE.register();
        ButtonBE.register();

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

    @SubscribeEvent
    public void onEvent(EventModBlockRegister e) {
        // --
        for (String cableColor : colors) {
            e.register(new IColoredWireBlock(cableColor));
        }
        e.register(new IExposedWireBlock());
        e.register(new IButtonBlock());
        e.register(new ISwitchBlock());
        e.register(new IDiodeBlock());
        e.register(new INotGateBlock());
        e.register(new ILowPulserBlock());
        e.register(new IPulserBlock());
        e.register(new IHighPulserBlock());
        e.register(new IDelayBlock());
        e.register(new IClockBlock());
        e.register(new IAndGateBlock());
        e.register(new IXorGateBlock());
        e.register(new IOrGateBlock());
        // --

        // Computer Mod
//        e.register(new BlockBiosFlasher());
//        e.register(new BlockDevComputer());
    }

}
