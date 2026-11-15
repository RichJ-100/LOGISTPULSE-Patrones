package com.logistpulse.inventory.chain;

import com.logistpulse.inventory.model.Inventory;

public class SkuValidator extends Validator {

    @Override
    protected void check(ReservationRequest request, Inventory inventory) {
        String sku = request.getSku();
        if (sku == null || sku.isBlank() || !inventory.existsSku(sku)) {
            throw new ValidationException("El SKU no existe: " + sku);
        }
    }
}
