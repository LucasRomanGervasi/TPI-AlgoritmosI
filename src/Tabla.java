import Excepciones.EtiquetaInvalida;
import Excepciones.TipoIncompatible;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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

    public void muestrear(double porcentaje, int maxAnchoCelda) {
    if (porcentaje < 0 || porcentaje > 100) {
        throw new IllegalArgumentException("El porcentaje debe estar entre 0 y 100.");
    }

    int totalFilas = filas.size();
    int totalColumnas = columnas.size();

    // Calcular la cantidad de filas a muestrear en base al porcentaje
    int filasAMostrar = (int) Math.ceil((porcentaje / 100) * totalFilas);
    
    if (filasAMostrar == 0) {
        System.out.println("No hay filas suficientes para muestrear.");
        return;
    }

    // Seleccionar índices de filas de forma aleatoria
    Random random = new Random();
    List<Integer> indicesAleatorios = new ArrayList<>();
    
    while (indicesAleatorios.size() < filasAMostrar) {
        int indiceAleatorio = random.nextInt(totalFilas);
        if (!indicesAleatorios.contains(indiceAleatorio)) {
            indicesAleatorios.add(indiceAleatorio);
        }
    }

    // Imprimir etiquetas de columnas
    System.out.print(String.format("%-" + (maxAnchoCelda + 1) + "s", "")); // Espacio para las etiquetas de filas
    for (int i = 0; i < totalColumnas; i++) {
        String etiqueta = columnas.get(i).getEtiquetaColumna();
        System.out.print(formatearTexto(etiqueta, maxAnchoCelda) + " ");
    }
    System.out.println();

    // Imprimir las filas seleccionadas
    for (int indiceFila : indicesAleatorios) {
        Fila fila = filas.get(indiceFila);
        System.out.print(formatearTexto(fila.getID() + "", maxAnchoCelda) + " "); // Imprime el ID de la fila

        for (int j = 0; j < totalColumnas; j++) {
            Object valor = fila.getFila().get(j).getValor();
            String textoCelda = (valor == null) ? "NA" : valor.toString();
            System.out.print(formatearTexto(textoCelda, maxAnchoCelda) + " ");
        }
        System.out.println();
        }
    }
    public void seleccionar(List<String> etiquetasColumnas, List<Integer> indicesFilas, int maxAnchoCelda) throws EtiquetaInvalida {
        // Validar que las etiquetas de columnas existan
        List<Integer> columnasASeleccionar = new ArrayList<>();
        for (String etiqueta : etiquetasColumnas) {
            if (!indicesColumnas.containsKey(etiqueta)) {
                throw new EtiquetaInvalida("La etiqueta de la columna '" + etiqueta + "' no existe.");
            }
            columnasASeleccionar.add(indicesColumnas.get(etiqueta));
        }
    
        // Validar que los índices de filas sean válidos
        for (int indiceFila : indicesFilas) {
            if (indiceFila < 0 || indiceFila >= filas.size()) {
                throw new EtiquetaInvalida("El índice de fila '" + indiceFila + "' no es válido.");
            }
        }
    
        // Imprimir las etiquetas de las columnas seleccionadas
        System.out.print(String.format("%-" + (maxAnchoCelda + 1) + "s", "")); // Espacio para los IDs de las filas
        for (int indiceColumna : columnasASeleccionar) {
            String etiqueta = columnas.get(indiceColumna).getEtiquetaColumna();
            System.out.print(formatearTexto(etiqueta, maxAnchoCelda) + " ");
        }
        System.out.println();
    
        // Imprimir las filas seleccionadas
        for (int indiceFila : indicesFilas) {
            Fila fila = filas.get(indiceFila);
            System.out.print(formatearTexto(fila.getID() + "", maxAnchoCelda) + " "); // Imprime el ID de la fila
    
            for (int indiceColumna : columnasASeleccionar) {
                Object valor = fila.getFila().get(indiceColumna).getValor();
                String textoCelda = (valor == null) ? "NA" : valor.toString();
                System.out.print(formatearTexto(textoCelda, maxAnchoCelda) + " ");
            }
            System.out.println();
        }
    }
    public void head(int x, int maxAnchoCelda) {
        if (x <= 0) {
            System.out.println("Debe seleccionar al menos una fila.");
            return;
        }
        int filasMostrar = Math.min(x, filas.size());
        visualizar(filasMostrar, columnas.size(), maxAnchoCelda);
    } //PREGUNTAR COMO SON LOS METODOS HEAD Y TAIL
    public void tail(int x, int maxAnchoCelda) {
        if (x <= 0) {
            System.out.println("Debe seleccionar al menos una fila.");
            return;
        }
        int totalFilas = filas.size();
        int filasMostrar = Math.min(x, totalFilas);
    
        // Imprimir etiquetas de columnas
        System.out.print(String.format("%-" + (maxAnchoCelda + 1) + "s", "")); // Espacio para las etiquetas de filas
        for (int i = 0; i < columnas.size(); i++) {
            String etiqueta = columnas.get(i).getEtiquetaColumna();
            System.out.print(formatearTexto(etiqueta, maxAnchoCelda) + " ");
        }
        System.out.println();
    
        // Imprimir las últimas 'x' filas
        for (int i = totalFilas - filasMostrar; i < totalFilas; i++) {
            Fila fila = filas.get(i);
            System.out.print(formatearTexto(fila.getID() + "", maxAnchoCelda) + " "); // Imprime el ID de la fila
    
            for (int j = 0; j < columnas.size(); j++) {
                Object valor = fila.getFila().get(j).getValor();
                String textoCelda = (valor == null) ? "NA" : valor.toString();
                System.out.print(formatearTexto(textoCelda, maxAnchoCelda) + " ");
            }
            System.out.println();
        }
    }

    public void eliminarTodosNAs() {
        // Recorrer las filas y actualizar las celdas que tienen NA en cualquier columna
        for (Fila fila : filas) {
            for (int j = 0; j < fila.getFila().size(); j++) {
                Celda<?> celda = fila.getFila().get(j);
                Object valor = celda.getValor();
    
                // Verificar si la celda es NA o equivalente
                if (valor == null || 
                    valor.equals("NA") || 
                    valor.equals("NAN") || 
                    valor.equals("null")) {
                    // Intentar establecer el valor de la celda como cadena vacía
                    try {
                        fila.setValorCelda(j, ""); // Establece el valor como cadena vacía
                    } catch (TipoIncompatible e) {
                        // Manejo de la excepción en caso de tipo incompatible
                        System.err.println("No se pudo establecer el valor vacío en la celda: " + e.getMessage());
                    }
                }
            }
        }
    }
}


