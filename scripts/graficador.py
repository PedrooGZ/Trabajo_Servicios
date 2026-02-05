import sys
import re
import matplotlib.pyplot as plt #Librería para crear gráficas

# Este Script analiza el archivo log generado por java y crea la gráfica


# Dicionario con listas vacías para cada sensor
def parse_log(path):
    data = {
        "TEMPERATURA": [],
        "PRESION": [],
        "HUMEDAD": []
    }

    # Expresión regular que obtiene la palabra (por ejemplo TEMPERATURA) y el valor con o sin decimales
    # El formato sería el siguiente: [SENSOR] | Valor: XX.X
    pattern = re.compile(r'^\[(\w+)\]\s*\|\s*Valor:\s*([-+]?\d+(\.\d+)?)')

    # Lectura dela archivo
    with open(path, "r", encoding="utf-8") as f:
        for line in f:
            line = line.strip() # Elimina espacios
            m = pattern.match(line)
            if not m:
                continue # Salta líneas que no coincidan
            sensor = m.group(1) # Esto sería de la expresión regular el nombre (por ejemplo TEMPERATURA)
            value = float(m.group(2)) # Esto sería de la expresión regular el valor por ejemplo 25.3
            if sensor in data:
                data[sensor].append(value) # Añade a la lista

    return data

def mean(values): # Funcion de media que lo que hace es que si la lista está vacía hace un promedio simple de suma entre cantidad.
    if not values:
        return 0.0
    return sum(values) / len(values)

def main(): # Este es el momento en el que java lanza el proceso: ProcessBuilder pb = new ProcessBuilder("py", "scripts/graficador.py", logPath);
    if len(sys.argv) < 2:
        print("Uso: python graficador.py telemetria.log")
        sys.exit(1)

    path = sys.argv[1]
    data = parse_log(path)

    # Cálculo de medias
    m_temp = mean(data["TEMPERATURA"])
    m_pres = mean(data["PRESION"])
    m_hum  = mean(data["HUMEDAD"])

    # Salida fácil para el parseo de java
    print(f"MEDIA_TEMPERATURA={m_temp:.2f}")
    print(f"MEDIA_PRESION={m_pres:.2f}")
    print(f"MEDIA_HUMEDAD={m_hum:.2f}")

    # Crea una figura con 3 líneas, una or cada sensor y con una leyenda y ejes.
    plt.figure()
    plt.plot(data["TEMPERATURA"], label="Temperatura (°C)")
    plt.plot(data["PRESION"], label="Presión (hPa)")
    plt.plot(data["HUMEDAD"], label="Humedad (%)")

    plt.xlabel("Muestra")
    plt.ylabel("Valor")
    plt.title("Telemetría: comparación de sensores")
    plt.legend()
    plt.show()

if __name__ == "__main__":
    main()
