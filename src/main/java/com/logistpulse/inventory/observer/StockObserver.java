package com.logistpulse.inventory.observer;

public interface StockObserver {

    void onStockChanged(StockChanged event);
}
