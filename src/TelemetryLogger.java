import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

public class TelemetryLogger { // Escribe los datos en un archivo telemetria.log de forma ordenada y segura
                                // Es una clase para registro y persistencia de datos.
    private final String logPath; // Ruta de telemetria.log

    public TelemetryLogger(String logPath) { // Guarda la ruta del archivo donde se escrbiran los logs.
        this.logPath = logPath;
    }


    public synchronized void logLine(String sensorName, double value) { // Método que escribe los log y debe de ser synchronized
                                                                        // porque sino habría race condition. Si por lo que sea hay algún
                                                                        // hilo que es muy lento escribiendo, el archivo no se corrompe, ya
                                                                        // que los hilos esperan gracias a synchronized.
                                                                        // Recibe las variables de sensorName y value de SensorTask del método run

        String line = String.format(Locale.US, "[%s] | Valor: %.1f%n", sensorName, value); // Escribe con formato y usa Locales.US
                                                                // para que los decimales usen punto y no coma.
        // Escribe el archivo con append true para que escriba all final y lo hacemos en un try-with-resources para que cierre
        // automáticamente el archivo.
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(logPath, true))) {
            bw.write(line);
        } catch (IOException e) {
            // En un entorno real, esto se gestionaría mejor, pero para el ejercicio vale.
            System.err.println("Error escribiendo en log: " + e.getMessage());
        }
    }

    public String getLogPath() { // devuelve
        return logPath;
    }
}
