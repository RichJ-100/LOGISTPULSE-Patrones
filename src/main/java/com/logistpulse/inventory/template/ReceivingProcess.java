package com.logistpulse.inventory.template;

import com.logistpulse.inventory.chain.ValidationException;
import com.logistpulse.inventory.ledger.Ledger;
import com.logistpulse.inventory.model.Inventory;
import com.logistpulse.inventory.model.Lot;
import com.logistpulse.inventory.observer.StockChanged;
import com.logistpulse.inventory.observer.StockPublisher;
import java.time.LocalDate;

// Recepción: llega un lote nuevo, queda RECIBIDO y pasa a ALMACENADO
public class ReceivingProcess extends InventoryProcess {

    private final String key;
    private final String lotCode;
    private final String sku;
    private final int quantity;
    private final LocalDate expirationDate; // puede ser null si no vence

    public ReceivingProcess(String key, String lotCode, String sku, int quantity,
                            LocalDate expirationDate, Inventory inventory,
                            Ledger ledger, StockPublisher publisher) {
        super(inventory, ledger, publisher);
        this.key = key;
        this.lotCode = lotCode;
        this.sku = sku;
        this.quantity = quantity;
        this.expirationDate = expirationDate;
    }

    @Override
    protected String getName() { return "RECEIVING"; }

    @Override
    protected String getKey() { return key; }

    @Override
    protected void validate() {
        if (sku == null || sku.isBlank()) {
            throw new ValidationException("El SKU es obligatorio");
        }
        if (quantity <= 0) {
            throw new ValidationException("La cantidad debe ser mayor a 0");
        }
        if (inventory.findLot(lotCode) != null) {
            throw new ValidationException("El lote ya existe: " + lotCode);
        }
    }

    @Override
    protected String execute() {
        Lot lot = new Lot(lotCode, sku, quantity, LocalDate.now(), expirationDate);
        lot.store();
        inventory.addLot(lot);
        return "Lote " + lotCode + " recibido y almacenado (" + quantity + " unidades de " + sku + ")";
    }

    @Override
    protected StockChanged buildEvent() {
        return new StockChanged(sku, lotCode, "RECEIVE", inventory.storedStock(sku));
    }
}
