package com.logistpulse.inventory;

import com.logistpulse.inventory.chain.ReservationRequest;
import com.logistpulse.inventory.chain.Validator;
import com.logistpulse.inventory.command.CommandInvoker;
import com.logistpulse.inventory.command.ReserveCommand;
import com.logistpulse.inventory.ledger.Ledger;
import com.logistpulse.inventory.ledger.LedgerEntry;
import com.logistpulse.inventory.model.Inventory;
import com.logistpulse.inventory.model.Lot;
import com.logistpulse.inventory.observer.LedgerObserver;
import com.logistpulse.inventory.observer.PulseFleetObserver;
import com.logistpulse.inventory.observer.ReplenishmentObserver;
import com.logistpulse.inventory.observer.StockPublisher;
import com.logistpulse.inventory.strategy.FefoPolicy;
import com.logistpulse.inventory.strategy.MinMaxPolicy;
import com.logistpulse.inventory.strategy.Picking;
import com.logistpulse.inventory.template.CycleCountProcess;
import com.logistpulse.inventory.template.ReceivingProcess;
import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {
        // Inventario con dos lotes de paracetamol
        Inventory inventory = new Inventory(100);
        Lot lot1 = new Lot("L-001", "PARACETAMOL", 20, LocalDate.now().minusDays(10), LocalDate.now().plusDays(30));
        Lot lot2 = new Lot("L-002", "PARACETAMOL", 20, LocalDate.now().minusDays(5), LocalDate.now().plusDays(10));
        lot1.store();
        lot2.store();
        inventory.addLot(lot1);
        inventory.addLot(lot2);

        // Observer
        Ledger ledger = new Ledger();
        ReplenishmentObserver replenishment = new ReplenishmentObserver(new MinMaxPolicy(30, 100));
        PulseFleetObserver pulseFleet = new PulseFleetObserver();
        StockPublisher publisher = new StockPublisher();
        publisher.subscribe(replenishment);
        publisher.subscribe(pulseFleet);
        publisher.subscribe(new LedgerObserver(ledger));

        // Command + Chain + Strategy + State
        CommandInvoker invoker = new CommandInvoker(ledger);
        Validator chain = Validator.defaultChain();
        Picking picking = new Picking(new FefoPolicy());

        ReserveCommand reserve = new ReserveCommand(
                new ReservationRequest("REQ-1", "PARACETAMOL", 5, "AGENTE_IA"),
                inventory, chain, picking, publisher);
        System.out.println(invoker.invoke(reserve));
        System.out.println(invoker.invoke(reserve)); // misma clave: no se repite

        ReserveCommand invalid = new ReserveCommand(
                new ReservationRequest("REQ-2", "NO-EXISTE", 1, "HUMANO"),
                inventory, chain, picking, publisher);
        System.out.println(invoker.invoke(invalid));

        System.out.println("L-002: " + lot2.getStateName());
        System.out.println(invoker.undoLast());
        System.out.println("L-002: " + lot2.getStateName());

        // Template Method: recepción y conteo cíclico
        System.out.println("--- Template Method ---");
        ReceivingProcess receiving = new ReceivingProcess("REC-1", "L-003", "PARACETAMOL", 50,
                LocalDate.now().plusDays(90), inventory, ledger, publisher);
        System.out.println(receiving.process());
        CycleCountProcess count = new CycleCountProcess("CNT-1", "L-003", 48, inventory, ledger, publisher);
        System.out.println(count.process());

        System.out.println("--- Ledger ---");
        for (LedgerEntry entry : ledger.getEntries()) {
            System.out.println(entry);
        }
        System.out.println("--- Reposición ---");
        System.out.println(replenishment.getMessages());
        System.out.println("--- PULSEFLEET ---");
        System.out.println(pulseFleet.getMessages());
    }
}
