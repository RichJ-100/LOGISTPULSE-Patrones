package com.logistpulse.inventory.strategy;

import com.logistpulse.inventory.model.Lot;
import java.util.List;

public interface PickingPolicy {

    Lot choose(List<Lot> lots);
}
