package me.zombii.horizon.common.cc.commands;

import finalforeach.cosmicreach.chat.IChat;
import finalforeach.cosmicreach.chat.commands.Command;
import finalforeach.cosmicreach.entities.player.Player;
import finalforeach.cosmicreach.items.ItemStack;
import me.zombii.horizon.common.cc.blocks.bios.BlockEntityBiosFlasher;

public class CommandCheckBiosChip extends Command {

    @Override
    public void run(IChat chat) {
        Player p = getCallingPlayer();
        ItemStack stack = p.inventory.getSlot(p.selectedHotbarSlot).getItemStack();

        if (BlockEntityBiosFlasher.isValid(stack)) {
            int id = stack.stackMetadata.getInt("bios-chip-id", -1);
            if (id != -1) {
                chat.addMessage(null, "Bios chip uses file \"data_" + id + ".bin\"");
            } else {
                chat.addMessage(null, "Bios chip is uninitialized");
            }
        }
    }

    @Override
    public String getShortDescription() {
        return "Outputs the file the bios-chip uses.";
    }
}
