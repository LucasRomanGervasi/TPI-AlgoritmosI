package ComponentesTabla;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import Excepciones.EtiquetaInvalida;
import Excepciones.TipoIncompatible;
import java.util.Random;
import java.util.Comparator;

public class Tabla {
    private List<Columna<?>> columnas;
    private Map<String, Integer> indicesColumnas;

    public Tabla() {
        this.columnas = new ArrayList<>();
        this.indicesColumnas = new HashMap<>();
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

    public <T> void agregarColumna(String etiqueta, Class<T> tipoDato) throws TipoIncompatible {
        if (!(tipoDato == Integer.class || tipoDato == Double.class || tipoDato == Boolean.class || tipoDato == String.class || tipoDato == null)) {
            throw new TipoIncompatible("Tipo de dato no soportado. Solo se permiten: Numérico (entero, real), Booleano y Cadena.");
        }
        Columna<T> nuevaColumna = new Columna<>(etiqueta, tipoDato);
        columnas.add(nuevaColumna);
        indicesColumnas.put(etiqueta, columnas.size() - 1);
    }

    public void setValorCelda(int fila, String etiquetaColumna, Object valor) throws TipoIncompatible, EtiquetaInvalida {
        // Buscar la columna por su etiqueta
        Columna<?> columna = getColumna(etiquetaColumna);
    
        // Verificar si el tipo del valor coincide con el tipo de dato de la columna
        if (!columna.getTipoDato().isInstance(valor)) {
            throw new TipoIncompatible("El tipo de dato no es compatible con el tipo de la columna " + etiquetaColumna);
        }
    
        // Asignar el valor a la celda
        columna.setValor(fila, valor);
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

    public void agregarFila() throws TipoIncompatible {
    
        // Agregar un nuevo valor nulo en cada columna en la nueva fila
        for (Columna<?> columna : columnas) {
            columna.agregarCelda(null); // Primero agrega al final
        }
        
        // Se asegura que las celdas se manejen correctamente
        // Asegúrate de que las columnas tengan la lógica para manejar el tamaño correcto
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

    public void visualizar(int maxFilas, int maxColumnas, int maxAnchoCelda, int filaInicio) {
        int totalFilas = getCantidadFilas();
        int totalColumnas = getCantidadColumnas();
        
        int filasMostrar = Math.min(maxFilas, totalFilas - filaInicio); // Ajusta filas a mostrar desde filaInicio
        int columnasMostrar = Math.min(maxColumnas, totalColumnas); // Ajusta columnas a mostrar
        
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

    public Columna<?> getColumna(String etiqueta) throws EtiquetaInvalida {
        if (!indicesColumnas.containsKey(etiqueta)) {
            throw new EtiquetaInvalida("La etiqueta de la columna no existe.");
        }
        return columnas.get(indicesColumnas.get(etiqueta));
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
    
                    // Intentar establecer el valor como cadena vacía
                    try {
                        columna.setValor(i, ""); // Establece el valor como cadena vacía
                    } catch (TipoIncompatible e) {
                        // Manejo de la excepción en caso de tipo incompatible
                        System.err.println("No se pudo establecer el valor vacío en la columna " + columna.getEtiquetaColumna() + ": " + e.getMessage());
                    }
                }
            }
        }
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
    
    public void rellenarColumna(String etiqueta, Object valor) throws EtiquetaInvalida, TipoIncompatible {
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
    
        // Crear una nueva tabla para almacenar la concatenación
        Tabla tablaConcatenada = new Tabla();
    
        // Agregar las columnas de la primera tabla a la nueva tabla
        for (Columna<?> columna : this.columnas) {
            tablaConcatenada.agregarColumna(columna.getEtiquetaColumna(), columna.getTipoDato());
        }
    
        // Copiar las celdas de la primera tabla a la nueva tabla
        for (int i = 0; i < this.getCantidadFilas(); i++) {
            tablaConcatenada.agregarFila(); // Agregar una nueva fila
            for (int j = 0; j < this.getCantidadColumnas(); j++) {
                Object valorCelda = this.columnas.get(j).getValor(i);
                tablaConcatenada.setValorCelda(i, this.columnas.get(j).getEtiquetaColumna(), valorCelda);
            }
        }
    
        // Copiar las celdas de la segunda tabla a la nueva tabla
        int numeroFilasPrimeraTabla = this.getCantidadFilas();
        for (int i = 0; i < otraTabla.getCantidadFilas(); i++) {
            tablaConcatenada.agregarFila(); // Agregar una nueva fila
            for (int j = 0; j < otraTabla.getCantidadColumnas(); j++) {
                Object valorCelda = otraTabla.columnas.get(j).getValor(i);
                tablaConcatenada.setValorCelda(numeroFilasPrimeraTabla + i, otraTabla.columnas.get(j).getEtiquetaColumna(), valorCelda);
            }
        }
    
        return tablaConcatenada;
    }

    public Tabla hacerCopiaProfunda(Tabla tabla) throws TipoIncompatible, EtiquetaInvalida {
        Tabla tablaCopia = new Tabla();
    
        // Copiar las columnas de la tabla original a la tabla copia
        for (int i = 0; i < tabla.getCantidadColumnas(); i++) {
            String etiqueta = tabla.columnas.get(i).getEtiquetaColumna();
            Class<?> tipoDato = tabla.columnas.get(i).getTipoDato();
            tablaCopia.agregarColumna(etiqueta, tipoDato);
        }
    
        // Copiar las celdas de la tabla original a la tabla copia
        for (int i = 0; i < tabla.getCantidadFilas(); i++) {
            tablaCopia.agregarFila(); // Agregar una nueva fila
            for (int j = 0; j < tabla.getCantidadColumnas(); j++) {
                Object valorCelda = tabla.columnas.get(j).getValor(i);
                tablaCopia.setValorCelda(i, tabla.columnas.get(j).getEtiquetaColumna(), valorCelda);
            }
        }
    
        return tablaCopia;
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
                    // Comparar los valores de las celdas
                    if (valor1 == null && valor2 == null) return 0;
                    if (valor1 == null) return ascendente ? -1 : 1;
                    if (valor2 == null) return ascendente ? 1 : -1;
                    @SuppressWarnings("unchecked")
                    int comparacion = ((Comparable<Object>) valor1).compareTo(valor2);
    
                if (comparacion != 0) {
                    return ascendente ? comparacion : -comparacion;
                }
                } catch (EtiquetaInvalida e) {

                    e.printStackTrace();
                }
    
            }
            return 0; // Si son iguales en todas las columnas, no se cambia el orden
        };
    
