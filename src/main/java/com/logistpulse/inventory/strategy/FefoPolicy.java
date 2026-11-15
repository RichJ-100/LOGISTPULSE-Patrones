package com.logistpulse.inventory.strategy;

import com.logistpulse.inventory.model.Lot;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// First Expired, First Out: sale primero el lote que vence antes
public class FefoPolicy implements PickingPolicy {

    @Override
    public Lot choose(List<Lot> lots) {
        return Collections.min(lots,
                Comparator.comparing(Lot::getExpirationDate, Comparator.nullsLast(Comparator.naturalOrder())));
    }
}
