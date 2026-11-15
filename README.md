# LOGISTPULSE · Smart Inventory (Java)

Sistema de inventario que procesa una reserva de stock con patrones de comportamiento.

## Flujo de una reserva

1. **Command**: la petición se empaqueta en un `ReserveCommand` y el `CommandInvoker` lo ejecuta y lo registra en el ledger.
2. **Chain of Responsibility**: la petición pasa por SKU → stock → lote → capacidad. Si un eslabón falla, rechaza con un motivo.
3. **Strategy**: `Picking` elige el lote con una política (FEFO, FIFO o LIFO). La reposición usa `ReplenishmentPolicy` (punto de reorden, EOQ, Min-Max).
4. **State**: el lote cambia de estado (Recibido → Almacenado → Reservado → Pickeado → Enviado, con Cuarentena como desvío). Una transición inválida lanza excepción.
5. **Observer**: el cambio publica `StockChanged` y lo reciben Reposición, PULSEFLEET y el Ledger.

## Procesos con pasos fijos

6. **Template Method**: `InventoryProcess.process()` fija validar → ejecutar → registrar → notificar. `ReceivingProcess` (recepción) y `CycleCountProcess` (conteo cíclico) redefinen solo los pasos que cambian.

## Paquetes

| Paquete | Contenido |
|---|---|
| `model` | `Lot`, `Inventory` |
| `command` | Comandos Reserve, Release, Adjust, Transfer e invoker |
| `chain` | Validadores encadenados |
| `strategy` | Políticas de picking y de reposición |
| `state` | Estados del lote |
| `observer` | Evento, publicador y observadores |
| `template` | Proceso base, recepción y conteo cíclico |
| `ledger` | Registro de auditoría |

## Cómo correrlo

    mvn test
    mvn compile
    java -cp target/classes com.logistpulse.inventory.Main
