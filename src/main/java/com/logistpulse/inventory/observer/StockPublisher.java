package com.logistpulse.inventory.observer;

import java.util.ArrayList;
import java.util.List;

public class StockPublisher {

    private final List<StockObserver> observers = new ArrayList<>();

    public void subscribe(StockObserver observer) {
        observers.add(observer);
    }

    public void unsubscribe(StockObserver observer) {
        observers.remove(observer);
    }

    public void publish(StockChanged event) {
        for (StockObserver observer : observers) {
            observer.onStockChanged(event);
        }
    }
}
