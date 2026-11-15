package com.logistpulse.inventory.state;

public class PickedState extends LotState {

    @Override
    public String getName() { return "PICKEADO"; }

    @Override
    public LotState ship() { return new ShippedState(); }
}
