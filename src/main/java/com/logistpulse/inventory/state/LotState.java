package com.logistpulse.inventory.state;

public abstract class LotState {

    public abstract String getName();

    // Solo el estado ALMACENADO está disponible para reservar
    public boolean isAvailable() { return false; }

    // Cada método devuelve el nuevo estado. Por defecto la transición es inválida.
    public LotState store() { return invalid("store"); }

    public LotState reserve() { return invalid("reserve"); }

    public LotState release() { return invalid("release"); }

    public LotState pick() { return invalid("pick"); }

    public LotState ship() { return invalid("ship"); }

    public LotState quarantine() { return invalid("quarantine"); }

    private LotState invalid(String action) {
        throw new IllegalStateException("Transición inválida: " + getName() + " -> " + action);
    }
}
