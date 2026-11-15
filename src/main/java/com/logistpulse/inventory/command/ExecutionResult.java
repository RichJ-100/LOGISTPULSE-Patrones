package com.logistpulse.inventory.command;

public class ExecutionResult {

    private final ExecutionStatus status;
    private final String message;

    public ExecutionResult(ExecutionStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public ExecutionStatus getStatus() { return status; }

    public String getMessage() { return message; }

    @Override
    public String toString() {
        return status + ": " + message;
    }
}
