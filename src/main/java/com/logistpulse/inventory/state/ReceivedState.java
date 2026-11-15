package com.logistpulse.inventory.state;

public class ReceivedState extends LotState {

    @Override
    public String getName() { return "RECIBIDO"; }

    @Override
    public LotState store() { return new StoredState(); }

    @Override
    public LotState quarantine() { return new QuarantineState(); }
}
