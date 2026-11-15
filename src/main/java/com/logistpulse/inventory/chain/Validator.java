package com.logistpulse.inventory.chain;

import com.logistpulse.inventory.model.Inventory;

public abstract class Validator {

    private Validator next;

    public Validator setNext(Validator next) {
        this.next = next;
        return next;
    }

    // Revisa la solicitud y, si pasa, la reenvía al siguiente eslabón
    public void validate(ReservationRequest request, Inventory inventory) {
        check(request, inventory);
        if (next != null) {
            next.validate(request, inventory);
        }
    }

    // Cada eslabón lanza ValidationException si rechaza la solicitud
    protected abstract void check(ReservationRequest request, Inventory inventory);

    // Cadena del PPT: SKU -> stock -> lote -> capacidad
    public static Validator defaultChain() {
        Validator sku = new SkuValidator();
        sku.setNext(new StockValidator())
           .setNext(new LotValidator())
           .setNext(new CapacityValidator());
        return sku;
    }
}
