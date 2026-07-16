package me.zombii.horizon.common.cc.blocks.computer;

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
import me.zombii.horizon.common.cc.computer.storage.nonportable.BiosChip;
import me.zombii.horizon.common.cc.lua.LuaCCLib;
import me.zombii.horizon.common.cc.lua.LuaEventBus;
import me.zombii.horizon.common.cc.packets.PacketToggleComputer;
import me.zombii.horizon.common.screen.ScreenManager;
import me.zombii.horizon.common.screen.ScreenOpenInfo;
import org.jspecify.annotations.Nullable;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.luajit.LuaJit;
import party.iroiro.luajava.value.LuaFunction;
import party.iroiro.luajava.value.LuaValue;

import java.io.*;

public class BlockEntityDevComputer extends BlockEntity {

    public static void register() {
        BlockEntityCreator.registerBlockEntityCreator(
                BlockDevComputer.BE_ID.toString(),
                (block, zone, x, y, z) ->
                        new BlockEntityDevComputer(zone, x, y, z)
        );
    }

    private final ContainerDevComputer container;
    private boolean powerState;
    private LuaJit luaState;
    private LuaEventBus internalPeripheralEventBus;

    public static boolean isValidBiosChip(ItemStack stack) {
        return BlockEntityBiosFlasher.isValid(stack);
    }

    public static boolean isValidNonPortableMedia(ItemStack stack) {
        if (stack == null) return false;
        return stack.hasTag(HorizonTags.TAG_NON_PORTABLE_STORAGE) &&
                stack.getItem().hasIntProperty("storage-size");
    }

    public static boolean isValidPortableMedia(ItemStack stack) {
        if (stack == null) return false;
        return stack.hasTag(HorizonTags.TAG_PORTABLE_STORAGE) &&
                stack.getItem().hasIntProperty("storage-size");
    }

    public ContainerDevComputer getContainer() {
        return container;
    }

    public BlockEntityDevComputer() {
        this(null, 0, 0, 0);
    }

    public BlockEntityDevComputer(Zone zone, int globalX, int globalY, int globalZ) {
        super(zone, globalX, globalY, globalZ);
        this.container = new ContainerDevComputer(3);
        if (GameSingletons.isHost()) {
            this.luaState = new LuaJit();
            this.internalPeripheralEventBus = new LuaEventBus();
            LuaCCLib.inject(this);
        }
    }

    private final BlockPosition position = new BlockPosition();

    @Override
    public void onInteract(Player player, Zone zone) {
        super.onInteract(player, zone);

        if (GameSingletons.isHost()) {
            try {
                position.setGlobal(zone, getGlobalX(), getGlobalY(), getGlobalZ());
                ScreenOpenInfo info = new ScreenOpenInfo(
                        player,
                        BlockDevComputer.SCREEN_ID,
                        position,
                        null,
                        SlotContainerWindows.add(container)
                );
                ScreenManager.openScreen(info);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRemove() {
        super.onRemove();
        if (GameSingletons.isHost()) {
            luaState.close();
        }
    }

    @Override
    public String getBlockEntityId() {
        return BlockBiosFlasher.BE_ID.toString();
    }

    @Override
    public boolean isTicking() {
        return false;
    }

    @Override
    public void write(CRBinSerializer serial) {
        super.write(serial);
        container.write(serial);
        serial.writeBoolean("power-state", powerState);
    }

    @Override
    public void read(CRBinDeserializer deserial) {
        super.read(deserial);
        container.read(deserial);
        setPower(deserial.readBoolean("power-state", false));
    }

    public boolean getPowerState() {
        return powerState;
    }

    private void setPower(boolean powerState) {
        this.powerState = powerState;

        if (!GameSingletons.isHost()) {
            return;
        }

        if (powerState) {
            if (!container.getSlot(0).hasItemStack()) return;
            BiosChip chip = BlockEntityBiosFlasher.getOrCreateBiosChip(container.getSlot(0).getItemStack());
            assert chip != null;
            DataInputStream stream = new DataInputStream(new ByteArrayInputStream(chip.getData()));
            try {
                int byteCount = stream.readInt();
                byte[] data = new byte[byteCount];
                stream.read(data);
                stream.close();

                String code = new String(data);
                luaState.run(code);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void setPowerState(boolean state) {
        if (powerState == state) return;
        setPower(state);

        if (!GameSingletons.isHost()) {
            GameSingletons.client().sendAsClient(
                    new PacketToggleComputer(this, state)
            );
        }
    }

    public Lua getLuaState() {
        return luaState;
    }

    public LuaEventBus getInternalPeripheralEventBus() {
        return internalPeripheralEventBus;
    }
}