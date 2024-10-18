import java.util.List;

import Excepciones.EtiquetaInvalida;
import Excepciones.TipoIncompatible;

public class Main {
    public static void main(String[] args) {
        // Crear una tabla
        Tabla tabla = new Tabla();

        try {
            // Agregar columnas
            tabla.agregarColumna("Nombre", String.class);
            tabla.agregarColumna("Edad", Integer.class);
            tabla.agregarColumna("Altura", Double.class);
            System.out.println("Tabla inicial (sin filas):");
            tabla.visualizar(5, 3, 10); // Visualizar tabla vacía con 5 filas y 3 columnas como máximo

            // Agregar filas con IDs
            tabla.agregarFila(0);
            tabla.agregarFila(1);
            tabla.agregarFila(2);

            // Asignar valores a las celdas
            tabla.setValorCelda(0, "Nombre", "Juan");
            tabla.setValorCelda(0, "Edad", 25);
            tabla.setValorCelda(0, "Altura", 1.75);

            tabla.setValorCelda(1, "Nombre", "Ana");
            tabla.setValorCelda(1, "Edad", 30);
            tabla.setValorCelda(1, "Altura", 1.60);

            tabla.setValorCelda(2, "Nombre", "Pedro");
            tabla.setValorCelda(2, "Edad", 20);
            tabla.setValorCelda(2, "Altura", 1.80);

            System.out.println("\nTabla con valores iniciales:");
            tabla.visualizar(5, 3, 10); // Visualizar tabla con datos

            // Eliminar una columna y visualizar la tabla modificada
            tabla.eliminarColumna("Altura");
            System.out.println("\nTabla después de eliminar la columna 'Altura':");
            tabla.visualizar(5, 2, 10); // Ahora solo dos columnas visibles

            // Eliminar una fila y visualizar la tabla modificada
            tabla.eliminarFila(1);
            System.out.println("\nTabla después de eliminar la fila con ID 1 (Ana):");
            tabla.visualizar(5, 2, 10); // Visualizar con 2 columnas y 2 filas

            // Modificar un valor y visualizar la tabla modificada
            tabla.setValorCelda(0, "Edad", 26); // Cambiar la edad de Juan
            System.out.println("\nTabla después de modificar la edad de Juan:");
            tabla.visualizar(5, 2, 10); // Visualizar con cambios


            System.out.println("Muestrear el 50% de las filas:");
            tabla.muestrear(50, 10);  // 50% de las filas, con ancho máximo de celda 10

        } catch (EtiquetaInvalida | TipoIncompatible e) {
            System.out.println(e.getMessage());
        }

        // Seleccionar filas y columnas específicas
        System.out.println("Selección parcial:");
        List<String> columnas = List.of("Nombre", "Altura");
        List<Integer> filas = List.of(0, 2); // Solo filas 0 y 2
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
            tabla1.agregarFila(0);
            tabla1.setValorCelda(0, "Nombre", "Ana");
            tabla1.setValorCelda(0, "Edad", 25);
            tabla1.agregarFila(1);
            tabla1.setValorCelda(1, "Nombre", "Luis");
            tabla1.setValorCelda(1, "Edad", 30);
            
            Tabla tabla2 = new Tabla();
            tabla2.agregarColumna("Nombre", String.class);
            tabla2.agregarColumna("Edad", Integer.class);
            tabla2.agregarFila(0);
            tabla2.setValorCelda(0, "Nombre", "Carlos");
            tabla2.setValorCelda(0, "Edad", 35);
            
            Tabla tablaConcatenada = tabla1.concatenar(tabla2);
            tablaConcatenada.visualizar(10, 10, 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}