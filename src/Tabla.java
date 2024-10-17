import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import Excepciones.EtiquetaInvalida;
import Excepciones.TipoIncompatible;

public class Tabla {
    private List<Columna<?>> columnas;
    private List<Fila> filas;
    private Map<String, Integer> indicesColumnas;

    public Tabla() {
        this.columnas = new ArrayList<>();
        this.filas = new ArrayList<>();
        this.indicesColumnas = new HashMap<>();
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

    public <T> Class<T> getTipoDatoColumna(String etiqueta) throws EtiquetaInvalida {
        if (!indicesColumnas.containsKey(etiqueta)) {
            throw new EtiquetaInvalida("La etiqueta de la columna no existe.");
        }
        return (Class<T>) columnas.get(indicesColumnas.get(etiqueta)).getTipoDato();
    }

    public <T> void agregarColumna(String etiqueta, Class<T> tipoDato) throws TipoIncompatible {
        if (!(tipoDato == Integer.class || tipoDato == Double.class || tipoDato == Boolean.class || tipoDato == String.class)) {
            throw new TipoIncompatible("Tipo de dato no soportado. Solo se permiten: Numérico (entero, real), Booleano y Cadena.");
        }
        Columna<T> nuevaColumna = new Columna<>(etiqueta, tipoDato);
        columnas.add(nuevaColumna);
        indicesColumnas.put(etiqueta, columnas.size() - 1);
    }

    public void agregarFila(int id) {
        Fila nuevaFila = new Fila(id);
        filas.add(nuevaFila);
    }

    public void setValorCelda(int idFila, String etiquetaColumna, Object valor) throws EtiquetaInvalida, TipoIncompatible {
        if (idFila < 0 || idFila >= filas.size()) {
            throw new EtiquetaInvalida("El ID de la fila no existe.");
        }
        if (!indicesColumnas.containsKey(etiquetaColumna)) {
            throw new EtiquetaInvalida("La etiqueta de la columna no existe.");
        }
        int columnaIndex = indicesColumnas.get(etiquetaColumna);
        Fila fila = filas.get(idFila);

        // Verificar el tipo de dato y actualizar la celda
        Columna<?> columna = columnas.get(columnaIndex);
        Class<?> tipoDato = columna.getTipoDato();

        if (!tipoDato.isInstance(valor)) {
            throw new TipoIncompatible("El tipo de dato no coincide con el de la columna.");
        }

        // Si no existe la celda, la crea
        if (fila.getFila().size() <= columnaIndex) {
            fila.agregarCelda(new Celda<>(valor));
        } else {
            fila.setValorCelda(columnaIndex, valor);
        }
    }

    public Object getCelda(int idFila, String etiquetaColumna) throws EtiquetaInvalida {
        if (idFila < 0 || idFila >= filas.size()) {
            throw new EtiquetaInvalida("El ID de la fila no existe.");
        }
        if (!indicesColumnas.containsKey(etiquetaColumna)) {
            throw new EtiquetaInvalida("La etiqueta de la columna no existe.");
        }
        int columnaIndex = indicesColumnas.get(etiquetaColumna);
        return filas.get(idFila).getFila().get(columnaIndex).getValor();
    }

    public void visualizar(int maxFilas, int maxColumnas, int maxAnchoCelda) {
        int totalFilas = filas.size();
        int totalColumnas = columnas.size();
    
        int filasMostrar = Math.min(maxFilas, totalFilas);
        int columnasMostrar = Math.min(maxColumnas, totalColumnas);
    
        // Imprimir etiquetas de columnas
        System.out.print(String.format("%-" + (maxAnchoCelda + 1) + "s", "")); // Espacio para las etiquetas de filas
        for (int i = 0; i < columnasMostrar; i++) {
            String etiqueta = columnas.get(i).getEtiquetaColumna();
            System.out.print(formatearTexto(etiqueta, maxAnchoCelda) + " ");
        }
        System.out.println();
    
        // Imprimir filas y sus celdas
        for (int i = 0; i < filasMostrar; i++) {
            Fila fila = filas.get(i);
            System.out.print(formatearTexto(fila.getID() + "", maxAnchoCelda) + " "); // Imprime el ID de la fila
    
            for (int j = 0; j < columnasMostrar; j++) {
                Object valor = fila.getFila().get(j).getValor();
                String textoCelda = (valor == null) ? "NA" : valor.toString();
                System.out.print(formatearTexto(textoCelda, maxAnchoCelda) + " ");
            }
            System.out.println();
        }
    
        // Mostrar resumen si hay más filas o columnas de las que se imprimen
        if (totalFilas > filasMostrar) {
            System.out.println("... (" + (totalFilas - filasMostrar) + " filas más)");
        }
        if (totalColumnas > columnasMostrar) {
            System.out.println("... (" + (totalColumnas - columnasMostrar) + " columnas más)");
        }
    }
    
    // Método auxiliar para truncar o rellenar con espacios los textos de las celdas
    private String formatearTexto(String texto, int maxAncho) {
        // Si el texto es más largo que el máximo ancho permitido, lo truncamos
        if (texto.length() > maxAncho) {
            return texto.substring(0, maxAncho);
        }
    
        // Si el texto es más corto, lo rellenamos con espacios
        return String.format("%-" + maxAncho + "s", texto);
    }

    public void eliminarColumna(String etiqueta) throws EtiquetaInvalida {
        if (!indicesColumnas.containsKey(etiqueta)) {
            throw new EtiquetaInvalida("La etiqueta de la columna no existe.");
        }
        int columnaIndex = indicesColumnas.get(etiqueta);
        columnas.remove(columnaIndex);
        indicesColumnas.remove(etiqueta);

        // Actualizar índices de las columnas
        for (int i = columnaIndex; i < columnas.size(); i++) {
            String nuevaEtiqueta = columnas.get(i).getEtiquetaColumna();
            indicesColumnas.put(nuevaEtiqueta, i);
        }
    }

    public void eliminarFila(int idFila) throws EtiquetaInvalida {
        if (idFila < 0 || idFila >= filas.size()) {
            throw new EtiquetaInvalida("El ID de la fila no existe.");
        }
        filas.remove(idFila);

        // Actualizar IDs de las filas
        for (int i = idFila; i < filas.size(); i++) {
            filas.get(i).setID(i);
        }
    }

    public Columna getColumna(String etiqueta) throws EtiquetaInvalida {
        if (!indicesColumnas.containsKey(etiqueta)) {
            throw new EtiquetaInvalida("La etiqueta de la columna no existe.");
        }
        return columnas.get(indicesColumnas.get(etiqueta));
    }

    public Fila getFila(int idFila) throws EtiquetaInvalida {
        if (idFila < 0 || idFila >= filas.size()) {
            throw new EtiquetaInvalida("El ID de la fila no existe.");
        }
        return filas.get(idFila);
    }
}


