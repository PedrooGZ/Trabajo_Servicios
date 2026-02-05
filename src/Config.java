import java.io.FileInputStream;
import java.util.Properties;

public class Config { // Clase en la que se establecen las variables de configuración, se podría hacer en el main, pero
                        // de esta manera, tenemos todo encapsulado en una sola clase lo que hace que esté todo más organizado.
    public final long duracionMs;
    public final long tempPeriodMs;
    public final long presPeriodMs;
    public final long humPeriodMs;

    private Config(long duracionMs, long tempPeriodMs, long presPeriodMs, long humPeriodMs) {
        this.duracionMs = duracionMs;
        this.tempPeriodMs = tempPeriodMs;
        this.presPeriodMs = presPeriodMs;
        this.humPeriodMs = humPeriodMs;
    }

    public static Config load(String path) throws Exception {
        Properties p = new Properties(); // Carga el archivo properties
        try (FileInputStream fis = new FileInputStream(path)) {
            p.load(fis);
        }

        // Valores por defecto
        long duracionMs   = Long.parseLong(p.getProperty("duracion_ms", "60000"));
        long tempPeriodMs = Long.parseLong(p.getProperty("temp_period_ms", "500"));
        long presPeriodMs = Long.parseLong(p.getProperty("pres_period_ms", "800"));
        long humPeriodMs  = Long.parseLong(p.getProperty("hum_period_ms", "1200"));

        // Crea y devuelve el archivo config completo
        return new Config(duracionMs, tempPeriodMs, presPeriodMs, humPeriodMs);
    }
}
