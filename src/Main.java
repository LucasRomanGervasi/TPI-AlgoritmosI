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
            tabla.eliminarColumna("Edad");
            System.out.println("\nTabla después de eliminar la columna 'Edad':");
            tabla.visualizar(5, 2, 10); // Ahora solo dos columnas visibles

            // Eliminar una fila y visualizar la tabla modificada
            tabla.eliminarFila(1);
            System.out.println("\nTabla después de eliminar la fila con ID 1 (Ana):");
            tabla.visualizar(5, 2, 10); // Visualizar con 2 columnas y 2 filas

            // Modificar un valor y visualizar la tabla modificada
            tabla.setValorCelda(0, "Edad", 26); // Cambiar la edad de Juan
            System.out.println("\nTabla después de modificar la edad de Juan:");
            tabla.visualizar(5, 2, 10); // Visualizar con cambios

        } catch (EtiquetaInvalida | TipoIncompatible e) {
            System.out.println(e.getMessage());
        }
    }
}


