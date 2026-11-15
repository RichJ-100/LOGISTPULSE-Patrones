package com.logistpulse.inventory.state;

public class ReservedState extends LotState {

    @Override
    public String getName() { return "RESERVADO"; }

    @Override
    public LotState release() { return new StoredState(); }

    @Override
    public LotState pick() { return new PickedState(); }
}
