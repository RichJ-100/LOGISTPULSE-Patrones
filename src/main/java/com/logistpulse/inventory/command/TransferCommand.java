package com.logistpulse.inventory.command;

import com.logistpulse.inventory.model.Inventory;
import com.logistpulse.inventory.model.Lot;
import com.logistpulse.inventory.observer.StockChanged;
import com.logistpulse.inventory.observer.StockPublisher;

public class TransferCommand implements Command {

    private final String key;
    private final String fromCode;
    private final String toCode;
    private final int quantity;
    private final Inventory inventory;
    private final StockPublisher publisher;

    public TransferCommand(String key, String fromCode, String toCode, int quantity,
                           Inventory inventory, StockPublisher publisher) {
        this.key = key;
        this.fromCode = fromCode;
        this.toCode = toCode;
        this.quantity = quantity;
        this.inventory = inventory;
        this.publisher = publisher;
    }

    @Override
    public String getKey() { return key; }

    @Override
    public String getName() { return "TRANSFER"; }

    @Override
    public ExecutionResult execute() {
        Lot from = inventory.findLot(fromCode);
        Lot to = inventory.findLot(toCode);
        if (from == null || to == null) {
            return new ExecutionResult(ExecutionStatus.REJECTED, "El lote de origen o destino no existe");
        }
        if (!from.getSku().equals(to.getSku())) {
            return new ExecutionResult(ExecutionStatus.REJECTED, "Los lotes deben ser del mismo SKU");
        }
        if (!from.isStored() || !to.isStored()) {
            return new ExecutionResult(ExecutionStatus.REJECTED, "Ambos lotes deben estar ALMACENADOS");
        }
        if (quantity <= 0 || from.getQuantity() < quantity) {
            return new ExecutionResult(ExecutionStatus.REJECTED, "Cantidad inválida o insuficiente en el origen");
        }
        from.adjust(-quantity);
        to.adjust(quantity);
        publisher.publish(new StockChanged(from.getSku(), fromCode, "TRANSFER",
                inventory.storedStock(from.getSku())));
        return new ExecutionResult(ExecutionStatus.SUCCESS, quantity + " unidades de "
                + fromCode + " a " + toCode);
    }

    @Override
    public ExecutionResult undo() {
        Lot from = inventory.findLot(fromCode);
        Lot to = inventory.findLot(toCode);
        from.adjust(quantity);
        to.adjust(-quantity);
        publisher.publish(new StockChanged(from.getSku(), fromCode, "TRANSFER",
                inventory.storedStock(from.getSku())));
        return new ExecutionResult(ExecutionStatus.SUCCESS, "Transferencia deshecha");
    }
}
