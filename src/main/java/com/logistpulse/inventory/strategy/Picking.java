package com.logistpulse.inventory.strategy;

import com.logistpulse.inventory.model.Lot;
import java.util.List;

// Contexto del Strategy: delega en la política activa
public class Picking {

    private PickingPolicy policy;

    public Picking(PickingPolicy policy) {
        this.policy = policy;
    }

    public void setPolicy(PickingPolicy policy) {
        this.policy = policy;
    }

    public Lot choose(List<Lot> lots) {
        return policy.choose(lots);
    }
}
