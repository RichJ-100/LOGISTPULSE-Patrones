package com.logistpulse.inventory.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.logistpulse.inventory.model.Lot;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class StateTest {

    private Lot newLot() {
        return new Lot("L-9", "SKU-1", 10, LocalDate.now(), null);
    }

    @Test
    void lotFollowsTheNormalFlow() {
        Lot lot = newLot();
        assertEquals("RECIBIDO", lot.getStateName());
        lot.store();
        assertEquals("ALMACENADO", lot.getStateName());
        lot.reserve();
        assertEquals("RESERVADO", lot.getStateName());
        lot.pick();
        assertEquals("PICKEADO", lot.getStateName());
        lot.ship();
        assertEquals("ENVIADO", lot.getStateName());
    }

    @Test
    void releaseReturnsLotToStored() {
        Lot lot = newLot();
        lot.store();
        lot.reserve();
        lot.release();
        assertEquals("ALMACENADO", lot.getStateName());
    }

    @Test
    void receivedLotCannotBeReserved() {
        Lot lot = newLot();
        assertThrows(IllegalStateException.class, lot::reserve);
    }

    @Test
    void quarantinedLotCannotBeReserved() {
        Lot lot = newLot();
        lot.store();
        lot.quarantine();
        assertEquals("CUARENTENA", lot.getStateName());
        assertThrows(IllegalStateException.class, lot::reserve);
    }

    @Test
    void shippedLotCannotChangeState() {
        Lot lot = newLot();
        lot.store();
        lot.reserve();
        lot.pick();
        lot.ship();
        assertThrows(IllegalStateException.class, lot::store);
    }
}
