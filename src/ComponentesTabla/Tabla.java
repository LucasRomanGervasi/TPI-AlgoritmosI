package ComponentesTabla;
import Excepciones.DimensionesIncompatibles;
import Excepciones.EtiquetaInvalida;
import Excepciones.TipoIncompatible;
import Interfaces.*;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Tabla implements Visualizacion {
    private List<Columna<?>> columnas;
    private Map<String, Integer> indicesColumnas;

    public Tabla(Object[][] matriz) {
        try {
            crearDesdeMatriz(matriz);
        } catch (TipoIncompatible | EtiquetaInvalida e) {
            System.err.println("Error al crear la tabla desde la matriz: " + e.getMessage());
        }
    }

    public Tabla(String rutaArchivoCSV, boolean headers, int maxCaracteresPorCelda) {
        try {
            crearDesdeArchivoCSV(rutaArchivoCSV, headers, maxCaracteresPorCelda);
        } catch (IOException e) {
            System.err.println("Error de IO al leer el archivo CSV: " + e.getMessage());
        } catch (TipoIncompatible | EtiquetaInvalida e) {
            System.err.println("Error al crear la tabla desde el archivo CSV: " + e.getMessage());
        }
    }

    public Tabla(List<Object> secuenciaLineal) {
        try {
            crearDesdeSecuenciaLineal(secuenciaLineal);
        } catch (TipoIncompatible | EtiquetaInvalida e) {
            System.err.println("Error al crear la tabla desde la secuencia lineal: " + e.getMessage());
        }
    }

    private void crearDesdeMatriz(Object[][] matriz) throws TipoIncompatible, EtiquetaInvalida {
        columnas = new ArrayList<>();
        indicesColumnas = new HashMap<>();
    
        // Verificar si la matriz es nula o no tiene filas suficientes
        if (matriz == null || matriz.length < 1) {
            throw new IllegalArgumentException("La matriz es nula o no tiene suficientes filas.");
        }
    
        // Crear columnas basadas en la primera fila de la matriz (etiquetas)
        for (int j = 0; j < matriz[0].length; j++) {
            if (!(matriz[0][j] instanceof String || matriz[0][j] instanceof Integer)) {
                throw new EtiquetaInvalida("La etiqueta de la columna debe ser un String o Integer.");
            }
    
            String etiqueta = matriz[0][j].toString();
            Class<?> tipoDato = determinarTipoColumna(matriz, j); // Determinar el tipo de la columna
            agregarColumna(etiqueta, tipoDato);
            indicesColumnas.put(etiqueta, columnas.size() - 1);
        }
    
        for (int i = 1; i < matriz.length; i++) {
            agregarFila();
            for (int j = 0; j < matriz[i].length; j++) {
                if (j < columnas.size()) {
                    Object valor = matriz[i][j];
    
                    // Mantener null sin asignar valor predeterminado
                    if (valor == null || valor.equals("NA") || valor.equals("NAN")) {
                        valor = null;
                    } else {
                        // Verificar y convertir el valor si no coincide con el tipo de la columna
                        Class<?> tipoColumna = columnas.get(j).getTipoDato();
                        if (!tipoColumna.isInstance(valor)) {
                            try {
                                valor = convertirValor(valor, tipoColumna);
                            } catch (TipoIncompatible e) {
                                System.err.println("Error al convertir el valor en la columna " + columnas.get(j).getEtiquetaColumna() + ": " + e.getMessage());
                                continue;
                            }
                        }
                    }
                    setValorCelda(i - 1, columnas.get(j).getEtiquetaColumna(), valor);
                }
            }
        }
    }
    
    // Método para determinar el tipo de cada columna
    private Class<?> determinarTipoColumna(Object[][] matriz, int columnaIndex) {
        boolean esEntero = true;
        boolean esDouble = true;
        boolean esBoolean = true;
    
        for (int i = 1; i < matriz.length; i++) {
            Object valor = matriz[i][columnaIndex];
    
            if (valor == null 
            || (valor instanceof String && (
                ((String) valor).equalsIgnoreCase("null") 
                || ((String) valor).equalsIgnoreCase("NA") 
                || ((String) valor).equalsIgnoreCase("NAN")
            ))) {
            continue;
            }
            if (valor instanceof String) {
                valor = ((String) valor).toUpperCase();
            }
        
            String valorStr = valor.toString();
            
            if (esEntero) {
                try {
                    Integer.parseInt(valorStr);
                } catch (NumberFormatException e) {
                    esEntero = false;
                }
            }
    
            if (esDouble) {
                try {
                    Double.parseDouble(valorStr);
                } catch (NumberFormatException e) {
                    esDouble = false;
                }
            }
    
            if (esBoolean) {
                if (!valorStr.equalsIgnoreCase("true") && !valorStr.equalsIgnoreCase("false")) {
                    esBoolean = false;
                }
            }
    
            // Si todos los tipos fallan, la columna es de tipo String
            if (!esEntero && !esDouble && !esBoolean) {
                return String.class;
            }
        }
    
        // Prioridad en tipos: Integer > Double > Boolean > String
        if (esEntero) return Integer.class;
        if (esDouble) return Double.class;
        if (esBoolean) return Boolean.class;
        return String.class;
    }
    
    // Método para convertir valores al tipo de dato correspondiente
    private Object convertirValor(Object valor, Class<?> tipoColumna) throws TipoIncompatible {
        String valorStr = valor.toString();
        
        try {
            if (tipoColumna == Integer.class) {
                return Integer.parseInt(valorStr);
            } else if (tipoColumna == Double.class) {
                return Double.parseDouble(valorStr);
            } else if (tipoColumna == Boolean.class) {
                return Boolean.parseBoolean(valorStr);
            } else if (tipoColumna == String.class) {
                return valorStr;
            }
        } catch (NumberFormatException e) {
            throw new TipoIncompatible("No se puede convertir el valor a " + tipoColumna.getName());
        }
        return valorStr; // Retornar como String si no se reconoce el tipo
    }
    
    private void crearDesdeArchivoCSV(String rutaArchivoCSV, boolean headers, int maxCaracteresPorCelda) throws IOException, TipoIncompatible, EtiquetaInvalida {
        if (rutaArchivoCSV == null || rutaArchivoCSV.isEmpty()) {
            throw new IllegalArgumentException("La ruta del archivo CSV no puede estar vacía.");
        }

        columnas = new ArrayList<>();
        indicesColumnas = new HashMap<>();

        List<List<String>> datos = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivoCSV))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] valores = linea.split(",");
                datos.add(Arrays.asList(valores));
            }
        }

        if (datos.isEmpty()) {
            throw new IOException("El archivo CSV está vacío.");
        }

        // Si no hay encabezados, eliminamos la primera fila (que sería la de encabezados)
        if (!headers && !datos.isEmpty()) {
            datos.remove(0);
        }

        // Generar etiquetas para las columnas: si no hay encabezados, usamos etiquetas vacías
        List<String> etiquetas = headers ? datos.remove(0) : generarEtiquetasUnicas(datos.get(0).size());
        int numColumnas = etiquetas.size();

        // Si no hay encabezados, reemplazamos las etiquetas con espacios vacíos incrementales
        if (!headers) {
            for (int i = 0; i < numColumnas; i++) {
                etiquetas.set(i, " ".repeat(i)); // Reemplazar con espacios vacíos incrementales
            }
        }

        // Agregar las columnas con las etiquetas adecuadas
        for (int col = 0; col < numColumnas; col++) {
            String etiqueta = etiquetas.get(col);
            Class<?> tipoDato = null;

            for (int fila = 0; fila < datos.size(); fila++) {
                String valor = datos.get(fila).get(col);
                if (valor != null && !valor.isEmpty()) {
                    Class<?> tipoActual = inferirTipoDato(valor);
                    if (tipoDato == null) {
                        tipoDato = tipoActual;
                    } else if (!tipoDato.equals(tipoActual)) {
                        tipoDato = String.class;
                        break;
                    }
                }
            }

            agregarColumna(etiqueta, tipoDato);
        }

        // Ahora agregamos las filas, ya sin la primera fila (si fue eliminada)
        for (int fila = 0; fila < datos.size(); fila++) {
            agregarFila();
            for (int col = 0; col < numColumnas; col++) {
                String valor = datos.get(fila).get(col);

                if (valor != null && valor.length() > maxCaracteresPorCelda) {
                    valor = valor.substring(0, maxCaracteresPorCelda);
                }

                Object valorConvertido = (valor == null || valor.isEmpty()) 
                    ? null 
                    : convertirValor(valor, columnas.get(col).getTipoDato());
                setValorCelda(fila, columnas.get(col).getEtiquetaColumna(), valorConvertido);
            }
        }
        }


    private List<String> generarEtiquetasUnicas(int numColumnas) {
        List<String> etiquetas = new ArrayList<>();
        for (int i = 0; i < numColumnas; i++) {
            // Generar etiquetas con espacios en blanco
            String etiqueta = " ".repeat(i); // " ", "  ", "   ", etc.
            etiquetas.add(etiqueta);
        }
        return etiquetas;
    }

    // Método auxiliar para inferir el tipo de dato de un valor en formato String
    private Class<?> inferirTipoDato(String valor) {
        if (valor == null || valor.equalsIgnoreCase("NA") || valor.equalsIgnoreCase("NAN") || valor.equalsIgnoreCase("null")) {
            return String.class;
        }
        try {
            Integer.parseInt(valor);
            return Integer.class;
        } catch (NumberFormatException e1) {
            try {
                Double.parseDouble(valor);
                return Double.class;
            } catch (NumberFormatException e2) {
                if (valor.equalsIgnoreCase("true") || valor.equalsIgnoreCase("false")) {
                    return Boolean.class;
                } else {
                    return String.class;
                }
            }
        }
    }
    

    private void crearDesdeSecuenciaLineal(List<Object> secuenciaLineal) throws TipoIncompatible, EtiquetaInvalida {
        if (secuenciaLineal == null || secuenciaLineal.isEmpty()) {
            throw new IllegalArgumentException("La secuencia lineal no puede estar vacía.");
        }
    
        columnas = new ArrayList<>();
        indicesColumnas = new HashMap<>();
    
        // Determinar el tipo de cada columna basándose en la primera fila
        List<?> primeraFila = (List<?>) secuenciaLineal.get(0);
        for (int i = 0; i < primeraFila.size(); i++) {
            Object valor = primeraFila.get(i);
            Class<?> tipoColumna = (valor != null) ? valor.getClass() : String.class;
            agregarColumna("Columna" + (i + 1), tipoColumna);
        }
    
        // Procesar cada fila y agregar valores a las columnas
        for (Object filaObj : secuenciaLineal) {
            if (filaObj instanceof List) {
                List<?> fila = (List<?>) filaObj;
                if (fila.size() != columnas.size()) {
                    throw new IllegalArgumentException("La fila tiene un número diferente de columnas al esperado.");
                }
                
                agregarFila();
                
                for (int i = 0; i < fila.size(); i++) {
                    String etiquetaColumna = "Columna" + (i + 1);
                    Object valor = fila.get(i);
                    Class<?> tipoEsperado = columnas.get(i).getTipoDato();
    
                    // Permitir valores null, "NA" o "NAN"
                    if (valor == null || valor.equals("NA") || valor.equals("NAN")) {
                        setValorCelda(getCantidadFilas() - 1, etiquetaColumna, null);
                    } else {
                        // Verificar si el valor es del tipo esperado, y convertirlo si es necesario
                        if (!tipoEsperado.isInstance(valor)) {
                            valor = convertirValor(valor.toString(), tipoEsperado);
                        }
                        setValorCelda(getCantidadFilas() - 1, etiquetaColumna, valor);
                    }
                }
            } else {
                throw new IllegalArgumentException("Cada fila en la secuencia debe ser una lista de objetos.");
            }
        }
    }
    

    public int getCantidadColumnas() {
        return columnas.size();
    }

    public int getCantidadFilas() {
        // Todas las columnas deben tener la misma cantidad de filas
        return columnas.isEmpty() ? 0 : columnas.get(0).getCeldas().size();
    }

    public List<Object> getEtiquetasColumnas() {
        List<Object> etiquetas = new ArrayList<>();
        for (Columna<?> columna : columnas) {
            etiquetas.add(columna.getEtiquetaColumna());
        }
        return etiquetas;
    }

    public List<Object> getEtiquetasFilas() {
        List<Object> etiquetas = new ArrayList<>();
        for (int i = 0; i < getCantidadFilas(); i++) {
            etiquetas.add(i);
        }
        return etiquetas;
    }

    @SuppressWarnings("unchecked")
    public <T> Class<T> getTipoDatoColumna(String etiqueta) throws EtiquetaInvalida {
        if (!indicesColumnas.containsKey(etiqueta)) {
            throw new EtiquetaInvalida("La etiqueta de la columna no existe.");
        }
        return (Class<T>) columnas.get(indicesColumnas.get(etiqueta)).getTipoDato();
    }

    public List<Object> getFila(int idFila) throws EtiquetaInvalida {
        if (idFila < 0 || idFila >= getCantidadFilas()) {
            throw new EtiquetaInvalida("El ID de la fila no existe.");
        }
        List<Object> fila = new ArrayList<>();
        for (Columna<?> columna : columnas) {
            fila.add(columna.getValor(idFila));
        }
        return fila;
    }

    public Columna<?> getColumna(Object etiqueta) throws EtiquetaInvalida {
                if (!indicesColumnas.containsKey(etiqueta)) {
                    throw new EtiquetaInvalida("La etiqueta de la columna no existe.");
                }
                return columnas.get(indicesColumnas.get(etiqueta));
    }

    public Object getCelda(int idFila, Object etiquetaColumna) throws EtiquetaInvalida {
        if (idFila < 0 || idFila >= getCantidadFilas()) {
            throw new EtiquetaInvalida("El ID de la fila no existe.");
        }
        if (!indicesColumnas.containsKey(etiquetaColumna)) {
            throw new EtiquetaInvalida("La etiqueta de la columna no existe.");
        }
        int columnaIndex = indicesColumnas.get(etiquetaColumna);
        return columnas.get(columnaIndex).getValor(idFila);
    }

    @Override
    public void mostrar(int maxFilas, int maxColumnas, int maxAnchoCelda, int filaInicio) {
        int totalFilas = getCantidadFilas();
        int totalColumnas = getCantidadColumnas();
        int filasMostrar = Math.min(maxFilas, totalFilas - filaInicio); // Ajusta filas a mostrar desde filaInicio
        int columnasMostrar = Math.min(maxColumnas, totalColumnas); // Ajusta columnas a mostrar
        System.out.print(String.format("%-" + (maxAnchoCelda + 1) + "s", "")); // Espacio para las etiquetas de filas
        for (int i = 0; i < columnasMostrar; i++) {
            String etiqueta = columnas.get(i).getEtiquetaColumna();
            System.out.print(formatearTexto(etiqueta, maxAnchoCelda) + " ");
        }
        System.out.println();
        
        // Imprimir filas y sus celdas desde filaInicio
        for (int i = filaInicio; i < filaInicio + filasMostrar; i++) {
            System.out.print(formatearTexto(i + "", maxAnchoCelda) + " "); // Imprime el ID de la fila
        
            for (int j = 0; j < columnasMostrar; j++) {
                Object valor = columnas.get(j).getValor(i);
                String textoCelda = (valor == null) ? "NA" : valor.toString();
                System.out.print(formatearTexto(textoCelda, maxAnchoCelda) + " ");
            }
            System.out.println();
        }
        
        // Mostrar resumen si hay más filas o columnas de las que se imprimen
        if (totalFilas > filaInicio + filasMostrar) {
            System.out.println("... (" + (totalFilas - (filaInicio + filasMostrar)) + " filas más)");
        }
        if (totalColumnas > columnasMostrar) {
            System.out.println("... (" + (totalColumnas - columnasMostrar) + " columnas más)");
        }
    }
    
    // Método auxiliar para truncar o rellenar con espacios los textos de las celdas
    private String formatearTexto(String etiqueta, int maxAncho) {
            // Si el texto es más largo que el máximo ancho permitido, lo truncamos
            if (etiqueta.length() > maxAncho) {
                return etiqueta.substring(0, maxAncho);
            }
        
            // Si el texto es más corto, lo rellenamos con espacios
            return String.format("%-" + maxAncho + "s", etiqueta);
    }

    public void setValorCelda(int fila, String etiquetaColumna, Object valor) throws TipoIncompatible, EtiquetaInvalida {
        Columna<?> columna = getColumna(etiquetaColumna);
    
        if (valor == null) {
            columna.setValor(fila, null);
            return;
        }
    
        Class<?> tipoEsperado = columna.getTipoDato();
        if (!tipoEsperado.isInstance(valor)) {
            if (tipoEsperado == String.class && valor instanceof String) {
                columna.setValor(fila, valor);
            } else {
                Object valorConvertido = convertValue(valor, tipoEsperado);
                columna.setValor(fila, valorConvertido);
            }
        } else {
            columna.setValor(fila, valor);
        }
    }

    private Object convertValue(Object value, Class<?> targetType) throws TipoIncompatible {
        if (value == null) return null;
    
        // Si el valor ya es del tipo esperado, devuélvelo sin conversión
        if (targetType.isInstance(value)) {
            return value;
        }
    
        // Conversiones específicas para Integer, Double, Boolean y String
        try {
            System.out.println(targetType);
            if (targetType == Integer.class && value instanceof String) {
                return Integer.parseInt((String) value);
            } else if (targetType == Double.class && value instanceof String) {
                return Double.parseDouble((String) value);
            } else if (targetType == Boolean.class && value instanceof String) {
                return Boolean.parseBoolean((String) value);
            } else if (targetType == String.class) {
                return value.toString();
            } else {
                throw new TipoIncompatible("Tipo de dato no soportado: " + targetType.getName());
            }
        } catch (NumberFormatException e) {
            throw new TipoIncompatible("Error al convertir el valor: " + value + " a tipo: " + targetType.getSimpleName());
        }
    }

    public <T> void agregarColumna(String etiqueta, Class<T> tipoDato) throws TipoIncompatible, EtiquetaInvalida {
        if (!(tipoDato == Integer.class || tipoDato == Double.class || tipoDato == Boolean.class || tipoDato == String.class || tipoDato == null)) {
            throw new TipoIncompatible("Tipo de dato no soportado. Solo se permiten: Numérico (entero, real), Booleano y Cadena.");
        }
        Columna<?> nuevaColumna = new Columna<>(etiqueta, tipoDato);
        for (int i = 0; i < getCantidadFilas(); i++) {
            nuevaColumna.agregarCelda(null); // Asigna un valor nulo por defecto
        }
    
        columnas.add(nuevaColumna);
        indicesColumnas.put(etiqueta, columnas.size() - 1);
    }

    public <T> void agregarColumna(String etiqueta, Class<T> tipoDato, List<Object> celdas) throws TipoIncompatible, EtiquetaInvalida {
        if (!(tipoDato == Integer.class || tipoDato == Double.class || tipoDato == Boolean.class || tipoDato == String.class || tipoDato == null)) {
            throw new TipoIncompatible("Tipo de dato no soportado. Solo se permiten: Numérico (entero, real), Booleano y Cadena.");
        }
        
        if (getCantidadFilas() != celdas.size()) {
            throw new IllegalArgumentException("El número de celdas proporcionadas no coincide con el número de filas existentes.");
        }
        
        @SuppressWarnings({ "rawtypes", "unchecked" })
        Columna nuevaColumna = new Columna(etiqueta, tipoDato);
        
        for (Object valor : celdas) {
            // Verificar que el valor corresponde al tipo especificado
            if (valor != null && !tipoDato.isInstance(valor)) {
                throw new TipoIncompatible("El valor '" + valor + "' no es compatible con el tipo de dato de la columna " + tipoDato.getSimpleName());
            }
            nuevaColumna.agregarCelda(valor);
        }
        
        columnas.add(nuevaColumna);
        indicesColumnas.put(etiqueta, columnas.size() - 1);
    }
    
    public void eliminarFila(int idFila) throws EtiquetaInvalida {
        if (idFila < 0 || idFila >= getCantidadFilas()) {
            throw new EtiquetaInvalida("El ID de la fila no existe.");
        }

        // Eliminar la celda en cada columna en la fila dada
        for (Columna<?> columna : columnas) {
            columna.eliminarValor(idFila);
        }
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

    public void agregarFila() throws TipoIncompatible {
    
        // Agregar un nuevo valor nulo en cada columna en la nueva fila
        for (Columna<?> columna : columnas) {
            columna.agregarCelda(null);
        }
    }

    public void agregarFila(List<Object> valores) throws TipoIncompatible {
        if (valores.size() != columnas.size()) {
            throw new TipoIncompatible("La cantidad de valores no coincide con la cantidad de columnas.");
        }
    
        // Agregar cada valor en su columna respectiva
        for (int i = 0; i < columnas.size(); i++) {
            Columna<?> columna = columnas.get(i);
            Object valor = valores.get(i);
    
            // Verificar que el valor sea del tipo correcto
            if (valor != null && !columna.getTipoDato().isInstance(valor)) {
                throw new TipoIncompatible("El valor en la columna '" + columna.getEtiquetaColumna() +
                                           "' no es compatible con el tipo de datos de la columna.");
            }
    
            columna.agregarCelda(valor);
        }
    }
    
    public void seleccionar(List<String> etiquetasColumnas, List<Integer> indicesFilas, int maxAnchoCelda) throws EtiquetaInvalida {
        if (maxAnchoCelda < 1) {
            throw new IllegalArgumentException("El ancho de celda debe ser mayor o igual a 1.");
            
        }
        List<Integer> columnasASeleccionar = new ArrayList<>();
        for (String etiqueta : etiquetasColumnas) {
            if (!indicesColumnas.containsKey(etiqueta)) {
                throw new EtiquetaInvalida("La etiqueta de la columna '" + etiqueta + "' no existe.");
            }
            columnasASeleccionar.add(indicesColumnas.get(etiqueta));
        }
    
        int totalFilas = getCantidadFilas();
        for (int indiceFila : indicesFilas) {
            if (indiceFila < 0 || indiceFila >= totalFilas) {
                throw new EtiquetaInvalida("El índice de fila '" + indiceFila + "' no es válido.");
            }
        }
    
        System.out.print(String.format("%-" + (maxAnchoCelda + 1) + "s", "")); // Espacio para los IDs de las filas
        for (int indiceColumna : columnasASeleccionar) {
            String etiqueta = columnas.get(indiceColumna).getEtiquetaColumna();
            System.out.print(formatearTexto(etiqueta, maxAnchoCelda) + " ");
        }
        System.out.println();
    
        for (int indiceFila : indicesFilas) {
            System.out.print(formatearTexto(indiceFila + "", maxAnchoCelda) + " "); // Imprime el ID de la fila
    
            for (int indiceColumna : columnasASeleccionar) {
                Object valor = columnas.get(indiceColumna).getValor(indiceFila);
                String textoCelda = (valor == null) ? "NA" : valor.toString();
                System.out.print(formatearTexto(textoCelda, maxAnchoCelda) + " ");
            }
            System.out.println();
        }
    }

    public void eliminarTodosNAs() {
        for (Columna<?> columna : columnas) {
            for (int i = 0; i < columna.getCeldas().size(); i++) {
                Object valor = columna.getValor(i);
    
                if (valor == null || valor.equals("NA") || valor.equals("NAN") || valor.equals("null")) {
                    try {
                        if (columna.getTipoDato() == String.class) {
                            columna.setValor(i, ""); // Para String, reemplaza con vacío
                        } else if (columna.getTipoDato() == Integer.class) {
                            columna.setValor(i, 0); // Para Integer, reemplaza con 0
                        } else if (columna.getTipoDato() == Boolean.class) {
                            columna.setValor(i, false); // Para Boolean, reemplaza con false
                        } else if (columna.getTipoDato() == Double.class) {
                            columna.setValor(i, 0.0); // Para Double, reemplaza con 0.0
                        }
                    } catch (TipoIncompatible e) {
                        System.err.println("No se pudo establecer el valor vacío en la columna " + columna.getEtiquetaColumna() + ": " + e.getMessage());
                    }
                }
            }
        }
    }
    
    
    
    
    public void head(int n) {
        mostrar(n, getCantidadColumnas(), 10, 0);
    }

    public void tail(int n) {
        int totalFilas = getCantidadFilas();
        int filasDesde = Math.max(0, totalFilas - n);
        int filasMostrar = Math.min(n, totalFilas);   
        
        System.out.println("Mostrando las últimas " + filasMostrar + " filas:");
        
        mostrar(filasMostrar, getCantidadColumnas(), 10, filasDesde);
    }

    public Tabla filtrar(String query) throws EtiquetaInvalida, TipoIncompatible {
        List<Object[]> filasSeleccionadas = new ArrayList<>();
    
        Object[] etiquetas = new Object[getCantidadColumnas()];
        for (int j = 0; j < getCantidadColumnas(); j++) {
            etiquetas[j] = columnas.get(j).getEtiquetaColumna();
        }
    
        filasSeleccionadas.add(etiquetas);
    
        for (int i = 0; i < getCantidadFilas(); i++) {
            if (evaluarFila(i, query)) {
                Object[] valorFila = new Object[getCantidadColumnas()];
                for (int j = 0; j < getCantidadColumnas(); j++) {
                    valorFila[j] = columnas.get(j).getValor(i);
                }
                filasSeleccionadas.add(valorFila);
            }
        }
    
        if (filasSeleccionadas.size() == 1) {
            throw new IllegalArgumentException("No se encontraron filas que coincidan con los criterios de filtrado.");
        }
    
        return new Tabla(filasSeleccionadas.toArray(new Object[0][]));
    }
    
    private boolean evaluarFila(int fila, String query) throws EtiquetaInvalida {
        List<String> condiciones = new ArrayList<>();
        List<String> operadoresLogicos = new ArrayList<>();
    
        String[] partesQuery = query.split(" ");
        for (int i = 0; i < partesQuery.length; i++) {
            String parte = partesQuery[i].trim();
            if (parte.equalsIgnoreCase("and") || parte.equalsIgnoreCase("or")) {
                operadoresLogicos.add(parte.toLowerCase());
            } else if (parte.equalsIgnoreCase("not")) {
                if (i + 3 < partesQuery.length) {
                    // NOT debe aplicarse a la siguiente condición, capturándola y aplicando negación
                    condiciones.add("not " + partesQuery[i + 1] + " " + partesQuery[i + 2] + " " + partesQuery[i + 3]);
                    i += 3;
                } else {
                    throw new IllegalArgumentException("La condición está incompleta después de NOT: " + parte);
                }
            } else {
                if (i + 2 < partesQuery.length) {
                    condiciones.add(partesQuery[i] + " " + partesQuery[i + 1] + " " + partesQuery[i + 2]);
                    i += 2;
                } else {
                    throw new IllegalArgumentException("La condición está incompleta: " + parte);
                }
            }
        }
    
        boolean resultado = evaluarCondicionLogica(fila, condiciones.get(0));
    
        for (int i = 1; i < condiciones.size(); i++) {
            String operador = operadoresLogicos.get(i - 1);
            boolean siguienteCondicion = evaluarCondicionLogica(fila, condiciones.get(i));
    
            switch (operador) {
                case "and":
                    resultado = resultado && siguienteCondicion;
                    break;
                case "or":
                    resultado = resultado || siguienteCondicion;
                    break;
                default:
                    throw new IllegalArgumentException("Operador lógico no válido: " + operador);
            }
        }
    
        return resultado;
    }
    
    private boolean evaluarCondicionLogica(int fila, String condicion) throws EtiquetaInvalida {
        boolean esNegada = condicion.startsWith("not ");
        if (esNegada) {
            condicion = condicion.substring(4); // Quitar "not " para evaluar la condición sin negación
        }
    
        boolean resultadoCondicion = evaluarCondicionSimple(fila, condicion);
        return esNegada ? !resultadoCondicion : resultadoCondicion;
    }
    
    private boolean evaluarCondicionSimple(int fila, String condicion) throws EtiquetaInvalida {
        String[] partes = condicion.split(" ");
        if (partes.length != 3) {
            throw new IllegalArgumentException("Condición no válida: " + condicion);
        }
    
        String columna = partes[0];
        String operador = partes[1];
        String valorStr = partes[2];
    
        Object valorCelda = getCelda(fila, columna);
        return evaluarCondicion(valorCelda, operador, valorStr);
    }
    
    private boolean evaluarCondicion(Object valorCelda, String operador, String valorComparacion) {
        if (valorCelda instanceof Number) {
            double valor = Double.parseDouble(valorCelda.toString());
            double valorComp = Double.parseDouble(valorComparacion);
            switch (operador) {
                case ">": return valor > valorComp;
                case "<": return valor < valorComp;
                case "=": return valor == valorComp;
                case "!=": return valor != valorComp;
                case ">=": return valor >= valorComp;
                case "<=": return valor <= valorComp;
                default: throw new IllegalArgumentException("Operador no soportado: " + operador);
            }
        } else if (valorCelda instanceof Boolean) {
            boolean valor = Boolean.parseBoolean(valorCelda.toString());
            boolean valorComp = Boolean.parseBoolean(valorComparacion);
            return valor == valorComp;
        } else if (valorCelda instanceof String) {
            switch (operador) {
                case "=": return valorCelda.equals(valorComparacion);
                case "!=": return !valorCelda.equals(valorComparacion);
                default: throw new IllegalArgumentException("Operador no soportado para String: " + operador);
            }
        }
        return false;
    }
    

    public Tabla hacerCopiaProfunda(Tabla tabla) throws TipoIncompatible, EtiquetaInvalida {
        int totalFilas = tabla.getCantidadFilas();
        Object[][] matrizInicial = new Object[totalFilas + 1][tabla.getCantidadColumnas()]; // +1 para la fila de etiquetas
        
        for (int i = 0; i < tabla.getCantidadColumnas(); i++) {
            matrizInicial[0][i] = tabla.columnas.get(i).getEtiquetaColumna();
        }
    
        for (int i = 0; i < totalFilas; i++) {
            for (int j = 0; j < tabla.getCantidadColumnas(); j++) {
                matrizInicial[i + 1][j] = tabla.columnas.get(j).getValor(i); // +1 para dejar espacio a la fila de etiquetas
            }
        }
    
        return new Tabla(matrizInicial);
    }

    public Tabla concatenar(Tabla otraTabla) throws TipoIncompatible, EtiquetaInvalida, DimensionesIncompatibles {
        if (this.getCantidadColumnas() != otraTabla.getCantidadColumnas()) {
            throw new DimensionesIncompatibles("Las tablas no tienen la misma cantidad de columnas.");
        }

        for (int i = 0; i < this.getCantidadColumnas(); i++) {
            Object etiquetaThis = this.columnas.get(i).getEtiquetaColumna();
            Object etiquetaOtra = otraTabla.columnas.get(i).getEtiquetaColumna();
            Class<?> tipoThis = this.columnas.get(i).getTipoDato();
            Class<?> tipoOtra = otraTabla.columnas.get(i).getTipoDato();
    
            if (!etiquetaThis.equals(etiquetaOtra)) {
                throw new EtiquetaInvalida("Las etiquetas de las columnas no coinciden: " + etiquetaThis + " vs " + etiquetaOtra);
            }
    
            if (!tipoThis.equals(tipoOtra)) {
                throw new TipoIncompatible("Los tipos de datos de las columnas no coinciden en la columna " + etiquetaThis);
            }
        }
    
        int totalFilas = this.getCantidadFilas() + otraTabla.getCantidadFilas();
        Object[][] matrizInicial = new Object[totalFilas + 1][this.getCantidadColumnas()]; // +1 para la fila de etiquetas
    
        for (int j = 0; j < this.getCantidadColumnas(); j++) {
            matrizInicial[0][j] = this.columnas.get(j).getEtiquetaColumna();
        }
    
        for (int i = 0; i < this.getCantidadFilas(); i++) {
            for (int j = 0; j < this.getCantidadColumnas(); j++) {
                matrizInicial[i + 1][j] = this.columnas.get(j).getValor(i); // +1 para compensar la fila de etiquetas
            }
        }
    
        for (int i = 0; i < otraTabla.getCantidadFilas(); i++) {
            for (int j = 0; j < otraTabla.getCantidadColumnas(); j++) {
                matrizInicial[this.getCantidadFilas() + i + 1][j] = otraTabla.columnas.get(j).getValor(i);
            }
        }
     
        return new Tabla(matrizInicial);
    }

    @SuppressWarnings("unchecked")
    public Tabla ordenar(Tabla tabla, List<String> etiquetasColumnas, List<Boolean> ordenAscendente) throws EtiquetaInvalida, TipoIncompatible {
        // Validar que las etiquetas existen en la tabla
        for (String etiqueta : etiquetasColumnas) {
            if (!tabla.getEtiquetasColumnas().contains(etiqueta)) {
                throw new EtiquetaInvalida("La etiqueta de la columna '" + etiqueta + "' no existe.");
            }
        }
    
        // Crear una lista de índices de filas a ordenar
        List<Integer> indicesFilas = new ArrayList<>();
        for (int i = 0; i < tabla.getCantidadFilas(); i++) {
            indicesFilas.add(i);
        }
    
        // Ordenar la lista de índices de filas usando un comparador
        indicesFilas.sort((fila1, fila2) -> {
            for (int i = 0; i < etiquetasColumnas.size(); i++) {
                String etiqueta = etiquetasColumnas.get(i);
                boolean ascendente = ordenAscendente.get(i);
    
                Object valor1 = null;
                Object valor2 = null;
    
                try {
                    // Obtener los valores de las filas usando las etiquetas de columna
                    valor1 = tabla.getCelda(fila1, etiqueta);
                    valor2 = tabla.getCelda(fila2, etiqueta);
                } catch (EtiquetaInvalida e) {
                    System.err.println("Error: " + e.getMessage());
                    return 0; // Terminar comparación si hay un error en la etiqueta
                }
    
                // Manejar los valores null
                if (valor1 == null && valor2 == null) continue;
                if (valor1 == null) return ascendente ? -1 : 1;
                if (valor2 == null) return ascendente ? 1 : -1;
    
                // Comparar los valores según el tipo
                int comparacion = 0;
                try {
                    if (!valor1.getClass().equals(valor2.getClass())) {
                        throw new TipoIncompatible("Los valores en la columna '" + etiqueta + "' tienen tipos incompatibles.");
                    }
    
                    // Comparación basada en el tipo de objeto
                    if (valor1 instanceof Comparable) {
                        comparacion = ((Comparable<Object>) valor1).compareTo(valor2);
                    } else {
                        throw new TipoIncompatible("Los valores en la columna '" + etiqueta + "' no son comparables.");
                    }
                } catch (TipoIncompatible e) {
                    System.err.println("Error: " + e.getMessage());
                    return 0; // Terminar comparación si hay un error de tipo
                }
    
                // Ajustar el orden según el valor de `ascendente`
                if (comparacion != 0) {
                    return ascendente ? comparacion : -comparacion;
                }
            }
            return 0;
        });
    
        // Crear una nueva matriz con los valores ordenados
        Object[][] datosOrdenados = new Object[tabla.getCantidadFilas() + 1][tabla.getCantidadColumnas()]; // +1 para las etiquetas
        for (int i = 0; i < tabla.getCantidadColumnas(); i++) {
            datosOrdenados[0][i] = tabla.getEtiquetasColumnas().get(i);
        }
    
        // Rellenar las filas ordenadas en la matriz
        for (int i = 0; i < tabla.getCantidadFilas(); i++) {
            for (int j = 0; j < tabla.getCantidadColumnas(); j++) {
                Object etiquetaColumna = tabla.getEtiquetasColumnas().get(j); // Obtener la etiqueta de la columna
                datosOrdenados[i + 1][j] = tabla.getCelda(indicesFilas.get(i), etiquetaColumna); // Asignar valor en la matriz
            }
        }
    
        Tabla tablaOrdenada = new Tabla(datosOrdenados);
    
        return tablaOrdenada;
    }

    @Override
    public void muestrear(double porcentaje, int maxAnchoCelda) {
        if (porcentaje < 0 || porcentaje > 100) {
            throw new IllegalArgumentException("El porcentaje debe estar entre 0 y 100.");
        }
    
        int totalFilas = getCantidadFilas();
        int totalColumnas = getCantidadColumnas();
    
        int filasAMostrar = (int) Math.ceil((porcentaje / 100) * totalFilas);
        
        if (filasAMostrar == 0) {
            System.out.println("No hay filas suficientes para muestrear.");
            return;
        }
    
        Random random = new Random();
        List<Integer> indicesAleatorios = new ArrayList<>();
        
        while (indicesAleatorios.size() < filasAMostrar) {
            int indiceAleatorio = random.nextInt(totalFilas);
            if (!indicesAleatorios.contains(indiceAleatorio)) {
                indicesAleatorios.add(indiceAleatorio);
            }
        }
    
        System.out.print(String.format("%-" + (maxAnchoCelda + 1) + "s", "")); // Espacio para las etiquetas de filas
        for (int i = 0; i < totalColumnas; i++) {
            String etiqueta = columnas.get(i).getEtiquetaColumna();
            System.out.print(formatearTexto(etiqueta, maxAnchoCelda) + " ");
        }
        System.out.println();
    
        for (int indiceFila : indicesAleatorios) {
            System.out.print(formatearTexto(indiceFila + "", maxAnchoCelda) + " "); // Imprime el ID de la fila
    
            for (int j = 0; j < totalColumnas; j++) {
                Object valor = columnas.get(j).getValor(indiceFila);
                String textoCelda = (valor == null) ? "NA" : valor.toString();
                System.out.print(formatearTexto(textoCelda, maxAnchoCelda) + " ");
            }
            System.out.println();
        }

    }
    public enum Operacion {
        SUMA, MAXIMO, MINIMO, CUENTA, MEDIA, VARIANZA, DESVIO
    }

    public Tabla agruparPor(List<String> columnasAgrupamiento, Operacion operacion) throws TipoIncompatible, EtiquetaInvalida {
        if (columnasAgrupamiento == null || columnasAgrupamiento.isEmpty()) {
            throw new IllegalArgumentException("Las columnas de agrupamiento no pueden estar vacías.");
        }
    
        // Mapa para almacenar los grupos de filas, donde la clave es la combinación de valores de agrupamiento
        Map<String, List<List<Object>>> grupos = new HashMap<>();
    
        // Agrupar filas según las columnas indicadas
        for (int i = 0; i < getCantidadFilas(); i++) {
            List<Object> fila = getFila(i); 
            String claveGrupo = generarClaveGrupo(fila, columnasAgrupamiento);
    
            grupos.computeIfAbsent(claveGrupo, k -> new ArrayList<>()).add(fila);
        }
    
        List<Object[]> datosResultado = new ArrayList<>();
        List<String> columnasNumericas = getColumnasNumericas(columnasAgrupamiento);

        Object[] filaEtiquetas = new Object[columnasNumericas.size() + 1];
        filaEtiquetas[0] = "Grupo";
        for (int i = 0; i < columnasNumericas.size(); i++) {
            filaEtiquetas[i + 1] = columnasNumericas.get(i);
        }
        datosResultado.add(filaEtiquetas);
    
        for (Map.Entry<String, List<List<Object>>> entradaGrupo : grupos.entrySet()) {
            String claveGrupo = entradaGrupo.getKey();
            List<List<Object>> filasGrupo = entradaGrupo.getValue();
    
            Object[] filaResultado = new Object[columnasNumericas.size() + 1];
            filaResultado[0] = claveGrupo;
    
            for (int i = 0; i < columnasNumericas.size(); i++) {
                String columna = columnasNumericas.get(i);
                filaResultado[i + 1] = calcularOperacion(filasGrupo, columna, operacion);
            }
            datosResultado.add(filaResultado);
        }
    
        Object[][] matrizDatos = datosResultado.toArray(new Object[0][]);
    
        
        Tabla nuevaTabla = new Tabla(matrizDatos);
        return nuevaTabla;
    }
    
    // Método para generar la clave de grupo como combinación de valores de las columnas de agrupamiento
    private String generarClaveGrupo(List<Object> fila, List<String> columnasAgrupamiento) {
        List<String> valores = new ArrayList<>();
        for (String columna : columnasAgrupamiento) {
            int indice = getIndiceColumna(columna);
            valores.add(fila.get(indice).toString());
        }
        return String.join(", ", valores);
    }
    
    private List<String> getColumnasNumericas(List<String> columnasAgrupamiento) {
        return columnas.stream()
            .filter(columna -> !columnasAgrupamiento.contains(columna.getEtiquetaColumna()) && 
                                Number.class.isAssignableFrom(columna.getTipoDato()))
            .map(Columna::getEtiquetaColumna)
            .collect(Collectors.toList());
    }
    
    private Object calcularOperacion(List<List<Object>> filasGrupo, String columna, Operacion operacion) throws TipoIncompatible {
        int indiceColumna = getIndiceColumna(columna);
        List<Double> valores = filasGrupo.stream()
            .map(fila -> fila.get(indiceColumna))
            .filter(Objects::nonNull)
            .map(valor -> ((Number) valor).doubleValue())
            .collect(Collectors.toList());
    
        switch (operacion) {
            case SUMA:
                return valores.stream().mapToDouble(Double::doubleValue).sum();
            case MAXIMO:
                return valores.stream().mapToDouble(Double::doubleValue).max().orElse(Double.NaN);
            case MINIMO:
                return valores.stream().mapToDouble(Double::doubleValue).min().orElse(Double.NaN);
            case CUENTA:
                return valores.size();
            case MEDIA:
                return valores.stream().mapToDouble(Double::doubleValue).average().orElse(Double.NaN);
            case VARIANZA:
                double media = valores.stream().mapToDouble(Double::doubleValue).average().orElse(0);
                return valores.stream().mapToDouble(v -> Math.pow(v - media, 2)).sum() / (valores.size() - 1);
            case DESVIO:
                double varianza = (double) calcularOperacion(filasGrupo, columna, Operacion.VARIANZA);
                return Math.sqrt(varianza);
            default:
                throw new TipoIncompatible("Operación no soportada: " + operacion);
        }
    }
    
    private int getIndiceColumna(String nombreColumna) {
        for (int i = 0; i < columnas.size(); i++) {
            if (columnas.get(i).getEtiquetaColumna().equals(nombreColumna)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Columna no encontrada: " + nombreColumna);
    }

    public void guardar(String path) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            List<Object> etiquetas = getEtiquetasColumnas();
            for (int i = 0; i < etiquetas.size(); i++) {
                writer.write(etiquetas.get(i).toString());
                if (i < etiquetas.size() - 1) writer.write(",");
            }
            writer.newLine();
    
            for (int i = 0; i < getCantidadFilas(); i++) {
                for (int j = 0; j < etiquetas.size(); j++) {
                    String etiqueta = etiquetas.get(j).toString();
                    try {
                        Object valor = getCelda(i, etiqueta);
                        writer.write(valor != null ? valor.toString() : "");
                    } catch (EtiquetaInvalida e) {
                        System.out.println("Error: La etiqueta '" + etiqueta + "' no existe en la tabla.");
                        writer.write("");
                    }
                    if (j < etiquetas.size() - 1) writer.write(",");
                }
                writer.newLine();
            }
            System.out.println("Tabla guardada en " + path);
        } catch (IOException e) {
            System.out.println("Error al guardar el archivo: " + e.getMessage());
        }
    
    }
}



