package me.zombii.horizon.common.cc.blocks.bios;

import dev.puzzleshq.puzzleloader.loader.util.RawAssetLoader;
import dev.puzzleshq.puzzleloader.loader.util.ResourceLocation;
import finalforeach.cosmicreach.blocks.BlockPosition;
import finalforeach.cosmicreach.blocks.blockentities.BlockEntity;
import finalforeach.cosmicreach.blocks.blockentities.BlockEntityCreator;
import finalforeach.cosmicreach.chat.Chat;
import finalforeach.cosmicreach.entities.player.Player;
import finalforeach.cosmicreach.items.ItemStack;
import finalforeach.cosmicreach.items.SlotContainerWindows;
import finalforeach.cosmicreach.networking.server.ServerSingletons;
import finalforeach.cosmicreach.savelib.crbin.CRBinDeserializer;
import finalforeach.cosmicreach.savelib.crbin.CRBinSerializer;
import finalforeach.cosmicreach.singletons.GameSingletons;
import finalforeach.cosmicreach.util.Identifier;
import finalforeach.cosmicreach.util.assets.GameAssetLoader;
import finalforeach.cosmicreach.world.Zone;
import me.zombii.horizon.common.HorizonTags;
import me.zombii.horizon.common.cc.computer.storage.AbstractDataStorageDevice;
import me.zombii.horizon.common.cc.computer.storage.nonportable.BiosChip;
import me.zombii.horizon.common.cc.packets.PacketFlashBIOS;
import me.zombii.horizon.common.screen.ScreenManager;
import me.zombii.horizon.common.screen.ScreenOpenInfo;

import javax.naming.SizeLimitExceededException;

public class BlockEntityBiosFlasher extends BlockEntity {

    public static void register() {
        BlockEntityCreator.registerBlockEntityCreator(
                BlockBiosFlasher.BE_ID.toString(),
                (block, zone, x, y, z) ->
                        new BlockEntityBiosFlasher(zone, x, y, z)
        );
    }

    private final ContainerBiosFlasher container;

    public static boolean isValid(ItemStack stack) {
        if (stack == null) return false;
        return stack.hasTag(HorizonTags.TAG_BIOS_CHIP) &&
                stack.getItem().hasIntProperty("bios-chip-size");
    }

    public ContainerBiosFlasher getContainer() {
        return container;
    }

    public BlockEntityBiosFlasher() {
        this(null, 0, 0, 0);
    }

    public BlockEntityBiosFlasher(Zone zone, int globalX, int globalY, int globalZ) {
        super(zone, globalX, globalY, globalZ);
        this.container = new ContainerBiosFlasher(1);
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
                        BlockBiosFlasher.SCREEN_ID,
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
        serial.writeObj("bios-chip", container.getSlot(0).getItemStack());
    }

    @Override
    public void read(CRBinDeserializer deserial) {
        super.read(deserial);
        container.getSlot(0).setItemStack(deserial.readObj("bios-chip", ItemStack.class));
    }

    public static boolean chipIsInitialized(ItemStack stack) {
        if (!isValid(stack)) {
            return false;
        }

        BiosChip chip = (BiosChip) AbstractDataStorageDevice.COMPONENTS_BY_ID.get(stack.stackMetadata.getInt("bios-chip-id", -1));
        if (chip == null) {
            return false;
        }

        return true;
    }

    public static BiosChip getOrCreateBiosChip(ItemStack stack) {
        if (!isValid(stack)) {
            return null;
        }

        BiosChip chip = (BiosChip) AbstractDataStorageDevice.COMPONENTS_BY_ID.get(stack.stackMetadata.getInt("bios-chip-id", -1));
        if (chip == null) {
            chip = new BiosChip(
                    stack.getItem().getIntProperty("bios-chip-size", -1)
            );
            chip.init();
            stack.setMetadataInt("bios-chip-id", chip.getSlot());
        }

        return chip;
    }

    public void flashChip(Player player) {
        ItemStack stack = container.getSlot(0).getItemStack();
        if (isValid(stack)) {
            if (GameSingletons.isHost()) {
                BiosChip chip = getOrCreateBiosChip(stack);

                try {
                    assert chip != null;
                    BiosChip.flashChip(
                            chip,
                            "horizon:lua/bios.lua",
                            "horizon:lua/json.lua"
                    );
                } catch (SizeLimitExceededException e) {
                    throw new RuntimeException(e);
                }

                if (GameSingletons.isClient()) {
                    Chat.MAIN_CLIENT_CHAT.addMessage(null, "Flashed Chip " + chip.getSlot());
                } else {
                    ServerSingletons.getConnection(player).sendChatMessage("Flashed Chip");
                }

            } else {
                GameSingletons.client().sendAsClient(new PacketFlashBIOS(this));
            }
        }
    }
}