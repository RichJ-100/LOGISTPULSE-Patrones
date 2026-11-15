package com.logistpulse.inventory.observer;

public class StockChanged {

    private final String sku;
    private final String lotCode;
    private final String operation;
    private final int availableStock;

    public StockChanged(String sku, String lotCode, String operation, int availableStock) {
        this.sku = sku;
        this.lotCode = lotCode;
        this.operation = operation;
        this.availableStock = availableStock;
    }

    public String getSku() { return sku; }

    public String getLotCode() { return lotCode; }

    public String getOperation() { return operation; }

    public int getAvailableStock() { return availableStock; }
}
