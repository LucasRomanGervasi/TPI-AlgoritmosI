import ComponentesTabla.Tabla;
import Excepciones.EtiquetaInvalida;
import Excepciones.TipoIncompatible;
import java.util.List;
import java.util.ArrayList;


public class Main {
    public static void main(String[] args) {
        try {
            /* Tabla tabla = new Tabla();

            // Agregar columnas de diferentes tipos
            tabla.agregarColumna("Nombre", String.class);
            tabla.agregarColumna("Edad", Integer.class);
            tabla.agregarColumna("Altura", Double.class);

            // Visualizar la tabla vacía con las etiquetas de columna
            System.out.println("Tabla inicial con columnas:");
            tabla.visualizar(5, 5, 10, 0);
            System.out.println();

            // Agregar filas con datos
            tabla.getColumna("Nombre").agregarCelda("Lucas");
            tabla.getColumna("Edad").agregarCelda(25);
            tabla.getColumna("Altura").agregarCelda(1.80);

            tabla.getColumna("Nombre").agregarCelda("Ana");
            tabla.getColumna("Edad").agregarCelda(30);
            tabla.getColumna("Altura").agregarCelda(1.65);

            tabla.getColumna("Nombre").agregarCelda("Juan");
            tabla.getColumna("Edad").agregarCelda(28);
            tabla.getColumna("Altura").agregarCelda(1.75);
            // Agregar filas con IDs
            tabla.agregarFila(0); //etiqueta de fila y columna pueden ser enteros y strings
            tabla.agregarFila(1);
            tabla.agregarFila(2);

            // Asignar valores a las celdas
            tabla.setValorCelda(0, "Nombre", "Juan");
            tabla.setValorCelda(0, "Edad", 25);
            tabla.setValorCelda(0, "Altura", 1.76);

            // Visualizar la tabla con filas agregadas
            System.out.println("Tabla después de agregar valores:");
            tabla.visualizar(5, 5, 10, 0);
            System.out.println();

            // Modificar un valor específico (reemplazar el nombre "Lucas" con "Lucía")
            tabla.getColumna("Nombre").setValor(0, "Lucía");

            // Visualizar la tabla después de la modificación
            System.out.println("Tabla después de modificar el valor de la primera fila:");
            tabla.visualizar(5, 5, 10, 0);
            System.out.println();

            // Eliminar una columna (por ejemplo, "Altura")
            tabla.eliminarColumna("Altura");

            // Visualizar la tabla después de eliminar la columna "Altura":
            System.out.println("Tabla después de eliminar la columna 'Altura':");
            tabla.visualizar(5, 5, 10, 0);
            System.out.println();

            // Eliminar una fila (por ejemplo, la segunda fila con índice 1)
            tabla.eliminarFila(1);

            // Visualizar la tabla después de eliminar la fila
            System.out.println("Tabla después de eliminar la segunda fila:");
            tabla.visualizar(5, 5, 10, 0);
            System.out.println();

            // Mostrar solo las primeras 2 filas (head)
            System.out.println("Visualización de la primera fila:");
            tabla.head(1);
            System.out.println();

            // Mostrar solo las últimas 2 filas (tail)
            System.out.println("Visualización de la ultima fila:");
            tabla.tail(1);
            System.out.println();

            // Eliminar todos los valores "NA"
            //tabla.getColumna("Nombre").setValor(0, null);
            //tabla.eliminarTodosNAs();
            //System.out.println("Tabla después de eliminar todos los 'NA':");
            //tabla.visualizar(5, 5, 10, 0);
            //System.out.println();

            //PRUEBA ULTIMOS METODOS

            System.out.println("\nTabla después de eliminar la fila con ID 1 (Ana):");
            tabla.visualizar(5, 2, 10); // Visualizar con 2 columnas y 2 filas

            // Modificar un valor y visualizar la tabla modificada
            tabla.setValorCelda(0, "Edad", 26); // Cambiar la edad de Juan
            System.out.println("\nTabla después de modificar la edad de Juan:");
            tabla.visualizar(5, 2, 10); // Visualizar con cambios


            System.out.println("Muestrear el 50% de las filas:");
            tabla.muestrear(50, 10);  // 50% de las filas, con ancho máximo de celda 10

        } catch (EtiquetaInvalida | TipoIncompatible e) {
            System.out.println(e.getMessage()); //no hace falta el try, catch. que corte la ejecucion
        }

        // Seleccionar filas y columnas específicas
        System.out.println("Selección parcial:");
        List<String> columnas = Arrays.asList("Nombre", "Edad"); // Usa Arrays.asList en lugar de List.of
        List<Integer> filas = Arrays.asList(0, 1);  // Solo filas 0 y 2
        try {
            tabla.seleccionar(columnas, filas, 10);  // Max ancho de celda: 10
        } catch (EtiquetaInvalida e) {
            System.out.println(e.getMessage());
        }

        // Mostrar las primeras 2 filas
        System.out.println("\nHead(1):");
        tabla.head(1, 10);

        // Mostrar las últimas 2 filas
        System.out.println("\nTail(1):");
        tabla.tail(1, 10);


        //PRUEBA DE CONCATENACION
        System.out.println("\nPrueba de concatenación:");
        try {
            Tabla tabla1 = new Tabla();
            tabla1.agregarColumna("Nombre", String.class);
            tabla1.agregarColumna("Edad", Integer.class);
            tabla1.agregarColumna("Activo", Boolean.class);

            // Agregar filas a la primera tabla
            tabla1.getColumna("Nombre").agregarCelda("Lucas");
            tabla1.getColumna("Edad").agregarCelda(25);
            tabla1.getColumna("Activo").agregarCelda(true);

            tabla1.getColumna("Nombre").agregarCelda("María");
            tabla1.getColumna("Edad").agregarCelda(30);
            tabla1.getColumna("Activo").agregarCelda(false);

            System.out.println("Tabla 1:");
            tabla1.visualizar(3, 3, 10, 0); // Visualizar la tabla 1

            // Crear la segunda tabla
            Tabla tabla2 = new Tabla();
            tabla2.agregarColumna("Nombre", String.class);
            tabla2.agregarColumna("Edad", Integer.class);
            tabla2.agregarColumna("Activo", Boolean.class);

            // Agregar filas a la segunda tabla
            tabla2.getColumna("Nombre").agregarCelda("Sofia");
            tabla2.getColumna("Edad").agregarCelda(26);
            tabla2.getColumna("Activo").agregarCelda(true);

            tabla2.getColumna("Nombre").agregarCelda("Juan");
            tabla2.getColumna("Edad").agregarCelda(28);
            tabla2.getColumna("Activo").agregarCelda(false);

            System.out.println("Tabla 2:");
            tabla2.visualizar(3, 3, 10, 0); // Visualizar la tabla 2

            // Concatenar las tablas
            Tabla tablaConcatenada = tabla1.concatenar(tabla2);

            System.out.println("Tabla Concatenada:");
            tablaConcatenada.visualizar(10, 3, 10, 0); // Visualizar la tabla concatenada
            

            //Prueba copia profunda
            System.out.println("Tabla copia:");
            Tabla tablaCopia = tabla1.hacerCopiaProfunda(tabla1);
            tablaCopia.visualizar(3, 3, 10, 0); // Visualizar la tabla copia */

            //Prueba ordenar    

            /* Tabla tabla = new Tabla();

        // Agregar las columnas a la tabla
        tabla.agregarColumna("Nombre", String.class);
        tabla.agregarColumna("Edad", Integer.class);
        tabla.agregarColumna("Ciudad", String.class);

        // Agregar filas con datos
        tabla.agregarFila();
        tabla.setValorCelda(0, "Nombre", "Ana");
        tabla.setValorCelda(0, "Edad", 25);
        tabla.setValorCelda(0, "Ciudad", "Buenos Aires");

        tabla.agregarFila();
        tabla.setValorCelda(1, "Nombre", "Juan");
        tabla.setValorCelda(1, "Edad", 30);
        tabla.setValorCelda(1, "Ciudad", "Córdoba");

        tabla.agregarFila();
        tabla.setValorCelda(2, "Nombre", "María");
        tabla.setValorCelda(2, "Edad", 22);
        tabla.setValorCelda(2, "Ciudad", "Rosario");

        tabla.agregarFila();
        tabla.setValorCelda(3, "Nombre", "Pedro");
        tabla.setValorCelda(3, "Edad", 35);
        tabla.setValorCelda(3, "Ciudad", "Mendoza");

        // Imprimir la tabla original
        System.out.println("Tabla Original:");
        tabla.visualizar(10, 3, 10, 0);

        // Ordenar la tabla por Edad en forma ascendente
        List<String> columnasParaOrdenar = new ArrayList<>();
        columnasParaOrdenar.add("Edad");

        Tabla tablaOrdenadaPorEdad = tabla.ordenar(tabla, columnasParaOrdenar, false);

        // Imprimir la tabla ordenada por Edad
        System.out.println("\nTabla Ordenada por Edad (Descendente):");
        tablaOrdenadaPorEdad.visualizar(10, 3, 10, 0);

        // Ordenar la tabla por Nombre en forma descendente
        columnasParaOrdenar.clear();
        columnasParaOrdenar.add("Nombre");

        Tabla tablaOrdenadaPorNombre = tabla.ordenar(tabla, columnasParaOrdenar, false);

        // Imprimir la tabla ordenada por Nombre
        System.out.println("\nTabla Ordenada por Nombre (Descendente):");
        tablaOrdenadaPorNombre.visualizar(10, 3, 10, 0); */


        Tabla miTabla = new Tabla();
        miTabla.agregarColumna("Edad", Integer.class);
        miTabla.agregarColumna("EsEstudiante", Boolean.class);

        miTabla.agregarFila();
        miTabla.setValorCelda(0, "Edad", 25);
        miTabla.setValorCelda(0, "EsEstudiante", true);

        miTabla.agregarFila();
        miTabla.setValorCelda(1, "Edad", 30);
        miTabla.setValorCelda(1, "EsEstudiante", false);

        miTabla.agregarFila();
        miTabla.setValorCelda(2, "Edad", 22);
        miTabla.setValorCelda(2, "EsEstudiante", true);

        // Filtrar filas donde Edad > 23 y EsEstudiante es true
        Tabla tablaFiltrada = miTabla.filtrar("Edad > 23 and EsEstudiante = true");
        tablaFiltrada.visualizar(5, 5, 30, 0);

        int fila = miTabla.getCantidadFilas();
        System.out.println("Cantidad de filas: " + fila);

        miTabla.visualizar(5, 5, 10, 0);

        } catch (TipoIncompatible | EtiquetaInvalida e) {
            System.out.println("Error: " + e.getMessage());
        }

    }


}
