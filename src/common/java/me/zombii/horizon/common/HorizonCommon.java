package me.zombii.horizon.common;

import dev.puzzleshq.puzzleloader.cosmic.game.GameRegistries;
import dev.puzzleshq.puzzleloader.cosmic.game.events.block.EventModBlockRegister;
import dev.puzzleshq.puzzleloader.cosmic.game.events.block.EventRegisterBlockEvent;
import dev.puzzleshq.puzzleloader.cosmic.game.events.command.EventRegisterCommand;
import dev.puzzleshq.puzzleloader.cosmic.game.events.net.EventRegisterPacket;
import dev.puzzleshq.puzzleloader.loader.mod.entrypoint.common.ModInit;
import io.github.puzzle.cosmic.item.AbstractCosmicItem;
import me.zombii.horizon.common.cc.blocks.computer.BlockDevComputer;
import me.zombii.horizon.common.cc.blocks.computer.BlockEntityDevComputer;
import me.zombii.horizon.common.cc.blocks.bios.BlockBiosFlasher;
import me.zombii.horizon.common.cc.blocks.bios.BlockEntityBiosFlasher;
import me.zombii.horizon.common.cc.commands.CommandCheckBiosChip;
import me.zombii.horizon.common.cc.computer.storage.nonportable.BiosChip;
import me.zombii.horizon.common.cc.computer.storage.portable.BasicStorage;
import me.zombii.horizon.common.cc.items.BiosChipItem;
import me.zombii.horizon.common.cc.items.CompactDiskItem;
import me.zombii.horizon.common.cc.items.FloppyDiskItem;
import me.zombii.horizon.common.cc.items.HardDiskDriveItem;
import me.zombii.horizon.common.cc.packets.PacketFlashBIOS;
import me.zombii.horizon.common.cc.packets.PacketOpenScreen;
import me.zombii.horizon.common.cc.packets.PacketToggleComputer;
import me.zombii.horizon.common.wired.be.energy.EnergyNetworkHubBlockEntity;
import me.zombii.horizon.common.wired.blocks.energy.BatteryBlock;
import me.zombii.horizon.common.wired.blocks.energy.EnergyHubBlock;
import me.zombii.horizon.common.wired.blocks.energy.PowerCableBlock;
import net.neoforged.bus.api.SubscribeEvent;

public class HorizonCommon implements ModInit {

    public static final String NAMESPACE = "horizon";

    public HorizonCommon() {
        GameRegistries.COSMIC_EVENT_BUS.register(this);
        GameRegistries.NETWORK_EVENT_BUS.register(this);
    }

    @Override
    public void onInit() {

//        PowerNetworkHubBlockEntity.register();
        EnergyNetworkHubBlockEntity.register();

        // Computer Mod
        BlockEntityBiosFlasher.register();
        BlockEntityDevComputer.register();

        AbstractCosmicItem.register(new BiosChipItem());
        AbstractCosmicItem.register(new FloppyDiskItem());
        AbstractCosmicItem.register(new CompactDiskItem());
        AbstractCosmicItem.register(new HardDiskDriveItem());

        BiosChip.register();
        BasicStorage.register();
    }

    @SubscribeEvent
    public void onEvent(EventRegisterCommand event) {
        event.register(CommandCheckBiosChip::new, "check_bios_chip");
    }

    @SubscribeEvent
    public void onEvent(EventRegisterPacket e) {
        e.registerPacket("open-screen", 9200, PacketOpenScreen.class);
        e.registerPacket("flash-bios", 9201, PacketFlashBIOS.class);
        e.registerPacket("set-computer-power", 9202, PacketToggleComputer.class);
    }

    @SubscribeEvent
    public void onEvent(EventModBlockRegister e) {
        e.register(new PowerCableBlock());
        e.register(new EnergyHubBlock());
        e.register(new BatteryBlock());

        // Computer Mod
        e.register(new BlockBiosFlasher());
        e.register(new BlockDevComputer());
    }

    @SubscribeEvent
    public void onEvent(EventRegisterBlockEvent e) {
//        e.getBlockEventsGenerator().
//        e.getBlockEventsGenerator().getTriggerMap()
    }

}
