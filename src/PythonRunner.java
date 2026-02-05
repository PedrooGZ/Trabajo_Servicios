import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

public class PythonRunner { // Código puente entre java y python.

    public Map<String, Double> runGraficador(String logPath) throws Exception { // Recibe la ruta de telemetria.log
                                                            // Devueleve un diccionario con los promedios
        // Ahora lanzo el proceso python. Se llama al python, se le dice la ruta del archivo del script a ejecutar y el nombre
        // del archivo que se le  va a pasara a python (telemetria.log recibido en la firma del método).
        ProcessBuilder pb = new ProcessBuilder("py", "scripts/graficador.py", logPath);
        pb.redirectErrorStream(true); // mezcla stdout+stderr para que no se pierdan errores, ya que si falla en python,
                                        // aquí en java podremos ver que ha pasado, además así no hay que poner getErrorStream().
        Process p = pb.start(); // Inicio el proceso

        Map<String, Double> medias = new LinkedHashMap<>(); // Lectura de resultados. Esto guarda la clave por ejemplo
                                                            // temperatura con su valor por ejemplo 35º.
                                                            // Se usa un linkedHasMap para que mantenga el orden de inserción.

        // Lee la salida de python línea a línea con BufferedReader. Desde Python se han enviado los datos con un formato
        // para que aquí en java se puedan dividir bien entre la palabra y el valor y así llenar el diccionario.
        try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Linea de depuración
                System.out.println("[PY] " + line); // Muestra por consola lo que python imprima.

                // Parsea para introducir en el diccioneario el par clave-valor mirando si empieza con MEDIA y contiene un =
                // así se pueden extraer ambos valores (clave, valor).
                if (line.startsWith("MEDIA_") && line.contains("=")) {
                    String[] parts = line.split("=", 2);
                    String key = parts[0].trim(); // MEDIA_TEMPERATURA
                    double val = Double.parseDouble(parts[1].trim());
                    medias.put(key, val);
                }
            }
        }

        int exit = p.waitFor(); // Java espera a que python termine y muestra el código, siendo 0 todo bien, 1 error general y
        // 2 archivo no encontrado.
        System.out.println("Proceso Python finalizado. Exit code: " + exit);
        return medias; // Devuelve las medias.
    }

    // Pyton ejecuta el graficador.py, imprime pb.redirectErrorStream(true); controlando la salida y los errores y java lee
    // con el bufferedReader.
}
