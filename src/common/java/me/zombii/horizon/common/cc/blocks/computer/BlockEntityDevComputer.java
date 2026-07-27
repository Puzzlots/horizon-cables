package me.zombii.horizon.common.cc.blocks.computer;

import com.badlogic.gdx.utils.ByteArray;
import finalforeach.cosmicreach.blocks.BlockPosition;
import finalforeach.cosmicreach.blocks.blockentities.BlockEntity;
import finalforeach.cosmicreach.blocks.blockentities.BlockEntityCreator;
import finalforeach.cosmicreach.entities.player.Player;
import finalforeach.cosmicreach.io.ByteArrayUtils;
import finalforeach.cosmicreach.items.ISlotContainerParent;
import finalforeach.cosmicreach.items.ItemSlot;
import finalforeach.cosmicreach.items.ItemStack;
import finalforeach.cosmicreach.items.SlotContainerWindows;
import finalforeach.cosmicreach.items.containers.SlotContainer;
import finalforeach.cosmicreach.savelib.crbin.CRBinDeserializer;
import finalforeach.cosmicreach.savelib.crbin.CRBinSerializer;
import finalforeach.cosmicreach.singletons.GameSingletons;
import finalforeach.cosmicreach.world.Zone;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import me.zombii.horizon.common.HorizonTags;
import me.zombii.horizon.common.cc.blocks.bios.BlockEntityBiosFlasher;
import me.zombii.horizon.common.cc.computer.peripherals.PeripheralInstance;
import me.zombii.horizon.common.cc.computer.storage.nonportable.BiosChip;
import me.zombii.horizon.common.cc.items.IPeripheralBlockEntity;
import me.zombii.horizon.common.cc.items.IPeripheralItem;
import me.zombii.horizon.common.cc.lua.LuaCCLib;
import me.zombii.horizon.common.cc.lua.LuaScreenApi;
import me.zombii.horizon.common.cc.lua.bus.AddressableLuaEventBus;
import me.zombii.horizon.common.cc.lua.bus.SmartEventBusHandle;
import me.zombii.horizon.common.cc.packets.PacketSetComputerPower;
import me.zombii.horizon.common.cc.display.CCPalette;
import me.zombii.horizon.common.cc.display.CCScreen;
import me.zombii.horizon.common.cc.display.ICCPalette;
import me.zombii.horizon.common.cc.display.ICCScreen;
import me.zombii.horizon.common.screen.ScreenManager;
import me.zombii.horizon.common.screen.ScreenOpenInfo;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.LuaException;

import java.util.UUID;

public class BlockEntityDevComputer extends BlockEntity implements IPeripheralBlockEntity {

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
    private PeripheralInstance[] peripheralInstances;

    public ICCPalette palette;
    public ICCScreen screen;

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
        this.container.setParent(new ISlotContainerParent() {
            @Override
            public void onItemSlotUpdate(SlotContainer slotContainer, ItemSlot itemSlot) {
                if (luaState == null) return;
                if (!itemSlot.hasItemStack()) {
                    if (peripheralInstances[itemSlot.getSlotId()] == null) return;
                    peripheralInstances[itemSlot.getSlotId()].getHandle().freeHandle();
                    peripheralInstances[itemSlot.getSlotId()] = null;
                } else {
                    if (itemSlot.getItemStack().getItem() instanceof IPeripheralItem item) {
                        peripheralInstances[itemSlot.getSlotId()] = item.registerInstance(luaState, internalPeripheralEventBus.getNewAddress(), itemSlot.getItemStack());
                    }
                }
            }
        });

        if (zone != null) {
            this.palette = CCPalette.getOrMake(UUID.randomUUID(), 2)
                    .setColor(0, (short) 0)
                    .setColor(1, (short) -1);
            this.screen = CCScreen.getOrMake(UUID.randomUUID(), palette, 200, 200);
        }

