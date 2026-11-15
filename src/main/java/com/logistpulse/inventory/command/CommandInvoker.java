package com.logistpulse.inventory.command;

import com.logistpulse.inventory.ledger.Ledger;
import com.logistpulse.inventory.ledger.LedgerEntry;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class CommandInvoker {

    private final Ledger ledger;
    private final Map<String, ExecutionResult> processed = new HashMap<>();
    private final Deque<Command> history = new ArrayDeque<>();

    public CommandInvoker(Ledger ledger) {
        this.ledger = ledger;
    }

    public ExecutionResult invoke(Command command) {
        // Idempotencia: si la clave ya se ejecutó con éxito, no se repite
        if (processed.containsKey(command.getKey())) {
            return processed.get(command.getKey());
        }

        ExecutionResult result;
        try {
            result = command.execute();
        } catch (RuntimeException e) {
            result = new ExecutionResult(ExecutionStatus.FAILED, e.getMessage());
        }

        ledger.register(new LedgerEntry(command.getName(), command.getKey(),
                result.getStatus().name(), result.getMessage()));

        if (result.getStatus() == ExecutionStatus.SUCCESS) {
            processed.put(command.getKey(), result);
            history.push(command);
        }
        return result;
    }

    public ExecutionResult undoLast() {
        if (history.isEmpty()) {
            return new ExecutionResult(ExecutionStatus.REJECTED, "No hay comandos para deshacer");
        }
        Command command = history.pop();
        ExecutionResult result = command.undo();
        processed.remove(command.getKey());
        ledger.register(new LedgerEntry(command.getName(), command.getKey(),
                ExecutionStatus.UNDONE.name(), result.getMessage()));
        return result;
    }
}
