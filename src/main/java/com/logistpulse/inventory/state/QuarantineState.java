package com.logistpulse.inventory.state;

public class QuarantineState extends LotState {

    @Override
    public String getName() { return "CUARENTENA"; }

    // Al salir de cuarentena el lote vuelve a ALMACENADO
    @Override
    public LotState store() { return new StoredState(); }
}
