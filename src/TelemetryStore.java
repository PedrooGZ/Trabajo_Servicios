import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TelemetryStore { // Clase que almacena de forma temporal y segura los valores de los sensores para múltiples hilos
                            // al ser una forma de guardado rápida, permite obtener los datos sin perderse y después, en
                            // TelemetryLogger se escriben en el archivo poco a poco de forma ordenada.

                            // Es una clase para acceso rápido y análisis en tiempo real.

    // ConcurrentHasMap es el mapa más seguro para hilos, ya que no necesita synchronized. El mapa tiene un string de clave
    // que es TEMPERATURA, PRESION Y HUMEDAD y de una cola segura para hilos de valor Double
    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<Double>> data = new ConcurrentHashMap<>();

    public TelemetryStore() { // Constructor que inicializa el mapa con 3 colas vacías.
        data.put("TEMPERATURA", new ConcurrentLinkedQueue<>());
        data.put("PRESION", new ConcurrentLinkedQueue<>());
        data.put("HUMEDAD", new ConcurrentLinkedQueue<>());
    }

    public void add(String sensorName, double value) { // Recibe el nombre del sensor y la temperatura y los añade al mapa
        // Aquí no hace falta synchronized ya que tanto el mapa como la cola es segura por defecto (thread-safe)
        // Estos valores vienen de SensorTask que en el método run, los añade a la variable TelemetryStore store.
        data.get(sensorName).add(value);
    }

    public ConcurrentHashMap<String, ConcurrentLinkedQueue<Double>> getData() { // Devuelve todos los datos (el mapa completo básicamente)
        return data;
    }
}