        // Obtener los índices de fila y ordenarlos
        List<Integer> indicesFilas = new ArrayList<>();
        for (int i = 0; i < tabla.getCantidadFilas(); i++) {
            indicesFilas.add(i);
        }
        indicesFilas.sort(comparadorFilas);
    
        // Crear una nueva tabla ordenada
        Tabla nuevaTabla = new Tabla();
        for (Columna<?> columna : tablaOrdenada.columnas) {
            nuevaTabla.agregarColumna(columna.getEtiquetaColumna(), columna.getTipoDato());
        }
    
        // Agregar las filas ordenadas a la nueva tabla
        for (int filaOrdenada : indicesFilas) {
            nuevaTabla.agregarFila();
            for (Columna<?> columna : tablaOrdenada.columnas) {
                Object valor = columna.getValor(filaOrdenada);
                nuevaTabla.setValorCelda(nuevaTabla.getCantidadFilas() - 1, columna.getEtiquetaColumna(), valor);
            }
        }
    
        return nuevaTabla; // Retorna la nueva tabla ordenada
    }
    
    public Tabla filtrar(String query) throws EtiquetaInvalida, TipoIncompatible {
        // Crear una nueva tabla que será el resultado del filtrado
        Tabla tablaFiltrada = new Tabla();
        
        // Copiar las columnas de la tabla original a la nueva tabla filtrada
        for (Columna<?> columna : this.columnas) {
            tablaFiltrada.agregarColumna(columna.getEtiquetaColumna(), columna.getTipoDato());
        }
    
        // Obtener todas las filas que cumplan con el filtro
        List<Integer> filasSeleccionadas = new ArrayList<>();
        
        for (int i = 0; i < getCantidadFilas(); i++) {
            if (evaluarFila(i, query)) {
                filasSeleccionadas.add(i);
            }
        }
        
        // Agregar las filas seleccionadas a la nueva tabla
        for (Integer fila : filasSeleccionadas) {
            // Agregar una nueva fila (sin índice de retorno)
            tablaFiltrada.agregarFila();
            for (int j = 0; j < columnas.size(); j++) {
                String etiqueta = columnas.get(j).getEtiquetaColumna();
                Object valor = getCelda(fila, etiqueta);
                // Usar la última fila agregada para establecer el valor
                tablaFiltrada.setValorCelda(tablaFiltrada.getCantidadFilas() - 1, etiqueta, valor);
            }
        }
    
        return tablaFiltrada;
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
                condiciones.add(parte + " " + partesQuery[i + 1] + " " + partesQuery[i + 2]);
                i += 2; // Saltar operador y valor
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
        String columna = partes[0];
        String operador = partes[1];
        String valorStr = partes[2];
    
        Object valorCelda = getCelda(fila, columna);
        return evaluarCondicion(valorCelda, operador, valorStr);
    }

    // Este método compara el valor de la celda con el valor en el query
    private boolean evaluarCondicion(Object valorCelda, String operador, String valorComparacion) {
        if (valorCelda instanceof Integer || valorCelda instanceof Double) {
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
        return false;
    }
    
    
}



