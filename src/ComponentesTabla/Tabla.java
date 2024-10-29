package ComponentesTabla;
import Excepciones.EtiquetaInvalida;
import Excepciones.TipoIncompatible;
import java.io.*;
import java.util.*;

public class Tabla {
    private List<Columna<?>> columnas;
    private Map<String, Integer> indicesColumnas;

    // Constructor desde matriz
    public Tabla(Object[][] matriz) {
        try {
            crearDesdeMatriz(matriz);
        } catch (TipoIncompatible | EtiquetaInvalida e) {
            System.err.println("Error al crear la tabla desde la matriz: " + e.getMessage());
        }
    }

    // Constructor desde archivo CSV
    public Tabla(String rutaArchivoCSV) {
        try {
            crearDesdeArchivoCSV(rutaArchivoCSV);
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
            // Limpia cualquier columna y fila parcial cargada para evitar una tabla incompleta
            columnas.clear();
            indicesColumnas.clear();
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
            if (!(matriz[0][j] instanceof String)) {
                throw new EtiquetaInvalida("La etiqueta de la columna debe ser un String.");
            }
            
            String etiqueta = matriz[0][j].toString();
            Class<?> tipoDato = String.class; // Asignar String por defecto
            
            // Determinar el tipo de dato de la columna basándonos en los datos (ignorar valores nulos o NA/NAN)
            for (int i = 1; i < matriz.length; i++) {
                if (matriz[i].length > j) {
                    Object valor = matriz[i][j];
                    if (valor != null && !valor.equals("NA") && !valor.equals("NAN")) {
                        tipoDato = determinarTipoDato(valor); // Determinar el tipo si el valor es válido
                        break;
                    }
                }
            }
            
            agregarColumna(etiqueta, tipoDato);
            indicesColumnas.put(etiqueta, columnas.size() - 1);
        }
        
        // Agregar filas a la tabla
        for (int i = 1; i < matriz.length; i++) {
            agregarFila();
            for (int j = 0; j < matriz[i].length; j++) {
                if (j < columnas.size()) {
                    Object valor = matriz[i][j];
                    
                    if (valor == null || valor.equals(false) || valor.equals("NA") || valor.equals("NAN")) {
                        // Asigna un valor predeterminado según el tipo de la columna
                        Class<?> tipoColumna = columnas.get(j).getTipoDato();
                        
                        // Si la columna es de tipo String, asigna una cadena vacía en lugar de "false"
                        if (tipoColumna == String.class && valor instanceof Boolean && !((Boolean) valor)) {
                            valor = ""; // Asignar cadena vacía para false en columna String
                        } else {
                            valor = obtenerValorPredeterminado(tipoColumna);
                        }
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

    // Método auxiliar para obtener un valor predeterminado según el tipo de la columna
    private Object obtenerValorPredeterminado(Class<?> tipoColumna) {
        if (tipoColumna == Integer.class) {
            return 0;
        } else if (tipoColumna == Boolean.class) {
            return false;
        } else if (tipoColumna == String.class) {
            return "";
        }
        return null;
    }
    
    // Método para convertir valores al tipo de dato correspondiente
    private Object convertirValor(Object valor, Class<?> tipoColumna) throws TipoIncompatible {
        if (tipoColumna == Integer.class) {
            return Integer.parseInt(valor.toString());
        } else if (tipoColumna == Double.class) {
            return Double.parseDouble(valor.toString());
        } else if (tipoColumna == Boolean.class) {
            return Boolean.parseBoolean(valor.toString());
        } else if (tipoColumna == String.class) {
            return valor.toString();
        }
        throw new TipoIncompatible("No se puede convertir el valor a " + tipoColumna.getName());
    }

    private Class<?> determinarTipoDato(Object valor) throws TipoIncompatible {
        if (valor instanceof Integer) {
            return Integer.class;
        } else if (valor instanceof Double) {
            return Double.class;
        } else if (valor instanceof Boolean) {
            return Boolean.class;
        } else if (valor instanceof String) {
            return String.class;
        }
        // Manejar "NA" y "NAN"
        else if (valor.equals("NA") || valor.equals("NAN")) {
            return String.class; // Asignar String para estos casos
        } else {
            throw new TipoIncompatible("Tipo de dato no soportado: " + valor.getClass().getName());
        }
    }
    
    // Método para crear tabla desde archivo CSV
    private void crearDesdeArchivoCSV(String rutaArchivoCSV) throws IOException, TipoIncompatible, EtiquetaInvalida {
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
        List<String> etiquetas = datos.get(0);
        int numColumnas = etiquetas.size();
    
        // Asignar tipo de dato según el primer valor no nulo de cada columna
        for (int col = 0; col < numColumnas; col++) {
            String etiqueta = etiquetas.get(col);
            Class<?> tipoDato = null;
    
            // Determinar el tipo de dato usando el primer valor no nulo de la columna
            for (int fila = 1; fila < datos.size(); fila++) {
                String valor = datos.get(fila).get(col);
                if (valor != null && !valor.isEmpty()) {
                    tipoDato = inferirTipoDato(valor);
                    break;
                }
            }
    
            if (tipoDato == null) {
                throw new TipoIncompatible("No se pudo inferir el tipo de dato de la columna: " + etiqueta);
            }
    
            agregarColumna(etiqueta, tipoDato);
    
            // Validar los tipos de datos en la columna
            for (int fila = 1; fila < datos.size(); fila++) {
                String valor = datos.get(fila).get(col);
                if (valor != null && !valor.isEmpty()) {
                    Class<?> tipoValor = inferirTipoDato(valor);
                    if (!tipoDato.equals(tipoValor)) {
                        throw new TipoIncompatible("Tipo de dato inconsistente en la columna " + etiqueta +
                                ". Se esperaba " + tipoDato.getSimpleName() + " pero se encontró " + tipoValor.getSimpleName() + " en la fila " + (fila + 1));
                    }
                }
            }
        }
    
        // Agregar filas a la tabla
        for (int i = 1; i < datos.size(); i++) {
            agregarFila();
            for (int j = 0; j < numColumnas; j++) {
                String valor = datos.get(i).get(j);
                Object valorConvertido = convertirValor(valor, columnas.get(j).getTipoDato());
                setValorCelda(i - 1, etiquetas.get(j), valorConvertido);
            }
        }
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
    

    // Método para crear tabla desde secuencia lineal
    private void crearDesdeSecuenciaLineal(List<Object> secuenciaLineal) throws TipoIncompatible, EtiquetaInvalida {
        if (secuenciaLineal == null || secuenciaLineal.isEmpty()) {
            throw new IllegalArgumentException("La secuencia lineal no puede estar vacía.");
        }
    
        columnas = new ArrayList<>();
        indicesColumnas = new HashMap<>();
    
        // Determinar el tipo de cada columna con base en la primera fila
        List<?> primeraFila = (List<?>) secuenciaLineal.get(0);
        for (int i = 0; i < primeraFila.size(); i++) {
            agregarColumna("Columna" + (i + 1), primeraFila.get(i).getClass());
        }
    
        // Agregar las filas verificando tipos
        for (Object filaObj : secuenciaLineal) {
            if (filaObj instanceof List) {
                List<?> fila = (List<?>) filaObj;
                agregarFila();
    
                for (int i = 0; i < fila.size(); i++) {
                    String etiquetaColumna = "Columna" + (i + 1);
                    Object valor = fila.get(i);
                    Class<?> tipoEsperado = columnas.get(i).getTipoDato();
    
                    // Verificar que el tipo del valor coincida con el tipo esperado de la columna
                    if (valor != null && !tipoEsperado.isInstance(valor)) {
                        throw new TipoIncompatible(
                            "El valor en la columna " + etiquetaColumna + " no es compatible. Se esperaba: "
                            + tipoEsperado.getSimpleName() + ", pero se encontró: " + valor.getClass().getSimpleName()
                        );
                    }
                    setValorCelda(getCantidadFilas() - 1, etiquetaColumna, valor);
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

    public Columna<?> getColumna(String etiqueta) throws EtiquetaInvalida {
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

    public void visualizar(int maxFilas, int maxColumnas, int maxAnchoCelda, int filaInicio) {
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
    
    

    public <T> void agregarColumna(String etiqueta, Class<T> tipoDato) throws TipoIncompatible, EtiquetaInvalida {
        if (!(tipoDato == Integer.class || tipoDato == Double.class || tipoDato == Boolean.class || tipoDato == String.class || tipoDato == null)) {
            throw new TipoIncompatible("Tipo de dato no soportado. Solo se permiten: Numérico (entero, real), Booleano y Cadena.");
        }
        if (indicesColumnas.containsKey(etiqueta)) {
            throw new EtiquetaInvalida("La columna con la etiqueta '" + etiqueta + "' ya existe.");
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
                        }
                    } catch (TipoIncompatible e) {
                        // Manejo de la excepción en caso de tipo incompatible
                        System.err.println("No se pudo establecer el valor vacío en la columna " + columna.getEtiquetaColumna() + ": " + e.getMessage());
                    }
                }
            }
        }
    }
    
    
    
    // Método auxiliar para obtener el valor de reemplazo adecuado según el tipo de la columna
    private Object obtenerValorReemplazo(Class<?> tipoDato) {
        if (tipoDato == String.class) {
            return ""; // Cadena vacía para String
        } else if (tipoDato == Integer.class) {
            return 0; // 0 para Integer
        } else if (tipoDato == Boolean.class) {
            return false; // false para Boolean
        } else if (tipoDato == Double.class) {
            return 0.0; // 0.0 para Double
        }
        return null; // Tipo no soportado
    }
    

    public void head(int n) {
        visualizar(n, getCantidadColumnas(), 10, 0);
    }

    public void tail(int n) {
        int totalFilas = getCantidadFilas();
        int filasDesde = Math.max(0, totalFilas - n); // Calcula desde dónde mostrar las filas (si n > totalFilas, comienza desde la fila 0)
        int filasMostrar = Math.min(n, totalFilas);   // Asegura que no se muestren más filas que las existentes
        
        System.out.println("Mostrando las últimas " + filasMostrar + " filas:");
        
        visualizar(filasMostrar, getCantidadColumnas(), 10, filasDesde);
    }

    public Tabla filtrar(String query) throws EtiquetaInvalida, TipoIncompatible {
        // Crear una lista para almacenar las filas seleccionadas
        List<Object[]> filasSeleccionadas = new ArrayList<>();
        
        // Crear la primera fila con las etiquetas de las columnas
        Object[] etiquetas = new Object[getCantidadColumnas()];
        for (int j = 0; j < getCantidadColumnas(); j++) {
            etiquetas[j] = columnas.get(j).getEtiquetaColumna();  // Obtener las etiquetas de las columnas
        }
        
        // Agregar la fila de etiquetas primero
        filasSeleccionadas.add(etiquetas);
        
        // Obtener todas las filas que cumplan con el filtro
        for (int i = 0; i < getCantidadFilas(); i++) {
            if (evaluarFila(i, query)) {
                // Crear un array que representa una fila
                Object[] valorFila = new Object[getCantidadColumnas()]; // Array para una fila
                for (int j = 0; j < getCantidadColumnas(); j++) {
                    valorFila[j] = columnas.get(j).getValor(i); // Obtener el valor de la columna
                }
                filasSeleccionadas.add(valorFila); // Agregar la fila a la lista
            }
        }
    
        // Si no se seleccionó ninguna fila, devolver una tabla vacía con solo etiquetas
        if (filasSeleccionadas.size() == 1) { // Solo contiene etiquetas
            throw new IllegalArgumentException("No se encontraron filas que coincidan con los criterios de filtrado.");  // Solo etiquetas sin datos
        }
    
        // Crear la tabla filtrada a partir de la matriz de filas seleccionadas
        return new Tabla(filasSeleccionadas.toArray(new Object[0][])); // Usar la lista de filas seleccionadas
    }
     
    // Este método evalúa si una fila cumple con la condición del query
    private boolean evaluarFila(int fila, String query) throws EtiquetaInvalida {
        // Separar las condiciones utilizando operadores lógicos como delimitadores
        List<String> condiciones = new ArrayList<>();
        List<String> operadoresLogicos = new ArrayList<>();

        // Identificar condiciones y operadores
        String[] partesQuery = query.split(" ");
        for (int i = 0; i < partesQuery.length; i++) {
            String parte = partesQuery[i].trim();
            if (parte.equals("and") || parte.equals("or") || parte.equals("not")) {
                operadoresLogicos.add(parte);
            } else {
                // Asegúrate de que el índice no exceda el tamaño del arreglo
                if (i + 2 < partesQuery.length) {
                    condiciones.add(partesQuery[i] + " " + partesQuery[i + 1] + " " + partesQuery[i + 2]);
                    i += 2; // Saltar operador y valor
                } else {
                    throw new IllegalArgumentException("La condición está incompleta: " + parte);
                }
            }
        }

        boolean resultado = evaluarCondicionSimple(fila, condiciones.get(0));  // Evaluar la primera condición

        // Evaluar las siguientes condiciones con operadores lógicos
        for (int i = 1; i < condiciones.size(); i++) {
            String operador = operadoresLogicos.get(i - 1);  // Obtenemos el operador previo a la condición
            boolean siguienteCondicion = evaluarCondicionSimple(fila, condiciones.get(i));

            switch (operador) {
                case "and":
                    resultado = resultado && siguienteCondicion;
                    break;
                case "or":
                    resultado = resultado || siguienteCondicion;
                    break;
                case "not":
                    resultado = resultado && !siguienteCondicion;  // NOT aplica solo a la siguiente condición
                    break;
            }
        }

        return resultado;
    }

    private boolean evaluarCondicionSimple(int fila, String condicion) throws EtiquetaInvalida {
        // Parsear la condición (por ejemplo, "columna1 > 3")
        String[] partes = condicion.split(" ");
        if (partes.length != 3) {
            throw new IllegalArgumentException("Condición no válida: " + condicion);
        }
        
        String columna = partes[0];
        String operador = partes[1];
        String valorStr = partes[2];

        Object valorCelda = getCelda(fila, columna); // Asegúrate de que getCelda maneje nombres de columnas
        return evaluarCondicion(valorCelda, operador, valorStr);
    }


    private boolean evaluarCondicion(Object valorCelda, String operador, String valorComparacion) {
        if (valorCelda instanceof Number) { // Para Integer y Double
            double valor = Double.parseDouble(valorCelda.toString());
            double valorComp = Double.parseDouble(valorComparacion);
            switch (operador) {
                case ">": return valor > valorComp;
                case "<": return valor < valorComp;
                case "=": return valor == valorComp;
            }
        } else if (valorCelda instanceof Boolean) {
            boolean valor = Boolean.parseBoolean(valorCelda.toString());
            boolean valorComp = Boolean.parseBoolean(valorComparacion);
            return valor == valorComp;
        } else if (valorCelda instanceof String) {
            return valorCelda.equals(valorComparacion);
        }
        return false; // Si el tipo no es compatible
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

    public Tabla concatenar(Tabla otraTabla) throws TipoIncompatible, EtiquetaInvalida {
        // Verificar que las dos tablas tienen el mismo número de columnas
        if (this.getCantidadColumnas() != otraTabla.getCantidadColumnas()) {
            throw new TipoIncompatible("Las tablas no tienen la misma cantidad de columnas.");
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

    public Tabla ordenar(Tabla tabla, List<String> etiquetasColumnas, boolean ascendente) throws EtiquetaInvalida, TipoIncompatible {
        // Hacer una copia profunda de la tabla original
        Tabla tablaOrdenada = hacerCopiaProfunda(tabla);
    
        // Validar que las etiquetas de las columnas existan
        for (String etiqueta : etiquetasColumnas) {
            if (!tablaOrdenada.indicesColumnas.containsKey(etiqueta)) {
                throw new EtiquetaInvalida("La etiqueta de la columna '" + etiqueta + "' no existe.");
            }
        }
    
        // Crear un comparador para las filas basado en las etiquetas de las columnas
        Comparator<Integer> comparadorFilas = (fila1, fila2) -> {
            for (String etiqueta : etiquetasColumnas) {
                Columna<?> columna;
                try {
                    columna = tablaOrdenada.getColumna(etiqueta);
                    Object valor1 = columna.getValor(fila1);
                    Object valor2 = columna.getValor(fila2);
    
                    if (valor1 == null && valor2 == null) return 0;
                    if (valor1 == null) return ascendente ? -1 : 1;
                    if (valor2 == null) return ascendente ? 1 : -1;
    
                    // Asegúrate de que los valores son comparables
                    if (valor1.getClass() != columna.getTipoDato()) {
                        try {
                            valor1 = convertValue(valor1, columna.getTipoDato());
                        } catch (TipoIncompatible e) {
                            e.printStackTrace();
                        }
                    }
                    if (valor2.getClass() != columna.getTipoDato()) {
                        try {
                            valor2 = convertValue(valor2, columna.getTipoDato());
                        } catch (TipoIncompatible e) {
                            e.printStackTrace();
                        }
                    }
    
                    @SuppressWarnings("unchecked")
                    int comparacion = ((Comparable<Object>) valor1).compareTo(valor2);
                    if (comparacion != 0) {
                        return ascendente ? comparacion : -comparacion;
                    }
                } catch (EtiquetaInvalida e) {
                    e.printStackTrace();
                }
            }
            return 0;
        };
    
        // Obtener los índices de fila y ordenarlos
        List<Integer> indicesFilas = new ArrayList<>();
        for (int i = 0; i < tabla.getCantidadFilas(); i++) {
            indicesFilas.add(i);
        }
        indicesFilas.sort(comparadorFilas);
    
        // Crear una nueva tabla ordenada
        Object[][] matrizInicial = new Object[1][tabla.getCantidadColumnas()];
        for (int i = 0; i < tabla.getCantidadColumnas(); i++) {
            matrizInicial[0][i] = tabla.columnas.get(i).getEtiquetaColumna();
        }
    
        Tabla nuevaTabla = new Tabla(matrizInicial);  // Usar matriz para inicializar
    
        // Agregar las filas ordenadas a la nueva tabla
        for (int filaOrdenada : indicesFilas) {
            nuevaTabla.agregarFila();
            for (Columna<?> columna : tablaOrdenada.columnas) {
                Object valor = columna.getValor(filaOrdenada);
                try {
                    // Asegúrate de convertir el valor antes de establecerlo
                    Object valorConvertido = convertValue(valor, columna.getTipoDato());
                    nuevaTabla.setValorCelda(nuevaTabla.getCantidadFilas() - 1, columna.getEtiquetaColumna(), valorConvertido);
                } catch (TipoIncompatible e) {
                    System.err.println("Error al establecer valor en la columna: " + columna.getEtiquetaColumna());
                    System.err.println("Valor original: " + valor);
                    System.err.println("Tipo esperado: " + columna.getTipoDato().getName());
                    throw e; // Vuelve a lanzar la excepción
                }
            }
        }
    
        return nuevaTabla;
    }
    
    // Método de conversión de tipos
    private Object convertValue(Object value, Class<?> targetType) throws TipoIncompatible {
        if (value == null) return null;
    
        // Si el valor ya es del tipo esperado, devuélvelo sin conversión
        if (targetType.isInstance(value)) {
            return value;
        }
    
        // Conversiones específicas para Integer, Double, Boolean y String
        try {
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
    
    

    /* public void imputarValoresFaltantes(Object valorImputacion) {
        // Recorremos todas las columnas y filas para buscar valores faltantes
        for (Columna<?> columna : columnas) {
            for (int i = 0; i < columna.getTamanio(); i++) {
                Object valorActual = columna.getValor(i);
    
                // Verificamos si el valor es nulo o es el literal "NA"
                if (valorActual == null || "NA".equals(valorActual)) {
                    try {
                        columna.setValor(i, valorImputacion);
                    } catch (TipoIncompatible e) {
                        System.err.println("Error al imputar valor: " + e.getMessage());
                    }
                }
            }
        }
    } */

    /* public void imputarValoresFaltantes(String etiquetaColumna, Object valorImputacion) throws EtiquetaInvalida {
        if (!indicesColumnas.containsKey(etiquetaColumna)) {
            throw new EtiquetaInvalida("La columna con la etiqueta '" + etiquetaColumna + "' no existe.");
        }
    
        // Obtener la columna por su etiqueta
        Columna<?> columna = columnas.get(indicesColumnas.get(etiquetaColumna));
    
        // Recorremos todas las filas de esa columna
        for (int i = 0; i < columna.getTamanio(); i++) {
            Object valorActual = columna.getValor(i);
    
            // Verificamos si el valor es nulo o es el literal "NA"
            if (valorActual == null || "NA".equals(valorActual)) {
                try {
                    columna.setValor(i, valorImputacion);
                } catch (TipoIncompatible e) {
                    System.err.println("Error al imputar valor en columna '" + etiquetaColumna + "': " + e.getMessage());
                }
            }
        }
    } */

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

    
    /* public void rellenarColumna(String etiqueta, Object valor) throws EtiquetaInvalida, TipoIncompatible {
        if (!indicesColumnas.containsKey(etiqueta)) {
            throw new EtiquetaInvalida("La etiqueta de la columna no existe.");
        }
        int columnaIndex = indicesColumnas.get(etiqueta);
        Columna<?> columna = columnas.get(columnaIndex);
    
        // Verificar el tipo de dato y actualizar todas las celdas de la columna
        Class<?> tipoDato = columna.getTipoDato();
        if (!tipoDato.isInstance(valor)) {
            throw new TipoIncompatible("El tipo de dato no coincide con el de la columna.");
        }
    
        for (int i = 0; i < getCantidadFilas(); i++) {
            columna.setValor(i, valor);
        }
    } */
    
}



