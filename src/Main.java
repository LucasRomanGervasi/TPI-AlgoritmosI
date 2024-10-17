import Excepciones.EtiquetaInvalida;
import Excepciones.TipoIncompatible;
import java.util.List;

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
            tabla.setValorCelda(0, "Edad", 10);
            tabla.setValorCelda(0, "Altura", 1.75);

            tabla.setValorCelda(1, "Nombre", "NA");
            tabla.setValorCelda(1, "Edad", 10);  // Edad es null para esta fila
            tabla.setValorCelda(1, "Altura", 1.60);

            tabla.setValorCelda(2, "Nombre", "Pedro");
            tabla.setValorCelda(2, "Edad", 20);
            tabla.setValorCelda(2, "Altura", 10.1);  // Altura es null para esta fila

            System.out.println("\nTabla con valores iniciales:");
            tabla.visualizar(5, 3, 10); // Visualizar tabla con datos

            // Eliminar filas con valores "NA" o null en cualquier columna
            tabla.eliminarTodosNAs();
            System.out.println("\nTabla después de eliminar todas las filas que tienen 'NA' o null en cualquier columna:");
            tabla.visualizar(5, 3, 10); // Visualizar tabla después de eliminar todas las filas con NA

        } catch (EtiquetaInvalida | TipoIncompatible e) {
            System.out.println(e.getMessage());
        }// Seleccionar filas y columnas específicas
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
    }
}