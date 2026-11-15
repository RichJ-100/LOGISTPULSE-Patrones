package com.logistpulse.inventory.model;

import java.util.ArrayList;
import java.util.List;

public class Inventory {

    private final List<Lot> lots = new ArrayList<>();
    private final int reservationCapacity;

    public Inventory(int reservationCapacity) {
        this.reservationCapacity = reservationCapacity;
    }

    public void addLot(Lot lot) {
        lots.add(lot);
    }

    public List<Lot> getLots() { return lots; }

    public int getReservationCapacity() { return reservationCapacity; }

    public Lot findLot(String code) {
        for (Lot lot : lots) {
            if (lot.getCode().equals(code)) {
                return lot;
            }
        }
        return null;
    }

    public boolean existsSku(String sku) {
        for (Lot lot : lots) {
            if (lot.getSku().equals(sku)) {
                return true;
            }
        }
        return false;
    }

    // Unidades del SKU en lotes ALMACENADOS
    public int storedStock(String sku) {
        int total = 0;
        for (Lot lot : lots) {
            if (lot.getSku().equals(sku) && lot.isStored()) {
                total = total + lot.getQuantity();
            }
        }
        return total;
    }

    // Lotes almacenados, no vencidos y con cantidad suficiente
    public List<Lot> availableLots(String sku, int quantity) {
        List<Lot> result = new ArrayList<>();
        for (Lot lot : lots) {
            if (lot.getSku().equals(sku) && lot.isStored()
                    && !lot.isExpired() && lot.getQuantity() >= quantity) {
                result.add(lot);
            }
        }
        return result;
    }

    // Unidades que están en lotes RESERVADOS
    public int reservedUnits() {
        int total = 0;
        for (Lot lot : lots) {
            if (lot.getStateName().equals("RESERVADO")) {
                total = total + lot.getQuantity();
            }
        }
        return total;
    }
}
