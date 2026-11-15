package com.logistpulse.inventory.observer;

import com.logistpulse.inventory.strategy.ReplenishmentPolicy;
import java.util.ArrayList;
import java.util.List;

public class ReplenishmentObserver implements StockObserver {

    private final ReplenishmentPolicy policy;
    private final List<String> messages = new ArrayList<>();

    public ReplenishmentObserver(ReplenishmentPolicy policy) {
        this.policy = policy;
    }

    @Override
    public void onStockChanged(StockChanged event) {
        int quantity = policy.quantityToReplenish(event.getAvailableStock());
        if (quantity > 0) {
            messages.add("Reponer " + quantity + " unidades de " + event.getSku());
        }
    }

    public List<String> getMessages() { return messages; }
}
