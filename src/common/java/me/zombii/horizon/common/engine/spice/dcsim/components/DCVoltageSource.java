package me.zombii.horizon.common.engine.spice.dcsim.components;

public class DCVoltageSource implements IDCCircuitComponent {

    private final double emf;

    public DCVoltageSource(
            double emf
    ) {
        this.emf = emf;
    }

    public double getEmf() {
        return emf;
    }

}