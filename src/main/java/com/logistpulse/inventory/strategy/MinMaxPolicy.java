package com.logistpulse.inventory.strategy;

public class MinMaxPolicy implements ReplenishmentPolicy {

    private final int min;
    private final int max;

    public MinMaxPolicy(int min, int max) {
        this.min = min;
        this.max = max;
    }

    // Si el stock baja al mínimo, se repone hasta el máximo
    @Override
    public int quantityToReplenish(int currentStock) {
        if (currentStock <= min) {
            return max - currentStock;
        }
        return 0;
    }
}
