package com.logistpulse.inventory.command;

import com.logistpulse.inventory.model.Inventory;
import com.logistpulse.inventory.model.Lot;
import com.logistpulse.inventory.observer.StockChanged;
import com.logistpulse.inventory.observer.StockPublisher;

public class AdjustCommand implements Command {

    private final String key;
    private final String lotCode;
    private final int delta;
    private final Inventory inventory;
    private final StockPublisher publisher;

    public AdjustCommand(String key, String lotCode, int delta, Inventory inventory, StockPublisher publisher) {
        this.key = key;
        this.lotCode = lotCode;
        this.delta = delta;
        this.inventory = inventory;
        this.publisher = publisher;
    }

    @Override
    public String getKey() { return key; }

    @Override
    public String getName() { return "ADJUST"; }

    @Override
    public ExecutionResult execute() {
        Lot lot = inventory.findLot(lotCode);
        if (lot == null) {
            return new ExecutionResult(ExecutionStatus.REJECTED, "El lote no existe: " + lotCode);
        }
        if (lot.getQuantity() + delta < 0) {
            return new ExecutionResult(ExecutionStatus.REJECTED, "El ajuste deja la cantidad en negativo");
        }
        lot.adjust(delta);
        publisher.publish(new StockChanged(lot.getSku(), lotCode, "ADJUST",
                inventory.storedStock(lot.getSku())));
        return new ExecutionResult(ExecutionStatus.SUCCESS, "Lote " + lotCode + " ajustado en " + delta);
    }

    @Override
    public ExecutionResult undo() {
        Lot lot = inventory.findLot(lotCode);
        lot.adjust(-delta);
        publisher.publish(new StockChanged(lot.getSku(), lotCode, "ADJUST",
                inventory.storedStock(lot.getSku())));
        return new ExecutionResult(ExecutionStatus.SUCCESS, "Ajuste del lote " + lotCode + " deshecho");
    }
}
