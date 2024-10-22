import ComponentesTabla.Tabla;
import Excepciones.EtiquetaInvalida;
import Excepciones.TipoIncompatible;

public class Main {
    public static void main(String[] args) throws TipoIncompatible, EtiquetaInvalida {
            // Datos iniciales: nombres de columnas y filas de datos
            Object[][] datosTabla = {
                {"Nombre", "Edad", "Altura"}, // Nombres de columnas
                {"Lucas", 25, 1.80},
                {"Ana", 30, 1.65},
                {"Juan", 28, 1.75}
            };

            // Crear la tabla desde una matriz de objetos
            Tabla tabla = new Tabla(datosTabla);

            // Visualizar la tabla inicial
            System.out.println("Tabla inicial con columnas y filas:");
            tabla.visualizar(5, 5, 10, 0);


            // Modificar un valor específico (reemplazar el nombre "Lucas" con "Lucía")
            tabla.setValorCelda(0, "Nombre", "Lucia");

            // Visualizar la tabla después de la modificación
            System.out.println("Tabla después de modificar el valor de la primera fila:");
            tabla.visualizar(5, 5, 10, 0);


            // Eliminar una columna (por ejemplo, "Altura")
            tabla.eliminarColumna("Altura");

            // Visualizar la tabla después de eliminar la columna "Altura"
            System.out.println("Tabla después de eliminar la columna 'Altura':");
            tabla.visualizar(5, 5, 10, 0);


            // Eliminar una fila (por ejemplo, la segunda fila con índice 1)
            tabla.eliminarFila(1);

            // Visualizar la tabla después de eliminar la fila
            System.out.println("Tabla después de eliminar la segunda fila:");
            tabla.visualizar(5, 5, 10, 0);


            // Mostrar solo las primeras 2 filas (head)
            System.out.println("Visualización de la primera fila:");
            tabla.head(1);


            // Mostrar solo las últimas 2 filas (tail)
            System.out.println("Visualización de la última fila:");
            tabla.tail(1);

            // Concatenación de tablas
            Object[][] datosTabla1 = {
                {"Nombre", "Edad", "Activo"}, // Nombres de columnas
                {"Lucas", 25, true},
                {"María", 30, false}
            };

            Object[][] datosTabla2 = {
                {"Nombre", "Edad", "Activo"}, // Nombres de columnas (igual a la primera tabla)
                {"Sofia", 26, true},
                {"Juan", 28, false}
            };

            // Crear las tablas desde matrices de objetos
            Tabla tabla1 = new Tabla(datosTabla1);
            Tabla tabla2 = new Tabla(datosTabla2);

            System.out.println("Tabla 1:");
            tabla1.visualizar(3, 3, 10, 0);

            System.out.println("Tabla 2:");
            tabla2.visualizar(3, 3, 10, 0);

            // Concatenar las tablas
            Tabla tablaConcatenada = tabla1.concatenar(tabla2); 
            System.out.println("Tabla Concatenada:");
            tablaConcatenada.visualizar(10, 3, 10, 0);

            // Prueba copia profunda
            System.out.println("Tabla copia:");
            Tabla tablaCopia = tabla1.hacerCopiaProfunda(tabla1);
            tablaCopia.visualizar(3, 3, 10, 0);

            // Datos filtrados
            Object[][] datosFiltrado = {
                {"Edad", "EsEstudiante"}, // Nombres de columnas
                {25, true},
                {30, false},
                {22, true}
            };

            // Crear la tabla desde una matriz de objetos
            Tabla miTabla = new Tabla(datosFiltrado);
            System.out.println("Tabla filtrada:");

            // Filtrar filas donde Edad > 23 y EsEstudiante es true
            Tabla tablaFiltrada = miTabla.filtrar("Edad > 23 and EsEstudiante = true");
            tablaFiltrada.visualizar(5, 5, 30, 0);

            int fila = miTabla.getCantidadFilas();
            System.out.println("Cantidad de filas: " + fila);
            miTabla.visualizar(5, 5, 15, 0);

    }
}

