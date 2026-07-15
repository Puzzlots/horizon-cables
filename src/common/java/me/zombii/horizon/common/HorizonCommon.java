package me.zombii.horizon.common;

import dev.puzzleshq.puzzleloader.cosmic.game.GameRegistries;
import dev.puzzleshq.puzzleloader.cosmic.game.events.block.EventModBlockRegister;
import dev.puzzleshq.puzzleloader.cosmic.game.events.block.EventRegisterBlockEvent;
import dev.puzzleshq.puzzleloader.cosmic.game.events.net.EventRegisterPacket;
import dev.puzzleshq.puzzleloader.cosmic.game.network.packet.PacketInterceptor;
import dev.puzzleshq.puzzleloader.loader.mod.entrypoint.common.ModInit;
import io.github.puzzle.cosmic.item.AbstractCosmicItem;
import me.zombii.horizon.common.cc.blocks.BiosFlasherBlock;
import me.zombii.horizon.common.cc.blocks.BiosFlasherBlockEntity;
import me.zombii.horizon.common.cc.computer.storage.nonportable.BiosChip;
import me.zombii.horizon.common.cc.computer.storage.nonportable.HardDrive;
import me.zombii.horizon.common.cc.computer.storage.portable.FloppyDisk;
import me.zombii.horizon.common.cc.computer.storage.portable.MiniCDRWDisk;
import me.zombii.horizon.common.cc.computer.storage.portable.MiniCDRomDisk;
import me.zombii.horizon.common.cc.items.BiosChipItem;
import me.zombii.horizon.common.cc.items.CompactDiskItem;
import me.zombii.horizon.common.cc.items.FloppyDiskItem;
import me.zombii.horizon.common.cc.packets.PacketOpenScreen;
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
        BiosFlasherBlockEntity.register();

        AbstractCosmicItem.register(new BiosChipItem());
        AbstractCosmicItem.register(new FloppyDiskItem());
        AbstractCosmicItem.register(new CompactDiskItem());

        BiosChip.register();
        FloppyDisk.register();
        MiniCDRomDisk.register();
        MiniCDRWDisk.register();
        HardDrive.register();
    }

    @SubscribeEvent
    public void onEvent(EventRegisterPacket e) {
        e.registerPacket("open-screen", 9200, PacketOpenScreen.class);
    }

    @SubscribeEvent
    public void onEvent(EventModBlockRegister e) {
        e.register(new PowerCableBlock());
        e.register(new EnergyHubBlock());
        e.register(new BatteryBlock());

        // Computer Mod
        e.register(new BiosFlasherBlock());
    }

    @SubscribeEvent
    public void onEvent(EventRegisterBlockEvent e) {
//        e.getBlockEventsGenerator().
//        e.getBlockEventsGenerator().getTriggerMap()
    }

}
