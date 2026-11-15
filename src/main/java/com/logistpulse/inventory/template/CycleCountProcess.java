package com.logistpulse.inventory.template;

import com.logistpulse.inventory.chain.ValidationException;
import com.logistpulse.inventory.ledger.Ledger;
import com.logistpulse.inventory.model.Inventory;
import com.logistpulse.inventory.model.Lot;
import com.logistpulse.inventory.observer.StockChanged;
import com.logistpulse.inventory.observer.StockPublisher;

// Conteo cíclico: se cuenta un lote y se corrige la cantidad del sistema
public class CycleCountProcess extends InventoryProcess {

    private final String key;
    private final String lotCode;
    private final int countedQuantity;

    public CycleCountProcess(String key, String lotCode, int countedQuantity,
                             Inventory inventory, Ledger ledger, StockPublisher publisher) {
        super(inventory, ledger, publisher);
        this.key = key;
        this.lotCode = lotCode;
        this.countedQuantity = countedQuantity;
    }

    @Override
    protected String getName() { return "CYCLE_COUNT"; }

    @Override
    protected String getKey() { return key; }

    @Override
    protected void validate() {
        if (inventory.findLot(lotCode) == null) {
            throw new ValidationException("El lote no existe: " + lotCode);
        }
        if (countedQuantity < 0) {
            throw new ValidationException("La cantidad contada no puede ser negativa");
        }
    }

    @Override
    protected String execute() {
        Lot lot = inventory.findLot(lotCode);
        int systemQuantity = lot.getQuantity();
        int difference = countedQuantity - systemQuantity;
        lot.adjust(difference);
        return "Conteo del lote " + lotCode + ": sistema " + systemQuantity
                + ", contado " + countedQuantity + ", diferencia " + difference;
    }

    @Override
    protected StockChanged buildEvent() {
        Lot lot = inventory.findLot(lotCode);
        return new StockChanged(lot.getSku(), lotCode, "CYCLE_COUNT", inventory.storedStock(lot.getSku()));
    }
}
