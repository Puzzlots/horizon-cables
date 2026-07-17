package me.zombii.horizon.common.cc.computer;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import me.zombii.horizon.common.cc.computer.peripherals.AbstractPeripheral;
import me.zombii.horizon.common.cc.computer.storage.nonportable.BiosChip;

public abstract class AbstractComputer {

    private BiosChip biosChip;
    private final ObjectList<AbstractPeripheral> internalPeripherals;

    public AbstractComputer() {
        this.internalPeripherals = new ObjectArrayList<>();
    }

    public BiosChip getBiosChip() {
        return biosChip;
    }

    public void setBiosChip(BiosChip biosChip) {
        this.biosChip = biosChip;
    }

    public abstract FormFactor getFormFactor();

    public ObjectList<AbstractPeripheral> getInternalPeripherals() {
        return internalPeripherals;
    }

    public enum FormFactor {
        PICO_ITX, // small form factor handheld
        MINI_ITX, // small form factor pc
        ATX       // full form factor pc
    }

}
