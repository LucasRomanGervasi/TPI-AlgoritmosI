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

        System.out.print(String.format("%-10s", "ID"));
        for (int i = 0; i < columnasMostrar; i++) {
            String etiqueta = columnas.get(i).getEtiquetaColumna();
            System.out.print(etiqueta + " ".repeat(maxAnchoCelda - etiqueta.length()));
        }
        System.out.println();

        for (int i = 0; i < filasMostrar; i++) {
            Fila fila = filas.get(i);
            System.out.print(String.format("%-10s", fila.getID()));

            for (int j = 0; j < columnasMostrar; j++) {
                Object valor = fila.getFila().get(j).getValor();
                String textoCelda = (valor == null) ? "NA" : valor.toString();
                System.out.print(textoCelda + " ".repeat(maxAnchoCelda - textoCelda.length()));
            }
            System.out.println();
        }

        if (totalFilas > filasMostrar) {
            System.out.println("... (" + (totalFilas - filasMostrar) + " filas más)");
        }
        if (totalColumnas > columnasMostrar) {
            System.out.println("... (" + (totalColumnas - columnasMostrar) + " columnas más)");
        }
    }
}

