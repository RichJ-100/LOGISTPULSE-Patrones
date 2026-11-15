package com.logistpulse.inventory.template;

import com.logistpulse.inventory.chain.ValidationException;
import com.logistpulse.inventory.command.ExecutionResult;
import com.logistpulse.inventory.command.ExecutionStatus;
import com.logistpulse.inventory.ledger.Ledger;
import com.logistpulse.inventory.ledger.LedgerEntry;
import com.logistpulse.inventory.model.Inventory;
import com.logistpulse.inventory.observer.StockChanged;
import com.logistpulse.inventory.observer.StockPublisher;

public abstract class InventoryProcess {

    protected final Inventory inventory;
    protected final Ledger ledger;
    protected final StockPublisher publisher;

    public InventoryProcess(Inventory inventory, Ledger ledger, StockPublisher publisher) {
        this.inventory = inventory;
        this.ledger = ledger;
        this.publisher = publisher;
    }

    // Método plantilla: fija el orden de los pasos y las subclases no lo sobrescriben
    public final ExecutionResult process() {
        // 1. validar
        try {
            validate();
        } catch (ValidationException e) {
            ledger.register(new LedgerEntry(getName(), getKey(), "REJECTED", e.getMessage()));
            return new ExecutionResult(ExecutionStatus.REJECTED, e.getMessage());
        }

        // 2. ejecutar
        String message = execute();

        // 3. registrar
        record(message);

        // 4. notificar
        notifyChange();

        return new ExecutionResult(ExecutionStatus.SUCCESS, message);
    }

    // Pasos que cada subclase debe definir
    protected abstract String getName();

    protected abstract String getKey();

    protected abstract void validate();

    protected abstract String execute();

    protected abstract StockChanged buildEvent();

    // Pasos con valor por defecto
    protected void record(String message) {
        ledger.register(new LedgerEntry(getName(), getKey(), "SUCCESS", message));
    }

    protected void notifyChange() {
        publisher.publish(buildEvent());
    }
}
