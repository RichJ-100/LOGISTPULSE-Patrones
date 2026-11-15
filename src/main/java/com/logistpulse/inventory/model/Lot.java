package com.logistpulse.inventory.model;

import com.logistpulse.inventory.state.LotState;
import com.logistpulse.inventory.state.ReceivedState;
import java.time.LocalDate;

public class Lot {

    private final String code;
    private final String sku;
    private int quantity;
    private final LocalDate receivedDate;
    private final LocalDate expirationDate; // puede ser null si no vence
    private LotState state;

    public Lot(String code, String sku, int quantity, LocalDate receivedDate, LocalDate expirationDate) {
        this.code = code;
        this.sku = sku;
        this.quantity = quantity;
        this.receivedDate = receivedDate;
        this.expirationDate = expirationDate;
        this.state = new ReceivedState();
    }

    public String getCode() { return code; }

    public String getSku() { return sku; }

    public int getQuantity() { return quantity; }

    public LocalDate getReceivedDate() { return receivedDate; }

    public LocalDate getExpirationDate() { return expirationDate; }

    public String getStateName() { return state.getName(); }

    public boolean isStored() { return state.isAvailable(); }

    public boolean isExpired() {
        return expirationDate != null && expirationDate.isBefore(LocalDate.now());
    }

    public void adjust(int delta) {
        quantity = quantity + delta;
    }

    // Transiciones: el lote delega en su estado y se cambia por el nuevo
    public void store() { state = state.store(); }

    public void reserve() { state = state.reserve(); }

    public void release() { state = state.release(); }

    public void pick() { state = state.pick(); }

    public void ship() { state = state.ship(); }

    public void quarantine() { state = state.quarantine(); }
}
