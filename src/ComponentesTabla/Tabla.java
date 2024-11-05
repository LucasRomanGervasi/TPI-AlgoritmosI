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

    // Constructor desde archivo CSV
    public Tabla(String rutaArchivoCSV, boolean headers) {
        try {
            crearDesdeArchivoCSV(rutaArchivoCSV, headers);
        } catch (IOException e) {
            System.err.println("Error de IO al leer el archivo CSV: " + e.getMessage());
        } catch (TipoIncompatible | EtiquetaInvalida e) {
            System.err.println("Error al crear la tabla desde el archivo CSV: " + e.getMessage());
        }
    }

    // Constructor desde secuencia lineal
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
    
        // Agregar filas a la tabla
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
    
            // Ignorar null, NA y NAN
            if (valor == null || valor.equals("NA") || valor.equals("NAN")) continue;
    
            String valorStr = valor.toString();
            
            // Verificar Integer
            if (esEntero) {
                try {
                    Integer.parseInt(valorStr);
                } catch (NumberFormatException e) {
                    esEntero = false;
                }
            }
    
            // Verificar Double
            if (esDouble) {
                try {
                    Double.parseDouble(valorStr);
                } catch (NumberFormatException e) {
                    esDouble = false;
                }
            }
    
            // Verificar Boolean
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
    
    
    
    private void crearDesdeArchivoCSV(String rutaArchivoCSV, boolean headers) throws IOException, TipoIncompatible, EtiquetaInvalida {
        if (rutaArchivoCSV == null || rutaArchivoCSV.isEmpty()) {
            throw new IllegalArgumentException("La ruta del archivo CSV no puede estar vacía.");
        }
    
        columnas = new ArrayList<>();  // Inicializar la lista de columnas
        indicesColumnas = new HashMap<>(); // Inicializar el mapa de índices
    
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
    
        // Asumir la primera fila como las etiquetas de las columnas
        List<String> etiquetas = headers ? datos.get(0) : generarEtiquetas(datos.get(0).size());
        int numColumnas = etiquetas.size();
    
        // Asignar tipo de dato según el primer valor no nulo de cada columna
        for (int col = 0; col < numColumnas; col++) {
            String etiqueta = etiquetas.get(col);
            Class<?> tipoDato = null;
            boolean tipoUniforme = true;
    
            // Determinar el tipo de dato usando el primer valor no nulo de la columna
            for (int fila = 1; fila < datos.size(); fila++) {
                String valor = datos.get(fila).get(col);
                if (valor != null && !valor.isEmpty()) {
                    Class<?> tipoActual = inferirTipoDato(valor);
                    if (tipoDato == null) {
                        tipoDato = tipoActual; // Asignar el primer tipo encontrado
                    } else if (!tipoDato.equals(tipoActual)) {
                        tipoDato = String.class; // Si hay una discrepancia, definir tipo como String
                        tipoUniforme = false; // Marcar como no uniforme
                    }
                }
            }
    
            agregarColumna(etiqueta, tipoDato);
        }
    
        // Validar los tipos de datos en la columna
        for (int fila = 1; fila < datos.size(); fila++) {
            agregarFila();
            for (int col = 0; col < numColumnas; col++) {
                String valor = datos.get(fila).get(col);
                Object valorConvertido = (valor == null || valor.isEmpty()) ? null : convertirValor(valor, columnas.get(col).getTipoDato());
                setValorCelda(fila - 1, etiquetas.get(col), valorConvertido);
            }
        }
    }
    
    // Método auxiliar para generar etiquetas en caso de no tener encabezados
    private List<String> generarEtiquetas(int numColumnas) {
        List<String> etiquetas = new ArrayList<>();
        for (int i = 1; i <= numColumnas; i++) {
            etiquetas.add("Columna" + i);
        }
        return etiquetas;
    }

    // Método auxiliar para inferir el tipo de dato de un valor en formato String
    private Class<?> inferirTipoDato(String valor) {
        try {
            Integer.parseInt(valor);
            return Integer.class;
        } catch (NumberFormatException e1) {
            if (valor.equalsIgnoreCase("true") || valor.equalsIgnoreCase("false")) {
                return Boolean.class;
            } else {
                return String.class;
            }
        }
    }
    
    // Método auxiliar para convertir un String al tipo de dato correspondiente
    private Object convertirValor(String valor, Class<?> tipo) throws TipoIncompatible {
        if (valor == null || valor.isEmpty()) {
            return null; // o algún valor por defecto si lo necesitas
        }
        if (tipo == Integer.class) {
            return Integer.parseInt(valor);
        } else if (tipo == Boolean.class) {
            return Boolean.parseBoolean(valor);
        } else if (tipo == String.class) {
            return valor;
        } else {
            throw new TipoIncompatible("Tipo de dato no compatible para la conversión: " + tipo.getSimpleName());
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
            // Asignar un tipo por defecto de String si el valor es null
            Class<?> tipoColumna = (valor != null) ? valor.getClass() : String.class;
            agregarColumna("Columna" + (i + 1), tipoColumna);
        }
    
        // Agregar filas verificando tipos
        for (Object filaObj : secuenciaLineal) {
            if (filaObj instanceof List) {
                List<?> fila = (List<?>) filaObj;
                agregarFila();
    
                for (int i = 0; i < fila.size(); i++) {
                    String etiquetaColumna = "Columna" + (i + 1);
                    Object valor = fila.get(i);
                    Class<?> tipoEsperado = columnas.get(i).getTipoDato();
    
                    // Permitir null, NA y NAN
                    if (valor == null || valor.equals("NA") || valor.equals("NAN")) {
                        setValorCelda(getCantidadFilas() - 1, etiquetaColumna, null);
                    } else {
                        // Comprobar si el valor es del tipo esperado
                        if (!tipoEsperado.isInstance(valor)) {
                            // Intentar convertir el valor al tipo esperado
                            valor = convertirValor(valor.toString(), tipoEsperado);
                        }
    
                        setValorCelda(getCantidadFilas() - 1, etiquetaColumna, valor);
                    }
                }
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

    public Object getCelda(int idFila, String etiquetaColumna) throws EtiquetaInvalida {
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
        //System.out.println(totalFilas + " filas x " + totalColumnas + " columnas");
        int filasMostrar = Math.min(maxFilas, totalFilas - filaInicio); // Ajusta filas a mostrar desde filaInicio
        int columnasMostrar = Math.min(maxColumnas, totalColumnas); // Ajusta columnas a mostrar
        //System.out.println(filasMostrar + " filas x " + columnasMostrar + " columnas");
        // Imprimir etiquetas de columnas
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
    private String formatearTexto(String texto, int maxAncho) {
        // Si el texto es más largo que el máximo ancho permitido, lo truncamos
        if (texto.length() > maxAncho) {
            return texto.substring(0, maxAncho);
        }
    
        // Si el texto es más corto, lo rellenamos con espacios
        return String.format("%-" + maxAncho + "s", texto);
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
            nuevaColumna.agregarValor(null); // Asigna un valor nulo por defecto
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
            columna.agregarCelda(null); // Primero agrega al final
        }
        
        // Se asegura que las celdas se manejen correctamente
        // Asegúrate de que las columnas tengan la lógica para manejar el tamaño correcto
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
        int totalFilas = getCantidadFilas();
        for (int indiceFila : indicesFilas) {
            if (indiceFila < 0 || indiceFila >= totalFilas) {
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
        // Recorrer cada columna y actualizar los valores que son NA o equivalentes
        for (Columna<?> columna : columnas) {
            for (int i = 0; i < columna.getCeldas().size(); i++) {
                Object valor = columna.getValor(i);
    
                // Imprimir el valor para depuración
                //System.out.println("Valor en la fila " + i + " de la columna " + columna.getEtiquetaColumna() + ": " + valor);
    
                // Verificar si el valor es null, "NA", "NAN", o "null"
                if (valor == null || 
                    valor.equals("NA") || 
                    valor.equals("NAN") || 
                    valor.equals("null")) {
    
                    // Intentar establecer el valor según el tipo de la columna
                    try {
                        if (columna.getTipoDato() == String.class) {
                            columna.setValor(i, ""); // Establece el valor como cadena vacía
                        } else if (columna.getTipoDato() == Integer.class) {
                            columna.setValor(i, 0); // Establece el valor como 0
                        } else if (columna.getTipoDato() == Boolean.class) {
                            columna.setValor(i, false); // Establece el valor como false
                        } else if (columna.getTipoDato() == Double.class) {
                            columna.setValor(i, 0.0); // Establece el valor como 0.0
                        }
                    } catch (TipoIncompatible e) {
                        // Manejo de la excepción en caso de tipo incompatible
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
        int filasDesde = Math.max(0, totalFilas - n); // Calcula desde dónde mostrar las filas (si n > totalFilas, comienza desde la fila 0)
        int filasMostrar = Math.min(n, totalFilas);   // Asegura que no se muestren más filas que las existentes
        
        System.out.println("Mostrando las últimas " + filasMostrar + " filas:");
        
        mostrar(filasMostrar, getCantidadColumnas(), 10, filasDesde);
    }

    // Método filtrar sin cambios
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
            return operador.equals("=") && valorCelda.equals(valorComparacion);
        }
        return false;
    }


    public Tabla hacerCopiaProfunda(Tabla tabla) throws TipoIncompatible, EtiquetaInvalida {
        // Crear una matriz que incluya espacio para las filas y las etiquetas
        int totalFilas = tabla.getCantidadFilas();
        Object[][] matrizInicial = new Object[totalFilas + 1][tabla.getCantidadColumnas()]; // +1 para la fila de etiquetas
        
        // Copiar las etiquetas de columna en la primera fila
        for (int i = 0; i < tabla.getCantidadColumnas(); i++) {
            matrizInicial[0][i] = tabla.columnas.get(i).getEtiquetaColumna();
        }
    
        // Copiar los datos de las filas de la tabla original
        for (int i = 0; i < totalFilas; i++) {
            for (int j = 0; j < tabla.getCantidadColumnas(); j++) {
                matrizInicial[i + 1][j] = tabla.columnas.get(j).getValor(i); // +1 para dejar espacio a la fila de etiquetas
            }
        }
    
        // Crear la nueva tabla a partir de la matriz con etiquetas y datos copiados
        return new Tabla(matrizInicial);
    }

    public Tabla concatenar(Tabla otraTabla) throws TipoIncompatible, EtiquetaInvalida, DimensionesIncompatibles {
        // Verificar que las dos tablas tienen el mismo número de columnas
        if (this.getCantidadColumnas() != otraTabla.getCantidadColumnas()) {
            throw new DimensionesIncompatibles("Las tablas no tienen la misma cantidad de columnas.");
        }
    
        // Verificar que las etiquetas de las columnas y los tipos coinciden
        for (int i = 0; i < this.getCantidadColumnas(); i++) {
            String etiquetaThis = this.columnas.get(i).getEtiquetaColumna();
            String etiquetaOtra = otraTabla.columnas.get(i).getEtiquetaColumna();
            Class<?> tipoThis = this.columnas.get(i).getTipoDato();
            Class<?> tipoOtra = otraTabla.columnas.get(i).getTipoDato();
    
            if (!etiquetaThis.equals(etiquetaOtra)) {
                throw new EtiquetaInvalida("Las etiquetas de las columnas no coinciden: " + etiquetaThis + " vs " + etiquetaOtra);
            }
    
            if (!tipoThis.equals(tipoOtra)) {
                throw new TipoIncompatible("Los tipos de datos de las columnas no coinciden en la columna " + etiquetaThis);
            }
        }
    
        // Crear una nueva tabla para almacenar la concatenación, usando un formato de matriz
        int totalFilas = this.getCantidadFilas() + otraTabla.getCantidadFilas();
        Object[][] matrizInicial = new Object[totalFilas + 1][this.getCantidadColumnas()]; // +1 para la fila de etiquetas
    
        // Agregar etiquetas de columna en la primera fila
        for (int j = 0; j < this.getCantidadColumnas(); j++) {
            matrizInicial[0][j] = this.columnas.get(j).getEtiquetaColumna();
        }
    
        // Agregar las filas de la primera tabla
        for (int i = 0; i < this.getCantidadFilas(); i++) {
            for (int j = 0; j < this.getCantidadColumnas(); j++) {
                matrizInicial[i + 1][j] = this.columnas.get(j).getValor(i); // +1 para compensar la fila de etiquetas
            }
        }
    
        // Agregar las filas de la segunda tabla
        for (int i = 0; i < otraTabla.getCantidadFilas(); i++) {
            for (int j = 0; j < otraTabla.getCantidadColumnas(); j++) {
                matrizInicial[this.getCantidadFilas() + i + 1][j] = otraTabla.columnas.get(j).getValor(i);
            }
        }
    
        // Crear la nueva tabla concatenada a partir de la matriz completa
        return new Tabla(matrizInicial);
    }

    public Tabla ordenar(Tabla tabla, List<String> etiquetasColumnas, List<Boolean> ordenAscendente) throws EtiquetaInvalida, TipoIncompatible {
        Tabla tablaOrdenada = hacerCopiaProfunda(tabla);
        
        // Validar etiquetas de columnas
        for (String etiqueta : etiquetasColumnas) {
            if (!tablaOrdenada.indicesColumnas.containsKey(etiqueta)) {
                throw new EtiquetaInvalida("La etiqueta de la columna '" + etiqueta + "' no existe.");
            }
        }
        
        // Obtener índices de filas
        List<Integer> indicesFilas = new ArrayList<>();
        for (int i = 0; i < tabla.getCantidadFilas(); i++) {
            indicesFilas.add(i);
        }
        
        // Crear un comparador encadenado para las filas
        indicesFilas.sort((fila1, fila2) -> {
            for (int i = 0; i < etiquetasColumnas.size(); i++) {
                String etiqueta = etiquetasColumnas.get(i);
                boolean ascendente = ordenAscendente.get(i);
    
                try {
                    Columna<?> columna = tablaOrdenada.getColumna(etiqueta);
                    Object valor1 = columna.getValor(fila1);
                    Object valor2 = columna.getValor(fila2);
    
                    // Depuración adicional para revisar el tipo exacto
                    System.out.println("Tipo columna '" + etiqueta + "': " + columna.getTipoDato());
                    System.out.println("Comparando valores en columna '" + etiqueta + "': valor1=" + valor1 + " (tipo " + (valor1 != null ? valor1.getClass().getName() : "null") + "), valor2=" + valor2 + " (tipo " + (valor2 != null ? valor2.getClass().getName() : "null") + ")");
                    
                    // Validar si ambos valores son null
                    if (valor1 == null && valor2 == null) continue;
                    if (valor1 == null) return ascendente ? -1 : 1;
                    if (valor2 == null) return ascendente ? 1 : -1;
    
                    // Comparación basada en tipos específicos
                    if (valor1 instanceof Integer && valor2 instanceof Integer) {
                        int comparacion = ((Integer) valor1).compareTo((Integer) valor2);
                        return ascendente ? comparacion : -comparacion;
                    } else if (valor1 instanceof Double && valor2 instanceof Double) {
                        int comparacion = ((Double) valor1).compareTo((Double) valor2);
                        return ascendente ? comparacion : -comparacion;
                    } else if (valor1.getClass().equals(valor2.getClass())) {
                        // Si los tipos son iguales, lanzar error
                        throw new TipoIncompatible("Los valores en la columna '" + etiqueta + "' tienen tipos incompatibles.");
                    } else {
                        // Si los valores no son Integer ni Double, lanzar un error de tipo
                        System.out.println("Error de tipo: Los valores de la columna '" + etiqueta + "' no son del tipo esperado (Integer o Double).");
                        throw new TipoIncompatible("El tipo de dato en la columna '" + etiqueta + "' no es compatible con la comparación.");
                    }
                } catch (EtiquetaInvalida e) {
                    System.out.println("Error de etiqueta: " + e.getMessage());
                    return 0;  // Mantener el orden si hay error en la comparación
                } catch (TipoIncompatible e) {
                    System.out.println("Error de tipo incompatible: " + e.getMessage());
                    return 0;  // Mantener el orden si hay un error de tipo
                }
            }
            return 0; // Si todos los valores son iguales, mantener el orden actual
        });
    
        // Crear una nueva tabla ordenada aplicando el orden de indicesFilas
        Tabla nuevaTablaOrdenada = hacerCopiaEstructura(tablaOrdenada);
        for (int i = 0; i < indicesFilas.size(); i++) {
            int filaOriginalIndex = indicesFilas.get(i);
            for (Object etiqueta : tablaOrdenada.getEtiquetasColumnas()) { // Itera sobre etiquetas
                Columna<?> columna = tablaOrdenada.getColumna(etiqueta); // Obtener columna por etiqueta
                Object valor = columna.getValor(filaOriginalIndex);
                nuevaTablaOrdenada.getColumna(etiqueta).setValor(i, valor);  // Copiar celda por celda
            }
        }
    
        return nuevaTablaOrdenada;
    }
    
    private Tabla hacerCopiaEstructura(Tabla tablaOriginal) {
        Object[][] estructuraInicial = new Object[tablaOriginal.getCantidadFilas()][tablaOriginal.getCantidadColumnas()];
        List<Object> etiquetasColumnas = tablaOriginal.getEtiquetasColumnas(); // Asumiendo que existe un método que retorna las etiquetas
        for (int i = 0; i < etiquetasColumnas.size(); i++) {
            estructuraInicial[0][i] = etiquetasColumnas.get(i);
        }
        return new Tabla(estructuraInicial);
    }
    
    
    
    
    
    
    

    @Override
    public void muestrear(double porcentaje, int maxAnchoCelda) {
        if (porcentaje < 0 || porcentaje > 100) {
            throw new IllegalArgumentException("El porcentaje debe estar entre 0 y 100.");
        }
    
        int totalFilas = getCantidadFilas();
        int totalColumnas = getCantidadColumnas();
    
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

    public Tabla agregarPor(List<String> columnasAgrupamiento, Operacion operacion) throws TipoIncompatible, EtiquetaInvalida {
        if (columnasAgrupamiento == null || columnasAgrupamiento.isEmpty()) {
            throw new IllegalArgumentException("Las columnas de agrupamiento no pueden estar vacías.");
        }
    
        // Mapa para almacenar los grupos de filas, donde la clave es la combinación de valores de agrupamiento
        Map<String, List<List<Object>>> grupos = new HashMap<>();
    
        // Agrupar filas según las columnas indicadas
        for (int i = 0; i < getCantidadFilas(); i++) {
            List<Object> fila = getFila(i);  // Usamos List<Object> en lugar de Map<String, Object>
            String claveGrupo = generarClaveGrupo(fila, columnasAgrupamiento);
    
            // Agregar la fila al grupo correspondiente
            grupos.computeIfAbsent(claveGrupo, k -> new ArrayList<>()).add(fila);
        }
    
        // Crear la estructura de la tabla de resultados
        List<Object[]> datosResultado = new ArrayList<>();
        List<String> columnasNumericas = getColumnasNumericas(columnasAgrupamiento);
    
        // Generar la fila de etiquetas de columnas
        Object[] filaEtiquetas = new Object[columnasNumericas.size() + 1];
        filaEtiquetas[0] = "Grupo";
        for (int i = 0; i < columnasNumericas.size(); i++) {
            filaEtiquetas[i + 1] = columnasNumericas.get(i);
        }
        datosResultado.add(filaEtiquetas);
    
        // Generar resultados para cada grupo
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
    
        // Convertir datosResultado a una matriz Object[][]
        Object[][] matrizDatos = datosResultado.toArray(new Object[0][]);
    
        // Crear una nueva instancia de Tabla y llamar a crearDesdeMatriz
        
        Tabla nuevaTabla = new Tabla(matrizDatos);
        return nuevaTabla;
    }
    
    // Método para generar la clave de grupo como combinación de valores de las columnas de agrupamiento
    private String generarClaveGrupo(List<Object> fila, List<String> columnasAgrupamiento) {
        List<String> valores = new ArrayList<>();
        for (String columna : columnasAgrupamiento) {
            int indice = getIndiceColumna(columna);  // Método para obtener el índice de la columna
            valores.add(fila.get(indice).toString());
        }
        return String.join(", ", valores);
    }
    
    // Obtener columnas numéricas que no están en las de agrupamiento
    private List<String> getColumnasNumericas(List<String> columnasAgrupamiento) {
        return columnas.stream()
            .filter(columna -> !columnasAgrupamiento.contains(columna.getEtiquetaColumna()) && 
                                Number.class.isAssignableFrom(columna.getTipoDato()))
            .map(Columna::getEtiquetaColumna)
            .collect(Collectors.toList());
    }
    
    // Calcular la operación de agregación sobre la columna de un grupo
    private Object calcularOperacion(List<List<Object>> filasGrupo, String columna, Operacion operacion) throws TipoIncompatible {
        int indiceColumna = getIndiceColumna(columna);  // Obtener índice de la columna
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
    
    // Método auxiliar para obtener el índice de una columna dado su nombre
    private int getIndiceColumna(String nombreColumna) {
        for (int i = 0; i < columnas.size(); i++) {
            if (columnas.get(i).getEtiquetaColumna().equals(nombreColumna)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Columna no encontrada: " + nombreColumna);
    }
    
}



