package me.zombii.horizon.common.cc.blocks;

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
import finalforeach.cosmicreach.world.Zone;
import io.github.puzzle.cosmic.item.AbstractCosmicItem;
import me.zombii.horizon.common.HorizonTags;
import me.zombii.horizon.common.cc.computer.storage.AbstractDataStorageDevice;
import me.zombii.horizon.common.cc.computer.storage.nonportable.BiosChip;
import me.zombii.horizon.common.cc.packets.PacketFlashBIOS;
import me.zombii.horizon.common.screen.ScreenManager;
import me.zombii.horizon.common.screen.ScreenOpenInfo;

import javax.naming.SizeLimitExceededException;

public class BiosFlasherBlockEntity extends BlockEntity {

    public static void register() {
        BlockEntityCreator.registerBlockEntityCreator(
                BiosFlasherBlock.BE_ID.toString(),
                (block, zone, x, y, z) ->
                        new BiosFlasherBlockEntity(zone, x, y, z)
        );
    }

    private final BiosFlasherContainer container;

    public static boolean isValid(ItemStack stack) {
        if (stack == null) return false;
        return stack.hasTag(HorizonTags.TAG_BIOS_CHIP) &&
                stack.getItem().hasIntProperty("bios-chip-size");
    }

    public BiosFlasherContainer getContainer() {
        return container;
    }

    public BiosFlasherBlockEntity() {
        this(null, 0, 0, 0);
    }

    public BiosFlasherBlockEntity(Zone zone, int globalX, int globalY, int globalZ) {
        super(zone, globalX, globalY, globalZ);
        this.container = new BiosFlasherContainer(1);
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
                        BiosFlasherBlock.SCREEN_ID,
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
        return BiosFlasherBlock.BE_ID.toString();
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

    public void flashChip(Player player) {
        ItemStack item = container.getSlot(0).getItemStack();
        if (isValid(item)) {
            if (GameSingletons.isHost()) {
                BiosChip chip = (BiosChip) AbstractDataStorageDevice.COMPONENTS_BY_ID.get(item.stackMetadata.getInt("bios-chip-id", -1));
                if (chip == null) {
                    chip = new BiosChip(
                            item.getItem().getIntProperty("bios-chip-size", -1)
                    );
                }

                try {
                    BiosChip.flashChip(chip, "print(\"Hello World!\")");
                } catch (SizeLimitExceededException e) {
                    throw new RuntimeException(e);
                }

                if (GameSingletons.isClient()) {
                    Chat.MAIN_CLIENT_CHAT.addMessage(null, "Flashed Chip " + chip.getSlot());
                } else {
                    ServerSingletons.getConnection(player).sendChatMessage("Flashed Chip");
                }

                item.setMetadataInt("bios-chip-id", chip.getSlot());
            } else {
                GameSingletons.client().sendAsClient(new PacketFlashBIOS(this));
            }
        }
    }
}