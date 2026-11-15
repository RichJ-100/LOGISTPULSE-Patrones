package com.logistpulse.inventory.template;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.logistpulse.inventory.TestData;
import com.logistpulse.inventory.command.ExecutionResult;
import com.logistpulse.inventory.command.ExecutionStatus;
import com.logistpulse.inventory.ledger.Ledger;
import com.logistpulse.inventory.model.Inventory;
import com.logistpulse.inventory.observer.PulseFleetObserver;
import com.logistpulse.inventory.observer.StockPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TemplateTest {

    private Inventory inventory;
    private Ledger ledger;
    private StockPublisher publisher;
    private PulseFleetObserver pulseFleet;

    @BeforeEach
    void setUp() {
        inventory = TestData.inventory();
        ledger = new Ledger();
        publisher = new StockPublisher();
        pulseFleet = new PulseFleetObserver();
        publisher.subscribe(pulseFleet);
    }

    private ReceivingProcess receiving(String key, String lotCode, int quantity) {
        return new ReceivingProcess(key, lotCode, "PARACETAMOL", quantity, null,
                inventory, ledger, publisher);
    }

    @Test
    void receivingCreatesStoredLot() {
        ExecutionResult result = receiving("R1", "L-NEW", 10).process();
        assertEquals(ExecutionStatus.SUCCESS, result.getStatus());
        assertEquals("ALMACENADO", inventory.findLot("L-NEW").getStateName());
        assertEquals(10, inventory.findLot("L-NEW").getQuantity());
    }

    @Test
    void receivingExistingLotIsRejected() {
        ExecutionResult result = receiving("R1", "L-001", 10).process();
        assertEquals(ExecutionStatus.REJECTED, result.getStatus());
        assertEquals(20, inventory.findLot("L-001").getQuantity());
    }

    @Test
    void receivingInvalidQuantityIsRejected() {
        ExecutionResult result = receiving("R1", "L-NEW", 0).process();
        assertEquals(ExecutionStatus.REJECTED, result.getStatus());
        assertNull(inventory.findLot("L-NEW"));
    }

    @Test
    void cycleCountCorrectsQuantity() {
        ExecutionResult result = new CycleCountProcess("C1", "L-001", 18,
                inventory, ledger, publisher).process();
        assertEquals(ExecutionStatus.SUCCESS, result.getStatus());
        assertEquals(18, inventory.findLot("L-001").getQuantity());
    }

    @Test
    void cycleCountOfUnknownLotIsRejected() {
        ExecutionResult result = new CycleCountProcess("C1", "L-XXX", 5,
                inventory, ledger, publisher).process();
        assertEquals(ExecutionStatus.REJECTED, result.getStatus());
    }

    @Test
    void cycleCountNegativeIsRejected() {
        ExecutionResult result = new CycleCountProcess("C1", "L-001", -1,
                inventory, ledger, publisher).process();
        assertEquals(ExecutionStatus.REJECTED, result.getStatus());
        assertEquals(20, inventory.findLot("L-001").getQuantity());
    }

    @Test
    void bothProcessesRecordInLedgerAndNotify() {
        receiving("R1", "L-NEW", 10).process();
        new CycleCountProcess("C1", "L-NEW", 9, inventory, ledger, publisher).process();
        assertEquals(2, ledger.getEntries().size());
        assertEquals(2, pulseFleet.getMessages().size());
    }

    @Test
    void rejectedProcessIsRecordedButDoesNotNotify() {
        receiving("R1", "L-001", 10).process();
        assertEquals(1, ledger.getEntries().size());
        assertEquals(0, pulseFleet.getMessages().size());
    }
}
