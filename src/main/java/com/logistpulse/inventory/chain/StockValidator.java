package com.logistpulse.inventory.chain;

import com.logistpulse.inventory.model.Inventory;

public class StockValidator extends Validator {

    @Override
    protected void check(ReservationRequest request, Inventory inventory) {
        if (request.getQuantity() <= 0) {
            throw new ValidationException("La cantidad debe ser mayor a 0");
        }
        int stock = inventory.storedStock(request.getSku());
        if (stock < request.getQuantity()) {
            throw new ValidationException("Stock insuficiente: hay " + stock
                    + " y se piden " + request.getQuantity());
        }
    }
}
