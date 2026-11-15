package com.logistpulse.inventory.state;

public class StoredState extends LotState {

    @Override
    public String getName() { return "ALMACENADO"; }

    @Override
    public boolean isAvailable() { return true; }

    @Override
    public LotState reserve() { return new ReservedState(); }

    @Override
    public LotState quarantine() { return new QuarantineState(); }
}