        if (GameSingletons.isHost()) {
            this.internalPeripheralEventBus = new AddressableLuaEventBus();
            this.peripheralInstances = new PeripheralInstance[this.container.numberOfSlots + 1];
        }
    }

    public PeripheralInstance[] getPeripheralInstances() {
        return peripheralInstances;
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
                        SlotContainerWindows.add(container),
                        true
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
            if (luaState != null)
                luaState.close();
        }
    }

    @Override
    public String getBlockEntityId() {
        return BlockDevComputer.BE_ID.toString();
    }

    @Override
    public void write(CRBinSerializer serial) {
        super.write(serial);
        container.write(serial);
        serial.writeBoolean("power-state", powerState);

        ByteArray array = new ByteArray();
        write(array);
        serial.writeByteArray("screen", array.items);
    }

    @Override
    public void read(CRBinDeserializer deserial) {
        super.read(deserial);
        container.read(deserial);
        setPower(deserial.readBoolean("power-state", false));

        byte[] screen = deserial.readByteArray("screen");
        ByteBuf buf = Unpooled.wrappedBuffer(screen);
        receive(buf);
        buf.release();
    }

    public void receive(ByteBuf byteBuf) {
        long uuidAA = byteBuf.readLong();
        long uuidAB = byteBuf.readLong();
        long uuidBA = byteBuf.readLong();
        long uuidBB = byteBuf.readLong();

        UUID uuidA = new UUID(uuidAA, uuidAB);
        UUID uuidB = new UUID(uuidBA, uuidBB);

        int width = byteBuf.readInt();
        int height = byteBuf.readInt();
        int paletteSize = byteBuf.readInt();

        ICCPalette palette = CCPalette.getOrMake(uuidA, paletteSize);
        screen = CCScreen.getOrMake(uuidB, palette, width, height);
        screen.read(byteBuf.nioBuffer());

//        position = readBlockPositionZoneless(byteBuf);
    }

    public void write(ByteArray array) {
        ByteArrayUtils.writeLong(array, screen.getUUID().getMostSignificantBits());
        ByteArrayUtils.writeLong(array, screen.getUUID().getLeastSignificantBits());
        ByteArrayUtils.writeLong(array, screen.getPalette().getUUID().getMostSignificantBits());
        ByteArrayUtils.writeLong(array, screen.getPalette().getUUID().getLeastSignificantBits());

        ByteArrayUtils.writeInt(array, screen.getWidth());
        ByteArrayUtils.writeInt(array, screen.getHeight());
        ByteArrayUtils.writeInt(array, screen.getPalette().getSize());
        screen.write(array);

//        writeBlockPosition(position);
    }

    public boolean isOn() {
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
            this.luaState = LuaCCLib.newLua();
            LuaCCLib.inject(this);

            addPeriphs();

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

    private void addPeriphs() {
        container.forEachSlot((itemSlot) -> {
            if (!itemSlot.hasItemStack()) {
                if (peripheralInstances[itemSlot.getSlotId()] == null) return;

                peripheralInstances[itemSlot.getSlotId()].getHandle().freeHandle();
                peripheralInstances[itemSlot.getSlotId()] = null;
            } else {
                if (itemSlot.getItemStack().getItem() instanceof IPeripheralItem item) {
                    peripheralInstances[itemSlot.getSlotId()] = item.registerInstance(luaState, internalPeripheralEventBus.getNewAddress(), itemSlot.getItemStack());
                }
            }
        });
        this.peripheralInstances[this.container.numberOfSlots] = registerBE(luaState, internalPeripheralEventBus.getNewAddress(), this);
    }

    public void setPowerState(boolean state) {
        if (powerState == state) return;
        setPower(state);

        if (!GameSingletons.isHost()) {
            GameSingletons.client().sendAsClient(
                    new PacketSetComputerPower(this, state)
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

    @Override
    public PeripheralInstance registerBE(Lua L, SmartEventBusHandle handle, BlockEntity entity) {
        PeripheralInstance instance = register(L, handle);
        instance.begin();
        LuaScreenApi.push(instance, screen);
        instance.end();
        return instance;
    }

    @Override
    public String getPeripheralID() {
        return "cc:screen";
    }

    @Override
    public String getPeripheralType() {
        return "cc:screen";
    }
}