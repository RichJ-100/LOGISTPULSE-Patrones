package com.logistpulse.inventory.chain;

public class ReservationRequest {

    private final String key;         // clave de idempotencia
    private final String sku;
    private final int quantity;
    private final String requestedBy; // HUMANO o AGENTE_IA

    public ReservationRequest(String key, String sku, int quantity, String requestedBy) {
        this.key = key;
        this.sku = sku;
        this.quantity = quantity;
        this.requestedBy = requestedBy;
    }

    public String getKey() { return key; }

    public String getSku() { return sku; }

    public int getQuantity() { return quantity; }

    public String getRequestedBy() { return requestedBy; }
}
