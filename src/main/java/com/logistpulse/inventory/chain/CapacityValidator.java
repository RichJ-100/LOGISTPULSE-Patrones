package com.logistpulse.inventory.chain;

import com.logistpulse.inventory.model.Inventory;

public class CapacityValidator extends Validator {

    @Override
    protected void check(ReservationRequest request, Inventory inventory) {
        int total = inventory.reservedUnits() + request.getQuantity();
        if (total > inventory.getReservationCapacity()) {
            throw new ValidationException("Capacidad de reservas excedida: máximo "
                    + inventory.getReservationCapacity());
        }
    }
}
