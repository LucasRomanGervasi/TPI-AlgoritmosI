import Excepciones.EtiquetaInvalida;
import Excepciones.TipoIncompatible;

public class Main {
    public static void main(String[] args) {
        // Crear una tabla con etiquetas de filas y columnas
        Tabla tabla = new Tabla();
        try {
            tabla.agregarColumna("Nombre", String.class);
            tabla.agregarColumna("Edad", Integer.class);
            tabla.agregarColumna("Altura", Double.class);

            tabla.agregarFila(0);
            tabla.agregarFila(1);
            tabla.agregarFila(2);

            // Agregar valores a las celdas
            tabla.setValorCelda(0, "Nombre", "Juan");
            tabla.setValorCelda(0, "Edad", 16); //probar cambiando valor a String
            tabla.setValorCelda(0, "Altura", 1.75);

            tabla.setValorCelda(1, "Nombre", "Ana");
            tabla.setValorCelda(1, "Edad", 30);
            tabla.setValorCelda(1, "Altura", 1.60);

            tabla.setValorCelda(2, "Nombre", "Pedro");
            tabla.setValorCelda(2, "Edad", 20);
            tabla.setValorCelda(2, "Altura", 1.80);

            // Visualizar la tabla
            tabla.visualizar(3, 3, 10);
        } catch (EtiquetaInvalida | TipoIncompatible e) {
            System.out.println(e.getMessage()); //tiene sentido que el try catch esté en el main?
        }
    }
}

