package com.logistpulse.inventory.strategy;

import com.logistpulse.inventory.model.Lot;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// Last In, First Out: sale primero el lote más reciente
public class LifoPolicy implements PickingPolicy {

    @Override
    public Lot choose(List<Lot> lots) {
        return Collections.max(lots, Comparator.comparing(Lot::getReceivedDate));
    }
}
