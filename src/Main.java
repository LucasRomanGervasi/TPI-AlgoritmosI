import ComponentesTabla.Tabla;
import Excepciones.EtiquetaInvalida;
import Excepciones.TipoIncompatible;


public class Main {
    public static void main(String[] args) {
        try {
            Tabla tabla = new Tabla();

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
            tabla2.getColumna("Nombre").agregarCelda("Sofía");
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

        } catch (TipoIncompatible | EtiquetaInvalida e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
