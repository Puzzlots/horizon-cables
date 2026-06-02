package me.zombii.horizon.common.engine.spice.dcsim.components;

public class DCResistor implements IDCCircuitComponent {

    private final double resistance;

    public DCResistor(
            double resistance
    ) {
        this.resistance = resistance;
    }

    public double getResistance() {
        return resistance;
    }

}
