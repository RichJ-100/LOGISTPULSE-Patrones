package com.logistpulse.inventory.command;

public interface Command {

    String getKey();

    String getName();

    ExecutionResult execute();

    // Compensación: deshace lo que hizo execute()
    ExecutionResult undo();
}
