package com.logistpulse.inventory.command;

import com.logistpulse.inventory.model.Inventory;
import com.logistpulse.inventory.model.Lot;
import com.logistpulse.inventory.observer.StockChanged;
import com.logistpulse.inventory.observer.StockPublisher;

public class ReleaseCommand implements Command {

    private final String key;
    private final String lotCode;
    private final Inventory inventory;
    private final StockPublisher publisher;

    public ReleaseCommand(String key, String lotCode, Inventory inventory, StockPublisher publisher) {
        this.key = key;
        this.lotCode = lotCode;
        this.inventory = inventory;
        this.publisher = publisher;
    }

    @Override
    public String getKey() { return key; }

    @Override
    public String getName() { return "RELEASE"; }

    @Override
    public ExecutionResult execute() {
        Lot lot = inventory.findLot(lotCode);
        if (lot == null) {
            return new ExecutionResult(ExecutionStatus.REJECTED, "El lote no existe: " + lotCode);
        }
        lot.release(); // lanza IllegalStateException si el lote no está RESERVADO
        publisher.publish(new StockChanged(lot.getSku(), lotCode, "RELEASE",
                inventory.storedStock(lot.getSku())));
        return new ExecutionResult(ExecutionStatus.SUCCESS, "Lote " + lotCode + " liberado");
    }

    @Override
    public ExecutionResult undo() {
        Lot lot = inventory.findLot(lotCode);
        lot.reserve();
        publisher.publish(new StockChanged(lot.getSku(), lotCode, "RESERVE",
                inventory.storedStock(lot.getSku())));
        return new ExecutionResult(ExecutionStatus.SUCCESS, "Lote " + lotCode + " reservado de nuevo");
    }
}
