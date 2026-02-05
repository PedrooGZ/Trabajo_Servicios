import java.util.concurrent.ThreadLocalRandom;

public class SensorTask implements Runnable { // Clase que representa el sensor que toma mediciones periódicas.
                                            // Implementa runnable para que pueda ejecutarla cada hilo por separado, ya que
                                            // cualquier objeto que implemente Runnable, puede pasarse a un Thread.
    // Variables finales
    private final String sensorName; // Nombre del sensor.
    private final long periodMs; // Cada cuanto mide en milisegundos.
    private final long endTimeMs; // Momento en el que paran.
    private final double min; // Valor mínimo posible.
    private final double max; // Valor máximo posible.
    private final TelemetryStore store; // Donde se guardan las mediciones.
    private final TelemetryLogger logger; // Donde se escriben las mediciones.

    public SensorTask(
            String sensorName,
            long periodMs,
            long endTimeMs,
            double min,
            double max,
            TelemetryStore store,
            TelemetryLogger logger
    ) {
        this.sensorName = sensorName;
        this.periodMs = periodMs;
        this.endTimeMs = endTimeMs;
        this.min = min;
        this.max = max;
        this.store = store;
        this.logger = logger;
    }

    @Override
    public void run() {
        int count = 0; // Contador de mediciones

        while (!Thread.currentThread().isInterrupted()
                && System.currentTimeMillis() < endTimeMs) { // Si nadie ha pedido parar el hilo ni se ha acabado el tiempo.

            double value = ThreadLocalRandom.current().nextDouble(min, max); // Genera números aleatorios entre los max y in establecidos
                                                                // usamos ThreadLocalRandom porque es más rápido en multihilos
            store.add(sensorName, value); // Guarda en memoria (rápido)
            logger.logLine(sensorName, value); // Escribe en el archivo

            count++; // Incrementa el contador

            // Debug que muestra cada 100 mediciones el progreso
            if (count % 100 == 0) {
                System.out.printf("%s -> %d muestras%n", sensorName, count);
            }

            try {
                Thread.sleep(periodMs); // Pausa el hilo 500 ms
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.printf("%s finaliza. Total muestras: %d%n", sensorName, count); // Imprime un resumen al terminar el tiempo total
    }
}
