package com.logistpulse.inventory;

import com.logistpulse.inventory.model.Inventory;
import com.logistpulse.inventory.model.Lot;
import java.time.LocalDate;

public class TestData {

    // Crea un lote ya ALMACENADO. daysToExpire null = no vence.
    public static Lot storedLot(String code, String sku, int quantity, int daysAgo, Integer daysToExpire) {
        LocalDate expiration = null;
        if (daysToExpire != null) {
            expiration = LocalDate.now().plusDays(daysToExpire);
        }
        Lot lot = new Lot(code, sku, quantity, LocalDate.now().minusDays(daysAgo), expiration);
        lot.store();
        return lot;
    }

    public static Inventory inventory() {
        Inventory inventory = new Inventory(50);
        inventory.addLot(storedLot("L-001", "PARACETAMOL", 20, 10, 30));
        inventory.addLot(storedLot("L-002", "PARACETAMOL", 20, 5, 10));
        inventory.addLot(storedLot("L-003", "ZAPATOS", 30, 20, null));
        return inventory;
    }
}
