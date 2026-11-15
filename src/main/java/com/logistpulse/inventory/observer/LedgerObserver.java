package com.logistpulse.inventory.observer;

import com.logistpulse.inventory.ledger.Ledger;
import com.logistpulse.inventory.ledger.LedgerEntry;

public class LedgerObserver implements StockObserver {

    private final Ledger ledger;

    public LedgerObserver(Ledger ledger) {
        this.ledger = ledger;
    }

    @Override
    public void onStockChanged(StockChanged event) {
        ledger.register(new LedgerEntry("STOCK_CHANGED", event.getSku(), "SUCCESS",
                event.getOperation() + " en lote " + event.getLotCode()
                        + ", stock disponible: " + event.getAvailableStock()));
    }
}
