import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws Exception {
        String logPath = "telemetria.log"; // Donde se guardan los datos
        String configPath = "config.properties"; // Donde están los parámetros

        // ===== 1) Leer configuración =====
        Properties p = new Properties(); // Crea el objeto para leer propiedades
        try (FileInputStream fis = new FileInputStream(configPath)) { // Abre el archivo
            p.load(fis); // Carga el documento config.properties que pone cada cuanto tiempo
                        // se van a coger muestras de cada cosa.
        }

        // Convierte las propiedades a variables. Se ponen también los ms por defecto por si no los encuentra
        // enn el properties.
        long duracionMs = Long.parseLong(p.getProperty("duracion_ms", "60000"));
        long tempPeriodMs = Long.parseLong(p.getProperty("temp_period_ms", "500"));
        long presPeriodMs = Long.parseLong(p.getProperty("pres_period_ms", "800"));
        long humPeriodMs  = Long.parseLong(p.getProperty("hum_period_ms", "1200"));

        // Convierte string a Path, lo crea si no existe y si existe lo trunca a 0 bytes
        Files.writeString(
                Path.of(logPath),
                "",
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );

        TelemetryStore store = new TelemetryStore(); // Almacena datos de telemetría de forma temporal hasta que se
                                                    // sobreescriben en telemetria.log
        TelemetryLogger logger = new TelemetryLogger(logPath); // Esto coge los datos de TelemetryStore y los escribe
                                                                // de forma ordenada en el telemetria.log

        // Parada por tiempo común: Esto calcula el momento exacto en el que deben de parar todos los sensores
        long endTimeMs = System.currentTimeMillis() + duracionMs;

        // Hilos sensores. Tienen el nombre, el tiempo, la hora de parada, el rango de valores (max y min) y los objetos
        // compartidos, es decir, aquellas clases que usa ese hilo para el funcionamiento del programa tanto telemetry store como logger
        Thread tTemp = new Thread(new SensorTask(
                "TEMPERATURA", tempPeriodMs, endTimeMs,
                10.0, 40.0,
                store, logger
        ));

        Thread tPres = new Thread(new SensorTask(
                "PRESION", presPeriodMs, endTimeMs,
                950.0, 1050.0,
                store, logger
        ));

        Thread tHum = new Thread(new SensorTask(
                "HUMEDAD", humPeriodMs, endTimeMs,
                0.0, 100.0,
                store, logger
        ));

        // Lanzamiento de hilos
        System.out.println("Lanzando sensores durante " + duracionMs + " ms...");
        tTemp.start();
        tPres.start();
        tHum.start();

        // Join para que el main espere hasta que los hilos terminen
        tTemp.join();
        tPres.join();
        tHum.join();

        System.out.println("Sensores finalizados. Log generado en: " + logger.getLogPath());


        PythonRunner runner = new PythonRunner(); // Clase que ejecuta Scripts de python
        Map<String, Double> medias = runner.runGraficador(logger.getLogPath()); // recibe las medias del Scrypt de python ejecutado
                                                                                // y gestionado en PythonRunner

        System.out.println("Medias capturadas desde Python:");
        for (var entry : medias.entrySet()) { // Muestra los datos capturados (clave, valor).
            System.out.printf("%s -> %.2f%n", entry.getKey(), entry.getValue());
        }

        System.out.println("Programa Java finalizado."); // Fin del programa
    }
}
