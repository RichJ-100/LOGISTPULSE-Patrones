package com.logistpulse.inventory.chain;

import com.logistpulse.inventory.model.Inventory;

public class LotValidator extends Validator {

    @Override
    protected void check(ReservationRequest request, Inventory inventory) {
        if (inventory.availableLots(request.getSku(), request.getQuantity()).isEmpty()) {
            throw new ValidationException("No hay lotes disponibles para " + request.getSku()
                    + " (vencidos, en cuarentena o sin cantidad suficiente)");
        }
    }
}
