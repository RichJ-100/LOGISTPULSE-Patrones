package com.logistpulse.inventory.chain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.logistpulse.inventory.TestData;
import com.logistpulse.inventory.model.Inventory;
import org.junit.jupiter.api.Test;

class ChainTest {

    @Test
    void validRequestPasses() {
        Inventory inventory = TestData.inventory();
        assertDoesNotThrow(() -> Validator.defaultChain()
                .validate(new ReservationRequest("K", "PARACETAMOL", 5, "HUMANO"), inventory));
    }

    @Test
    void unknownSkuIsRejected() {
        Inventory inventory = TestData.inventory();
        ValidationException e = assertThrows(ValidationException.class, () -> Validator.defaultChain()
                .validate(new ReservationRequest("K", "NO-EXISTE", 1, "AGENTE_IA"), inventory));
        assertTrue(e.getMessage().contains("SKU"));
    }

    @Test
    void insufficientStockIsRejected() {
        Inventory inventory = TestData.inventory();
        ValidationException e = assertThrows(ValidationException.class, () -> Validator.defaultChain()
                .validate(new ReservationRequest("K", "PARACETAMOL", 1000, "HUMANO"), inventory));
        assertTrue(e.getMessage().contains("Stock"));
    }

    @Test
    void expiredLotIsRejected() {
        Inventory inventory = new Inventory(50);
        inventory.addLot(TestData.storedLot("L-X", "VENCIDO", 10, 30, -1));
        ValidationException e = assertThrows(ValidationException.class, () -> Validator.defaultChain()
                .validate(new ReservationRequest("K", "VENCIDO", 5, "HUMANO"), inventory));
        assertTrue(e.getMessage().contains("lotes"));
    }

    @Test
    void quarantinedLotIsRejected() {
        Inventory inventory = new Inventory(50);
        inventory.addLot(TestData.storedLot("L-Q", "CUARENTENA", 10, 5, 30));
        inventory.findLot("L-Q").quarantine();
        assertThrows(ValidationException.class, () -> Validator.defaultChain()
                .validate(new ReservationRequest("K", "CUARENTENA", 5, "HUMANO"), inventory));
    }

    @Test
    void capacityExceededIsRejected() {
        Inventory small = new Inventory(10);
        small.addLot(TestData.storedLot("L-001", "PARACETAMOL", 20, 10, 30));
        ValidationException e = assertThrows(ValidationException.class, () -> Validator.defaultChain()
                .validate(new ReservationRequest("K", "PARACETAMOL", 15, "HUMANO"), small));
        assertTrue(e.getMessage().contains("Capacidad"));
    }
}
