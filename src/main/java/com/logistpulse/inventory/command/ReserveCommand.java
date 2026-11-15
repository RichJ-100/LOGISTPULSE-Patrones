package com.logistpulse.inventory.command;

import com.logistpulse.inventory.chain.ReservationRequest;
import com.logistpulse.inventory.chain.ValidationException;
import com.logistpulse.inventory.chain.Validator;
import com.logistpulse.inventory.model.Inventory;
import com.logistpulse.inventory.model.Lot;
import com.logistpulse.inventory.observer.StockChanged;
import com.logistpulse.inventory.observer.StockPublisher;
import com.logistpulse.inventory.strategy.Picking;
import java.util.List;

public class ReserveCommand implements Command {

    private final ReservationRequest request;
    private final Inventory inventory;
    private final Validator chain;
    private final Picking picking;
    private final StockPublisher publisher;
    private Lot reservedLot;

    public ReserveCommand(ReservationRequest request, Inventory inventory, Validator chain,
                          Picking picking, StockPublisher publisher) {
        this.request = request;
        this.inventory = inventory;
        this.chain = chain;
        this.picking = picking;
        this.publisher = publisher;
    }

    @Override
    public String getKey() { return request.getKey(); }

    @Override
    public String getName() { return "RESERVE"; }

    @Override
    public ExecutionResult execute() {
        // 1. Chain: validar
        try {
            chain.validate(request, inventory);
        } catch (ValidationException e) {
            return new ExecutionResult(ExecutionStatus.REJECTED, e.getMessage());
        }

        // 2. Strategy: elegir el lote
        List<Lot> candidates = inventory.availableLots(request.getSku(), request.getQuantity());
        reservedLot = picking.choose(candidates);

        // 3. State: cambiar el estado del lote
        reservedLot.reserve();

        // 4. Observer: avisar el cambio
        publisher.publish(new StockChanged(request.getSku(), reservedLot.getCode(), "RESERVE",
                inventory.storedStock(request.getSku())));

        return new ExecutionResult(ExecutionStatus.SUCCESS, "Lote " + reservedLot.getCode()
                + " reservado para " + request.getRequestedBy());
    }

    @Override
    public ExecutionResult undo() {
        reservedLot.release();
        publisher.publish(new StockChanged(request.getSku(), reservedLot.getCode(), "RELEASE",
                inventory.storedStock(request.getSku())));
        return new ExecutionResult(ExecutionStatus.SUCCESS, "Reserva del lote "
                + reservedLot.getCode() + " deshecha");
    }
}
