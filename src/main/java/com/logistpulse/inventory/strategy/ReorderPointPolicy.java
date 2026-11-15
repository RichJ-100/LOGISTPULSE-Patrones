package com.logistpulse.inventory.strategy;

public class ReorderPointPolicy implements ReplenishmentPolicy {

    private final int reorderPoint;
    private final int orderQuantity;

    public ReorderPointPolicy(int reorderPoint, int orderQuantity) {
        this.reorderPoint = reorderPoint;
        this.orderQuantity = orderQuantity;
    }

    @Override
    public int quantityToReplenish(int currentStock) {
        if (currentStock <= reorderPoint) {
            return orderQuantity;
        }
        return 0;
    }
}
