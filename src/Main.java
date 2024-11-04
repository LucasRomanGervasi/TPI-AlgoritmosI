import ComponentesTabla.Tabla;
import ComponentesTabla.Tabla.Operacion;
import Excepciones.DimensionesIncompatibles;
import Excepciones.EtiquetaInvalida;
import Excepciones.TipoIncompatible;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    private static boolean tablaIngresadaManual = false;
    public static void main(String[] args) throws TipoIncompatible, EtiquetaInvalida {
        Scanner scanner = new Scanner(System.in);
        boolean continuar = true;
        Tabla tabla = null;

        while (continuar) {
            // Si la tabla no se ha cargado aún, preguntar cómo desea cargarla
            if (tabla == null) {
                tabla = elegirTabla(scanner);
            }

            System.out.println("Seleccione una operación:");
            System.out.println("1. Modificar valor en una tabla");
            System.out.println("2. Eliminar columna");
            System.out.println("3. Eliminar fila");
            System.out.println("4. Mostrar primeras filas (head)");
            System.out.println("5. Mostrar últimas filas (tail)");
            System.out.println("6. Concatenar tablas");
            System.out.println("7. Crear copia profunda");
            System.out.println("8. Filtrar tabla");
            System.out.println("9. Ordenar tabla");
            System.out.println("10. Muestrear tabla");
            System.out.println("11. Eliminar valores NA");
            System.out.println("12. Agrupar tabla");
            System.out.println("0. Salir");

            System.out.print("Ingrese su opción: ");
            int opcion = scanner.nextInt();
            scanner.nextLine(); // Consumir nueva línea

            if (opcion == 0) {
                System.out.println("Saliendo del programa...");
                break;
            }

            ejecutarOperacion(tabla, opcion, scanner);

            System.out.print("¿Desea ejecutar otra operación? (1 - Sí, 2 - No): ");
            int respuesta = scanner.nextInt();
            if (respuesta != 1) {
                continuar = false;
            }
        }

        scanner.close();
        System.out.println("Programa finalizado.");
    }

    private static Tabla elegirTabla(Scanner scanner) {
        System.out.println("¿Desea cargar una tabla de ejemplo o cargar manualmente?");
        System.out.println("1. Usar tabla de ejemplo");
        System.out.println("2. Cargar manualmente");
        System.out.print("Ingrese su opción: ");
        int eleccion = scanner.nextInt();
        scanner.nextLine(); // Consumir nueva línea

        if (eleccion == 1) {
            // Tabla de ejemplo
            Object[][] datosEjemplo = {
                {"Nombre", "Edad", "Altura"},
                {"Lucas", 25, 1.80},
                {"Ana", 30, 1.65},
                {"Juan", 28, 1.75}
            };
            tablaIngresadaManual = false;
            System.out.println("Tabla de ejemplo cargada.");
            return new Tabla(datosEjemplo);
        } else {
            System.out.println("Seleccione el método para ingresar la tabla:");
            System.out.println("1. Ingresar matriz de objetos");
            System.out.println("2. Ingresar desde archivo CSV");
            System.out.println("3. Ingresar desde secuencia lineal");
            System.out.print("Ingrese su opción: ");
            tablaIngresadaManual = true;
            int metodoCarga = scanner.nextInt();
            scanner.nextLine(); // Consumir nueva línea

            switch (metodoCarga) {
                case 1:
                    // Ingresar matriz de objetos
                    System.out.print("Ingrese el número de filas: ");
                    int filas = scanner.nextInt();
                    System.out.print("Ingrese el número de columnas: ");
                    int columnas = scanner.nextInt();
                    scanner.nextLine(); // Consumir nueva línea

                    Object[][] matriz = new Object[filas][columnas];
                    for (int i = 0; i < filas; i++) {
                        for (int j = 0; j < columnas; j++) {
                            System.out.print("Ingrese el valor para la celda [" + i + "][" + j + "]: ");
                            matriz[i][j] = scanner.nextLine();
                        }
                    }
                    return new Tabla(matriz);

                case 2:
                    // Ingresar desde archivo CSV
                    System.out.print("Ingrese la ruta del archivo CSV: ");
                    String rutaCSV = scanner.nextLine();
                    System.out.print("¿Desea incluir headers? (true/false): ");
                    boolean incluirHeaders = scanner.nextBoolean();
                    scanner.nextLine(); // Consumir nueva línea
                    return new Tabla(rutaCSV, incluirHeaders);

                case 3:
                    // Ingresar desde secuencia lineal
                    System.out.print("Ingrese los elementos de la secuencia (separados por comas): ");
                    String inputSecuencia = scanner.nextLine();
                    
                    // Crear una lista con un único elemento que sea la lista de los valores
                    List<Object> secuencia = new ArrayList<>();
                    List<Object> fila = Arrays.stream(inputSecuencia.split(","))
                        .map(String::trim)  // Limpiar espacios
                        .collect(Collectors.toList());
                    
                    secuencia.add(fila); // Agregar la fila a la lista principal
                
                    return new Tabla(secuencia);  // Pasar la lista a la tabla

                default:
                    System.out.println("Opción inválida. Usando tabla de ejemplo por defecto.");
                    Object[][] datosFallback = {
                        {"Nombre", "Edad", "Altura"},
                        {"Lucas", 25, 1.80},
                        {"Ana", 30, 1.65},
                        {"Juan", 28, 1.75}
                    };
                    Tabla tablaEjemplo = new Tabla(datosFallback);
                    System.out.println("Tabla de ejemplo cargada:");
                    tablaEjemplo.mostrar(5, 5, 10, 0);
                    return tablaEjemplo;
            }
        }
    }

    private static void ejecutarOperacion(Tabla tabla, int opcion, Scanner scanner) throws TipoIncompatible, EtiquetaInvalida {
        switch (opcion) {
            case 1:
                // Mostrar las etiquetas de las filas y columnas disponibles
                List<Object> etiquetasFilas = tabla.getEtiquetasFilas();
                List<Object> etiquetasColumnas = tabla.getEtiquetasColumnas();
            
                System.out.println("Etiquetas de filas disponibles:");
                for (int i = 0; i < etiquetasFilas.size(); i++) {
                    System.out.println((i + 1) + " - " + etiquetasFilas.get(i));
                }
            
                System.out.println("Etiquetas de columnas disponibles:");
                for (int i = 0; i < etiquetasColumnas.size(); i++) {
                    System.out.println((i + 1) + " - " + etiquetasColumnas.get(i));
                }
            
                // Solicitar selección de fila
                int indiceFila = -1;
                while (indiceFila < 0 || indiceFila >= etiquetasFilas.size()) {
                    System.out.print("Ingrese el número correspondiente a la fila: ");
                    indiceFila = scanner.nextInt() - 1;
                    if (indiceFila < 0 || indiceFila >= etiquetasFilas.size()) {
                        System.out.println("Índice de fila no válido. Intente nuevamente.");
                    }
                }
            
                // Solicitar selección de columna
                int indiceColumna = -1;
                while (indiceColumna < 0 || indiceColumna >= etiquetasColumnas.size()) {
                    System.out.print("Ingrese el número correspondiente a la columna: ");
                    indiceColumna = scanner.nextInt() - 1;
                    if (indiceColumna < 0 || indiceColumna >= etiquetasColumnas.size()) {
                        System.out.println("Índice de columna no válido. Intente nuevamente.");
                    }
                }
            
                String nombreColumna = etiquetasColumnas.get(indiceColumna).toString();
            
                // Obtener el tipo de dato de la columna seleccionada
                Class<?> tipoColumna = tabla.getTipoDatoColumna(nombreColumna);
                System.out.println("El tipo de dato para la columna " + nombreColumna + " es: " + tipoColumna.getSimpleName());
            
                // Solicitar el nuevo valor con el tipo de dato correspondiente
                Object nuevoValor = null;
                boolean valorValido = false;
                while (!valorValido) {
                    System.out.print("Ingrese un nuevo valor para la celda en el tipo " + tipoColumna.getSimpleName() + ": ");
                    String valorIngresado = scanner.next();
            
                    try {
                        // Validar el tipo de dato ingresado y asignarlo a nuevoValor
                        if (tipoColumna == Integer.class) {
                            nuevoValor = Integer.parseInt(valorIngresado);
                        } else if (tipoColumna == Double.class) {
                            nuevoValor = Double.parseDouble(valorIngresado);
                        } else if (tipoColumna == Boolean.class) {
                            nuevoValor = Boolean.parseBoolean(valorIngresado);
                        } else if (tipoColumna == String.class) {
                            nuevoValor = valorIngresado;
                        }
                        valorValido = true;
                    } catch (Exception e) {
                        System.out.println("El valor ingresado no coincide con el tipo " + tipoColumna.getSimpleName() + ". Intente nuevamente.");
                    }
                }
            
                System.out.println("Tabla actual antes de la modificación:");
                tabla.mostrar(5, 5, 10, 0);
            
                // Realizar la modificación de la celda
                tabla.setValorCelda(indiceFila, nombreColumna, nuevoValor);
            
                // Mostrar la tabla después de la modificación
                System.out.println("Tabla después de la modificación:");
                tabla.mostrar(5, 5, 10, 0);
            
                break;
            
            // Caso 5
            case 2:
                System.out.println("Tabla actual antes de eliminar la columna:");
                tabla.mostrar(5, 5, 10, 0);

                List<Object> etiquetasColumnasEliminar = tabla.getEtiquetasColumnas();
                System.out.println("Columnas disponibles para eliminar:");
                for (int i = 0; i < etiquetasColumnasEliminar.size(); i++) {
                    System.out.println((i + 1) + " - " + etiquetasColumnasEliminar.get(i));
                }

                System.out.print("Ingrese el número correspondiente a la columna a eliminar: ");
                int indiceColumnaEliminar = scanner.nextInt() - 1;
                scanner.nextLine();

                // Verificar si el índice está dentro del rango
                if (indiceColumnaEliminar < 0 || indiceColumnaEliminar >= etiquetasColumnasEliminar.size()) {
                    System.out.println("Índice de columna no válido. Operación cancelada.");
                    break;
                }

                String colEliminar = etiquetasColumnasEliminar.get(indiceColumnaEliminar).toString();
                tabla.eliminarColumna(colEliminar);

                System.out.println("Tabla después de eliminar la columna:");
                tabla.mostrar(5, 5, 10, 0);
                break;

                // Caso 6
            case 3:
                System.out.println("Tabla actual antes de eliminar la fila:");
                tabla.mostrar(5, 5, 10, 0);

                List<Object> etiquetasFilasEliminar = tabla.getEtiquetasFilas();
                System.out.println("Filas disponibles para eliminar:");
                for (int i = 0; i < etiquetasFilasEliminar.size(); i++) {
                    System.out.println((i + 1) + " - " + etiquetasFilasEliminar.get(i));
                }

                System.out.print("Ingrese el número correspondiente a la fila a eliminar: ");
                int indiceFilaEliminar = scanner.nextInt() - 1;
                scanner.nextLine();

                if (indiceFilaEliminar < 0 || indiceFilaEliminar >= etiquetasFilasEliminar.size()) {
                    System.out.println("Índice de fila no válido. Operación cancelada.");
                    break;
                }

                tabla.eliminarFila(indiceFilaEliminar);
                System.out.println("Tabla después de eliminar la fila:");
                tabla.mostrar(5, 5, 10, 0);
                break;

                // Caso 7 y 8
            case 4:
                System.out.print("Ingrese la cantidad de filas a mostrar (head): ");
                int nHead = scanner.nextInt();
                if (nHead <= 0 || nHead > tabla.getCantidadFilas()) {
                    System.out.println("Cantidad no válida. Debe ser entre 1 y " + tabla.getCantidadFilas());
                    break;
                }
                tabla.head(nHead);
                break;

            case 5:
                System.out.print("Ingrese la cantidad de filas a mostrar (tail): ");
                int nTail = scanner.nextInt();
                if (nTail <= 0 || nTail > tabla.getCantidadFilas()) {
                    System.out.println("Cantidad no válida. Debe ser entre 1 y " + tabla.getCantidadFilas());
                    break;
                }
                tabla.tail(nTail);
                break;

            case 6:
                 // Mostrar la tabla actual antes de la concatenación
                 System.out.println("Tabla actual:");
                 tabla.mostrar(5, 5, 10, 0);
 
                 Tabla tablaAConcatenar;
                 if (tablaIngresadaManual) {
                     // Forzar ingreso de una nueva tabla manual
                     System.out.println("Ingrese una segunda tabla de forma manual para concatenar.");
                     tablaAConcatenar = elegirTabla(scanner);
                 } else {
                     // Usar tabla de ejemplo
                     Object[][] datosEjemplo = {
                         {"Nombre", "Edad", "Altura"},
                         {"Facundo", 20, 1.60},
                         {"Ricardo", 39, 1.95},
                         {"Sofia", 19, 1.55}
                     };
                     tablaAConcatenar = new Tabla(datosEjemplo);
                     System.out.println("Tabla de ejemplo para concatenación:");
                     tablaAConcatenar.mostrar(5, 5, 10, 0);
                 }
 
                 try {
                     Tabla tablaConcatenada = tabla.concatenar(tablaAConcatenar);
                     System.out.println("Tabla después de la concatenación:");
                     tablaConcatenada.mostrar(6, 5, 10, 0);
                 } catch (DimensionesIncompatibles e) {
                     System.out.println("Error: No se puede concatenar las tablas debido a dimensiones incompatibles.");
                 }
                 break;
            
            case 7:
                // Verificar si la tabla actual es nula antes de intentar hacer una copia profunda
                if (tabla == null) {
                    System.out.println("Error: No se puede hacer una copia profunda porque la tabla actual es nula.");
                } else {
                    Tabla copiaProfunda = tabla.hacerCopiaProfunda(tabla);
                    System.out.println("Copia profunda de la tabla:");
                    copiaProfunda.mostrar(5, 5, 10, 0);
                }
                break;
            
            case 8:
                // Mostrar las columnas disponibles para filtrar
                System.out.println("Columnas disponibles para filtrar:");
                List<Object> columnasDisponibles = tabla.getEtiquetasColumnas();
                for (int i = 0; i < columnasDisponibles.size(); i++) {
                    System.out.println((i + 1) + " - " + columnasDisponibles.get(i));
                }
            
                // Dar un ejemplo de formato de consulta
                System.out.println("\nEjemplo de formato para la consulta:");
                System.out.println("  Columna1 > 10 AND Columna2 < 20 OR NOT Columna3 = 'Valor'");
                System.out.println("Formato: Columna condición (<, >, =, !=, <=, >=) valor (AND, OR, NOT) Columna condición valor");
            
                System.out.println("Tabla a filtrar: ");
                tabla.mostrar(5, 5, 10, 0);
            
                // Solicitar la condición de filtrado al usuario
                System.out.print("Ingrese la condición de filtrado: ");
                String condicion = scanner.nextLine();
            
                try {
                    // Realizar el filtrado y mostrar la tabla resultante
                    Tabla tablaFiltrada = tabla.filtrar(condicion);
                    System.out.println("Tabla después de aplicar el filtro:");
                    tablaFiltrada.mostrar(5, 5, 30, 0);
                } catch (Exception e) {
                    System.out.println("Error: Condicion invalida");
                }
                break;
            

                // Caso 12
            case 9:
                System.out.println("Tabla actual:");
                tabla.mostrar(5, 5, 10, 0);

                System.out.println("Etiquetas de columnas disponibles para ordenar:");
                System.out.println(tabla.getEtiquetasColumnas());

                System.out.print("Ingrese las etiquetas de las columnas para ordenar (separadas por comas): ");
                String etiquetasInput = scanner.nextLine();
                List<String> etiquetas = Arrays.stream(etiquetasInput.split(",\\s*"))
                                            .collect(Collectors.toList());

                List<Boolean> ordenAscendente = new ArrayList<>();
                for (String etiqueta : etiquetas) {
                    if (!tabla.getEtiquetasColumnas().contains(etiqueta)) {
                        System.out.println("Etiqueta inválida: " + etiqueta + ". Operación cancelada.");
                        break;
                    }
                    
                    while (true) {
                        System.out.print("¿Desea ordenar la columna '" + etiqueta + "' de manera ascendente (true) o descendente (false)? Ingrese true o false: ");
                        String ordenInput = scanner.nextLine().trim();
                        if (ordenInput.equalsIgnoreCase("true")) {
                            ordenAscendente.add(true);
                            break;
                        } else if (ordenInput.equalsIgnoreCase("false")) {
                            ordenAscendente.add(false);
                            break;
                        } else {
                            System.out.println("Entrada no válida. Por favor, ingrese 'true' para ascendente o 'false' para descendente.");
                        }
                    }
                }

                try {
                    Tabla tablaOrdenada = tabla.ordenar(tabla, etiquetas, ordenAscendente);
                    System.out.println("\nTabla después de ordenar:");
                    tablaOrdenada.mostrar(5, 5, 10, 0);
                } catch (EtiquetaInvalida e) {
                    System.out.println("Error: " + e.getMessage());
                } catch (TipoIncompatible e) {
                    System.out.println("Error de tipo incompatible: " + e.getMessage());
                }
                break;
            case 10:
                System.out.println("Tabla actual:");
                tabla.mostrar(5, 5, 10, 0);

                System.out.print("Ingrese el porcentaje de muestreo: ");
                int porcentaje = scanner.nextInt();
                tabla.muestrear(porcentaje, 10);
                break;
            case 11:
                Object[][] datosEj = {
                    {"Nombre", "Edad", "Altura"},
                    {"NAN", null, 1.60},
                    {"Ricardo", 39, 1.95},
                    {"NA", 19, null}
                    };
                Tabla tablaEj = new Tabla(datosEj);
                System.out.println("Tabla antes de eliminar valores NA:");
                tablaEj.mostrar(5, 5, 10, 0);

                tablaEj.eliminarTodosNAs();
                System.out.println("Tabla después de eliminar valores NA:");
                tablaEj.mostrar(5, 5, 10, 0);

                break;

                // Caso 12
            case 12:
                Object[][] datosAgrupar = {
                    {"Nombre", "Producto", "Cantidad", "Precio"},
                    {"Lucas", "A", 10, 100.0},
                    {"Ana", "B", 5, 200.0},
                    {"Juan", "A", 15, 150.0},
                    {"Lucas", "B", 20, 250.0},
                    {"Ana", "A", 10, 120.0},
                    {"Juan", "B", 5, 220.0},
                    {"Lucas", "A", 5, 110.0},
                    {"Ana", "B", 10, 210.0},
                    {"Juan", "A", 20, 160.0},
                    {"Roberto", "B", 15, 230.0}
                };

                Tabla tablaAgrupar = new Tabla(datosAgrupar);

                System.out.println("Seleccione la operación para agrupar:");
                System.out.println("1 - Suma");
                System.out.println("2 - Máximo");
                System.out.println("3 - Mínimo");
                System.out.println("4 - Cuenta");
                System.out.println("5 - Media");
                System.out.println("6 - Varianza");
                System.out.println("7 - Desvío estándar");

                int operacionSeleccionada = scanner.nextInt();
                Operacion operacion = null;
                switch (operacionSeleccionada) {
                    case 1 -> operacion = Operacion.SUMA;
                    case 2 -> operacion = Operacion.MAXIMO;
                    case 3 -> operacion = Operacion.MINIMO;
                    case 4 -> operacion = Operacion.CUENTA;
                    case 5 -> operacion = Operacion.MEDIA;
                    case 6 -> operacion = Operacion.VARIANZA;
                    case 7 -> operacion = Operacion.DESVIO;
                    default -> System.out.println("Opción inválida.");
                }
                if (operacion == null) break;

                System.out.println("Tabla a agrupar:");
                tablaAgrupar.mostrar(5, 5, 10, 0);

                System.out.println("Etiquetas de la tabla disponibles para agrupar:");
                List<Object> etiquetasDisponibles = tablaAgrupar.getEtiquetasColumnas();
                for (int i = 0; i < etiquetasDisponibles.size(); i++) {
                    System.out.println((i + 1) + " - " + etiquetasDisponibles.get(i));
                }

                List<String> etiquetasAgrupamiento = new ArrayList<>();
                System.out.println("Ingrese los números de las etiquetas para agrupar (separados por comas):");
                String inputEtiquetas = scanner.next();
                String[] indicesEtiquetas = inputEtiquetas.split(",");
                for (String indice : indicesEtiquetas) {
                    try {
                        int idx = Integer.parseInt(indice.trim()) - 1;
                        if (idx < 0 || idx >= etiquetasDisponibles.size()) {
                            System.out.println("Índice inválido: " + indice);
                            continue;
                        }
                        etiquetasAgrupamiento.add(etiquetasDisponibles.get(idx).toString());
                    } catch (Exception e) {
                        System.out.println("Índice inválido: " + indice);
                    }
                }

                Tabla tablaAgrupada = tablaAgrupar.agregarPor(etiquetasAgrupamiento, operacion);
                System.out.println("Tabla agrupada:");
                tablaAgrupada.mostrar(5, 5, 10, 0);
                break;
        }

    }
}


