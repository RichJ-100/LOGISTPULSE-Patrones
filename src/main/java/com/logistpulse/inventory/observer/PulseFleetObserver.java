package com.logistpulse.inventory.observer;

import java.util.ArrayList;
import java.util.List;

// Avisa a PULSEFLEET (aquí solo guarda el mensaje)
public class PulseFleetObserver implements StockObserver {

    private final List<String> messages = new ArrayList<>();

    @Override
    public void onStockChanged(StockChanged event) {
        messages.add("PULSEFLEET: " + event.getOperation() + " en lote "
                + event.getLotCode() + " (" + event.getSku() + ")");
    }

    public List<String> getMessages() { return messages; }
}
