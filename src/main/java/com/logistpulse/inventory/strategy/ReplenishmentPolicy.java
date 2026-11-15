package com.logistpulse.inventory.strategy;

public interface ReplenishmentPolicy {

    // Devuelve cuántas unidades reponer (0 si no hace falta)
    int quantityToReplenish(int currentStock);
}
