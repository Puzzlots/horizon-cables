package me.zombii.horizon.common.cc.blocks.peripherals.screen;

import finalforeach.cosmicreach.blocks.BlockPosition;
import finalforeach.cosmicreach.blocks.blockentities.BlockEntity;
import finalforeach.cosmicreach.blocks.blockentities.BlockEntityCreator;
import finalforeach.cosmicreach.entities.player.Player;
import finalforeach.cosmicreach.items.ItemStack;
import finalforeach.cosmicreach.items.SlotContainerWindows;
import finalforeach.cosmicreach.savelib.crbin.CRBinDeserializer;
import finalforeach.cosmicreach.savelib.crbin.CRBinSerializer;
import finalforeach.cosmicreach.singletons.GameSingletons;
import finalforeach.cosmicreach.world.Zone;
import me.zombii.horizon.common.HorizonTags;
import me.zombii.horizon.common.cc.blocks.bios.BlockBiosFlasher;
import me.zombii.horizon.common.cc.blocks.bios.BlockEntityBiosFlasher;
import me.zombii.horizon.common.cc.blocks.computer.BlockDevComputer;
import me.zombii.horizon.common.cc.blocks.computer.ContainerDevComputer;
import me.zombii.horizon.common.cc.computer.storage.nonportable.BiosChip;
import me.zombii.horizon.common.cc.lua.LuaCCLib;
import me.zombii.horizon.common.cc.lua.bus.AddressableLuaEventBus;
import me.zombii.horizon.common.cc.packets.PacketToggleComputer;
import me.zombii.horizon.common.screen.ScreenManager;
import me.zombii.horizon.common.screen.ScreenOpenInfo;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.LuaException;
import party.iroiro.luajava.lua55.Lua55;

public class BlockEntityScreenPeripheral extends BlockEntity {

    public static void register() {
        BlockEntityCreator.registerBlockEntityCreator(
                BlockScreenPeripheral.BE_ID.toString(),
                (block, zone, x, y, z) ->
                        new BlockEntityScreenPeripheral(zone, x, y, z)
        );
    }

    public BlockEntityScreenPeripheral() {
        this(null, 0, 0, 0);
    }

    public BlockEntityScreenPeripheral(Zone zone, int globalX, int globalY, int globalZ) {
        super(zone, globalX, globalY, globalZ);
    }

    @Override
    public void onRemove() {
        super.onRemove();
    }

    @Override
    public String getBlockEntityId() {
        return BlockScreenPeripheral.BE_ID.toString();
    }

    @Override
    public boolean isTicking() {
        return false;
    }

    @Override
    public void write(CRBinSerializer serial) {
        super.write(serial);
    }

    @Override
    public void read(CRBinDeserializer deserial) {
        super.read(deserial);
    }

}