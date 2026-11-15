package com.logistpulse.inventory.strategy;

import com.logistpulse.inventory.model.Lot;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// First In, First Out: sale primero el lote más antiguo
public class FifoPolicy implements PickingPolicy {

    @Override
    public Lot choose(List<Lot> lots) {
        return Collections.min(lots, Comparator.comparing(Lot::getReceivedDate));
    }
}
