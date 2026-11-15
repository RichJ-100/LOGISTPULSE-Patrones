package com.logistpulse.inventory.strategy;

public class EoqPolicy implements ReplenishmentPolicy {

    private final double annualDemand;
    private final double orderCost;
    private final double holdingCost;
    private final int reorderPoint;

    public EoqPolicy(double annualDemand, double orderCost, double holdingCost, int reorderPoint) {
        this.annualDemand = annualDemand;
        this.orderCost = orderCost;
        this.holdingCost = holdingCost;
        this.reorderPoint = reorderPoint;
    }

    // Lote económico de pedido: EOQ = raíz(2 * D * S / H)
    @Override
    public int quantityToReplenish(int currentStock) {
        if (currentStock <= reorderPoint) {
            return (int) Math.round(Math.sqrt(2 * annualDemand * orderCost / holdingCost));
        }
        return 0;
    }
}
