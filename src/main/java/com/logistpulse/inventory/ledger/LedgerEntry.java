package com.logistpulse.inventory.ledger;

import java.time.LocalDateTime;

public class LedgerEntry {

    private final String type;
    private final String key;
    private final String status;
    private final String message;
    private final LocalDateTime timestamp;

    public LedgerEntry(String type, String key, String status, String message) {
        this.type = type;
        this.key = key;
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public String getType() { return type; }

    public String getKey() { return key; }

    public String getStatus() { return status; }

    public String getMessage() { return message; }

    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return timestamp + " | " + type + " | " + key + " | " + status + " | " + message;
    }
}
