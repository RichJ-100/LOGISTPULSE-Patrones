package com.logistpulse.inventory.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.logistpulse.inventory.TestData;
import com.logistpulse.inventory.model.Inventory;
import com.logistpulse.inventory.model.Lot;
import java.util.List;
import org.junit.jupiter.api.Test;

class StrategyTest {

    private final Inventory inventory = TestData.inventory();

    private List<Lot> candidates() {
        return inventory.availableLots("PARACETAMOL", 5);
    }

    @Test
    void fefoChoosesLotThatExpiresFirst() {
        assertEquals("L-002", new Picking(new FefoPolicy()).choose(candidates()).getCode());
    }

    @Test
    void fifoChoosesOldestLot() {
        assertEquals("L-001", new Picking(new FifoPolicy()).choose(candidates()).getCode());
    }

    @Test
    void lifoChoosesNewestLot() {
        assertEquals("L-002", new Picking(new LifoPolicy()).choose(candidates()).getCode());
    }

    @Test
    void policyCanChangeAtRuntime() {
        Picking picking = new Picking(new FefoPolicy());
        assertEquals("L-002", picking.choose(candidates()).getCode());
        picking.setPolicy(new FifoPolicy());
        assertEquals("L-001", picking.choose(candidates()).getCode());
    }

    @Test
    void reorderPointPolicy() {
        ReplenishmentPolicy policy = new ReorderPointPolicy(10, 50);
        assertEquals(50, policy.quantityToReplenish(8));
        assertEquals(0, policy.quantityToReplenish(20));
    }

    @Test
    void minMaxPolicy() {
        ReplenishmentPolicy policy = new MinMaxPolicy(10, 100);
        assertEquals(96, policy.quantityToReplenish(4));
        assertEquals(0, policy.quantityToReplenish(50));
    }

    @Test
    void eoqPolicy() {
        // EOQ = raíz(2 * 1000 * 50 / 10) = 100
        ReplenishmentPolicy policy = new EoqPolicy(1000, 50, 10, 20);
        assertEquals(100, policy.quantityToReplenish(15));
        assertEquals(0, policy.quantityToReplenish(30));
    }
}
