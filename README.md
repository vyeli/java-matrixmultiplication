# Matrix Multiplication Performance Analysis

Este proyecto implementa y compara tres versiones diferentes del algoritmo de multiplicación de matrices:
1. Versión secuencial
2. Versión paralela usando ExecutorService
3. Versión paralela usando ForkJoin

## Requisitos

### Java
- Java Development Kit (JDK) 21
- Apache Maven 3.6 o superior

### Python (para análisis)
- Python 3.8 o superior
- Bibliotecas especificadas en analysis/requirements.txt:
  - pandas
  - matplotlib
  - seaborn
  - numpy

## Configuración y Ejecución

### 1. Compilar y Ejecutar Pruebas

```bash
# Clonar el repositorio
git clone https://github.com/tuusuario/matrixmultiplication.git
cd matrixmultiplication

# Compilar el proyecto
mvn clean compile

# Ejecutar las pruebas
mvn exec:java
```

### 2. Analizar Resultados

```bash
# Ir al directorio de análisis
cd analysis

# Crear y activar entorno virtual
python -m venv venv

# En Windows:
venv\Scripts\activate
# En Linux/Mac:
source venv/bin/activate

# Instalar dependencias
pip install -r requirements.txt

# Ejecutar análisis
python analyze_results.py
```

## Detalles de Implementación

### Matrices
- Tamaño: 1024 x 1024
- Valores: Generados aleatoriamente con semilla fija
- Tipo: double[][]

### Paralelización
- ExecutorService: División por filas
- ForkJoin: División recursiva con umbral de 64 filas

### Mediciones
- Warmup: 2 iteraciones
- Tests: 5 iteraciones por configuración
- Threads: 2, 4, 6, 8, 10, 12

## Resultados

Los resultados se guardan en:
- `results/times.txt`: Tiempos de ejecución
- `results/system_info.txt`: Información del sistema

El análisis genera:
1. Gráfico de tiempos de ejecución
2. Gráfico de speedup
3. Gráfico de eficiencia
4. Reporte detallado

Los gráficos se guardan en `analysis/plots/`.

## Formato de Resultados

### times.txt
```csv
implementation,threads,iteration,time
sequential,1,1,10.123
executor,2,1,5.678
...
```

### Gráficos
- execution_times.png: Comparación de tiempos
- speedup.png: Speedup vs threads
- efficiency.png: Eficiencia vs threads

## Notas

- Los tiempos se miden usando java time Duration
- Se realiza GC antes de cada prueba para minimizar efectos de la recolección de basura
- Los resultados pueden variar según el hardware
