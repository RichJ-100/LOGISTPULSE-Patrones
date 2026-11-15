package com.logistpulse.inventory.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.logistpulse.inventory.TestData;
import com.logistpulse.inventory.chain.ReservationRequest;
import com.logistpulse.inventory.chain.Validator;
import com.logistpulse.inventory.ledger.Ledger;
import com.logistpulse.inventory.model.Inventory;
import com.logistpulse.inventory.observer.StockPublisher;
import com.logistpulse.inventory.strategy.FefoPolicy;
import com.logistpulse.inventory.strategy.Picking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CommandTest {

    private Inventory inventory;
    private Ledger ledger;
    private StockPublisher publisher;
    private CommandInvoker invoker;

    @BeforeEach
    void setUp() {
        inventory = TestData.inventory();
        ledger = new Ledger();
        publisher = new StockPublisher();
        invoker = new CommandInvoker(ledger);
    }

    private ReserveCommand reserve(String key, String sku, int quantity) {
        return new ReserveCommand(new ReservationRequest(key, sku, quantity, "HUMANO"),
                inventory, Validator.defaultChain(), new Picking(new FefoPolicy()), publisher);
    }

    @Test
    void reserveChangesLotToReserved() {
        ExecutionResult result = invoker.invoke(reserve("K1", "PARACETAMOL", 5));
        assertEquals(ExecutionStatus.SUCCESS, result.getStatus());
        assertEquals("RESERVADO", inventory.findLot("L-002").getStateName());
    }

    @Test
    void rejectedReserveDoesNotChangeInventory() {
        ExecutionResult result = invoker.invoke(reserve("K1", "NO-EXISTE", 1));
        assertEquals(ExecutionStatus.REJECTED, result.getStatus());
        assertEquals(0, inventory.reservedUnits());
    }

    @Test
    void sameKeyIsExecutedOnlyOnce() {
        invoker.invoke(reserve("K1", "PARACETAMOL", 5));
        invoker.invoke(reserve("K1", "PARACETAMOL", 5));
        assertEquals(1, ledger.getEntries().size());
        assertEquals(20, inventory.reservedUnits());
    }

    @Test
    void undoReleasesTheLot() {
        invoker.invoke(reserve("K1", "PARACETAMOL", 5));
        invoker.undoLast();
        assertEquals("ALMACENADO", inventory.findLot("L-002").getStateName());
        assertEquals(2, ledger.getEntries().size());
    }

    @Test
    void releaseCommandReturnsLotToStored() {
        invoker.invoke(reserve("K1", "PARACETAMOL", 5));
        ExecutionResult result = invoker.invoke(new ReleaseCommand("K2", "L-002", inventory, publisher));
        assertEquals(ExecutionStatus.SUCCESS, result.getStatus());
        assertEquals("ALMACENADO", inventory.findLot("L-002").getStateName());
    }

    @Test
    void releaseOfStoredLotFails() {
        ExecutionResult result = invoker.invoke(new ReleaseCommand("K1", "L-001", inventory, publisher));
        assertEquals(ExecutionStatus.FAILED, result.getStatus());
    }

    @Test
    void adjustChangesQuantityAndUndoRestoresIt() {
        invoker.invoke(new AdjustCommand("K1", "L-001", -5, inventory, publisher));
        assertEquals(15, inventory.findLot("L-001").getQuantity());
        invoker.undoLast();
        assertEquals(20, inventory.findLot("L-001").getQuantity());
    }

    @Test
    void adjustBelowZeroIsRejected() {
        ExecutionResult result = invoker.invoke(new AdjustCommand("K1", "L-001", -50, inventory, publisher));
        assertEquals(ExecutionStatus.REJECTED, result.getStatus());
    }

    @Test
    void transferMovesQuantityBetweenLots() {
        invoker.invoke(new TransferCommand("K1", "L-001", "L-002", 5, inventory, publisher));
        assertEquals(15, inventory.findLot("L-001").getQuantity());
        assertEquals(25, inventory.findLot("L-002").getQuantity());
    }
}
