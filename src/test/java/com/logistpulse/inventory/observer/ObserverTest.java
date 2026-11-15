package com.logistpulse.inventory.observer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.logistpulse.inventory.ledger.Ledger;
import com.logistpulse.inventory.strategy.MinMaxPolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ObserverTest {

    private Ledger ledger;
    private StockPublisher publisher;
    private ReplenishmentObserver replenishment;
    private PulseFleetObserver pulseFleet;

    @BeforeEach
    void setUp() {
        ledger = new Ledger();
        publisher = new StockPublisher();
        replenishment = new ReplenishmentObserver(new MinMaxPolicy(30, 100));
        pulseFleet = new PulseFleetObserver();
        publisher.subscribe(replenishment);
        publisher.subscribe(pulseFleet);
        publisher.subscribe(new LedgerObserver(ledger));
    }

    @Test
    void allObserversReceiveTheEvent() {
        publisher.publish(new StockChanged("PARACETAMOL", "L-002", "RESERVE", 20));
        assertEquals(1, replenishment.getMessages().size());
        assertEquals(1, pulseFleet.getMessages().size());
        assertEquals(1, ledger.getEntries().size());
    }

    @Test
    void replenishmentIgnoresHighStock() {
        publisher.publish(new StockChanged("PARACETAMOL", "L-002", "RESERVE", 500));
        assertEquals(0, replenishment.getMessages().size());
        assertEquals(1, pulseFleet.getMessages().size());
    }

    @Test
    void unsubscribedObserverStopsReceiving() {
        publisher.publish(new StockChanged("PARACETAMOL", "L-002", "RESERVE", 20));
        publisher.unsubscribe(pulseFleet);
        publisher.publish(new StockChanged("PARACETAMOL", "L-002", "RELEASE", 40));
        assertEquals(1, pulseFleet.getMessages().size());
        assertEquals(2, ledger.getEntries().size()); // el ledger sigue suscrito (2 eventos)
    }
}
