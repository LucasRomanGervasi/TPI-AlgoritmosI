import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import Excepciones.EtiquetaInvalida;


public class Tabla {
    private List<Columna<?>> columnas;
    private List<Fila> filas;
    private Map<String, Integer> indicesColumnas;
    private Map<String, Integer> indicesFilas;

    public Tabla() {
        this.columnas = new ArrayList<>();
        this.filas = new ArrayList<>();
        this.indicesColumnas = new HashMap<>();
        this.indicesFilas = new HashMap<>();
    }

    public int getCantidadColumnas() {
        return columnas.size();
    }

    public int getCantidadFilas() {
        return filas.size();
    }

    public List<String> getEtiquetasColumnas() {
        List<String> etiquetas = new ArrayList<>();
        for (Columna<?> columna : columnas) {
            etiquetas.add(columna.getEtiquetaColumna());
        }
        return etiquetas;
    }

    public List<String> getEtiquetasFilas() {
        List<String> etiquetas = new ArrayList<>();
        for (Fila fila : filas) {
            etiquetas.add(fila.getEtiquetaFila());
        }
        return etiquetas;
    }

    public <T> Class<T> getTipoDatoColumna(String etiqueta) throws EtiquetaInvalida {
        if (!indicesColumnas.containsKey(etiqueta)) {
            throw new EtiquetaInvalida("La etiqueta de la columna no existe.");
        }
        return (Class<T>) columnas.get(indicesColumnas.get(etiqueta)).getTipoDato();
    }

    public <T> void agregarColumna(String etiqueta, Class<T> tipoDato) {
        Columna<T> nuevaColumna = new Columna<>(etiqueta, tipoDato);
        columnas.add(nuevaColumna);
        indicesColumnas.put(etiqueta, columnas.size() - 1);
    }

    public void agregarFila(String etiqueta) {
        Fila nuevaFila = new Fila(etiqueta);
        filas.add(nuevaFila);
        indicesFilas.put(etiqueta, filas.size() - 1);
    }

    public Object getCelda(String etiquetaFila, String etiquetaColumna) throws EtiquetaInvalida {
        if (!indicesFilas.containsKey(etiquetaFila)) {
            throw new EtiquetaInvalida("La etiqueta de la fila no existe.");
        }
        if (!indicesColumnas.containsKey(etiquetaColumna)) {
            throw new EtiquetaInvalida("La etiqueta de la columna no existe.");
        }
        int filaIndex = indicesFilas.get(etiquetaFila);
        int columnaIndex = indicesColumnas.get(etiquetaColumna);
        return filas.get(filaIndex).getFila().get(columnaIndex).getValor();
    }

    public Fila getFila(String etiquetaFila) throws EtiquetaInvalida {
        if (!indicesFilas.containsKey(etiquetaFila)) {
            throw new EtiquetaInvalida("La etiqueta de la fila no existe.");
        }
        return filas.get(indicesFilas.get(etiquetaFila));
    }

    public Columna<?> getColumna(String etiquetaColumna) throws EtiquetaInvalida {
        if (!indicesColumnas.containsKey(etiquetaColumna)) {
            throw new EtiquetaInvalida("La etiqueta de la columna no existe.");
        }
        return columnas.get(indicesColumnas.get(etiquetaColumna));
    }

}
