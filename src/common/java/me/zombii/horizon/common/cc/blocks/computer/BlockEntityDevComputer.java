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
import me.zombii.horizon.common.cc.items.IPeripheralItem;
import me.zombii.horizon.common.cc.lua.LuaCCLib;
import me.zombii.horizon.common.cc.lua.bus.AddressableLuaEventBus;
import me.zombii.horizon.common.cc.lua.bus.SmartEventBusHandle;
import me.zombii.horizon.common.cc.packets.PacketToggleComputer;
import me.zombii.horizon.common.screen.ScreenManager;
import me.zombii.horizon.common.screen.ScreenOpenInfo;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.LuaException;
import party.iroiro.luajava.lua55.Lua55;

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
    private Lua luaState;
    private AddressableLuaEventBus internalPeripheralEventBus;

    public static boolean isValidBiosChip(ItemStack stack) {
        return BlockEntityBiosFlasher.isValid(stack);
    }

    public static boolean isValidStorageMedia(ItemStack stack) {
        if (stack == null) return false;
        return stack.hasTag(HorizonTags.TAG_STORAGE_DEVICE) &&
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
            this.internalPeripheralEventBus = new AddressableLuaEventBus();
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
        return BlockDevComputer.BE_ID.toString();
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
            if (!BlockEntityBiosFlasher.chipIsInitialized(container.getSlot(0).getItemStack())) return;
            this.luaState = new Lua55();
            LuaCCLib.inject(this);

            try {
                luaState.run("cc.bios.init()");
            } catch (LuaException e) {
                System.out.println(e.getMessage());
            }
        } else {
            if (luaState != null) {
                internalPeripheralEventBus.reset();
                luaState.close();
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

    public AddressableLuaEventBus getInternalBus() {
        return internalPeripheralEventBus;
    }

    public BiosChip getBios() {
        ItemStack stack = container.getSlot(0).getItemStack();
        if (BlockEntityBiosFlasher.chipIsInitialized(stack)) {
            BiosChip chip = BlockEntityBiosFlasher.getOrCreateBiosChip(stack);
            assert chip != null;
            return chip;
        }
        return null;
    }
}