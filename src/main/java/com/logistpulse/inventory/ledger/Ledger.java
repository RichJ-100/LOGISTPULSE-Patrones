package com.logistpulse.inventory.ledger;

import java.util.ArrayList;
import java.util.List;

// Registro de auditoría
public class Ledger {

    private final List<LedgerEntry> entries = new ArrayList<>();

    public void register(LedgerEntry entry) {
        entries.add(entry);
    }

    public List<LedgerEntry> getEntries() { return entries; }
}
