import ComponentesTabla.Tabla;
import ComponentesTabla.Tabla.Operacion;
import Excepciones.EtiquetaInvalida;
import Excepciones.TipoIncompatible;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws TipoIncompatible, EtiquetaInvalida {
            System.out.println("Pruebas de formatos de carga:");

            System.out.println("Carga desde una matriz de objetos:");
            Object[][] datos = {
                {"Edad", "EsEstudiante"}, // Nombres de columnas
                {17, true},
                {30, false},
                {22, true}
            };
            // Crear la tabla desde una matriz de objetos
            Tabla TablaMatriz = new Tabla(datos);
            TablaMatriz.visualizar(5, 5, 15, 0);

            System.out.println(System.lineSeparator());
            System.out.println(System.lineSeparator());

            System.out.println("Carga desde un archivo CSV:");
            Tabla tablaCSV = new Tabla("C:\\Users\\lucas\\OneDrive\\Escritorio\\dato (2).csv", false);
            tablaCSV.visualizar(5, 5, 15, 0);
            Class n = tablaCSV.getTipoDatoColumna("Columna2");
            System.out.println(n);

            System.out.println(System.lineSeparator());
            System.out.println(System.lineSeparator());

            System.out.println("Carga desde una secuencia lineal:");
            List<Object> secuenciaLineal = new ArrayList<>();

            // Agregar filas a la secuencia
            secuenciaLineal.add(Arrays.asList(10, null, 30));      // Fila 1
            secuenciaLineal.add(Arrays.asList(40, 50, 60));      // Fila 2
            secuenciaLineal.add(Arrays.asList(70, 80, 90));      // Fila 3
            
            // Crear la tabla usando el constructor con secuencia lineal
            Tabla tablaSLineal = new Tabla(secuenciaLineal);
            tablaSLineal.visualizar(5, 5, 15, 0);

        
            System.out.println(System.lineSeparator());
            System.out.println(System.lineSeparator());
        
            System.out.println("Pruebas de manipulación de tablas:");       
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

            System.out.println(System.lineSeparator());
            System.out.println(System.lineSeparator());
            // Modificar un valor específico (reemplazar el nombre "Lucas" con "Lucía")
            tabla.setValorCelda(0, "Nombre", "Lucia");

            // Visualizar la tabla después de la modificación
            System.out.println("Tabla después de modificar el valor de la primera fila:");
            tabla.visualizar(5, 5, 10, 0);

            System.out.println(System.lineSeparator());
            System.out.println(System.lineSeparator());


            // Eliminar una columna (por ejemplo, "Altura")
            tabla.eliminarColumna("Altura");

            // Visualizar la tabla después de eliminar la columna "Altura"
            System.out.println("Tabla después de eliminar la columna 'Altura':");
            tabla.visualizar(5, 5, 10, 0);

            System.out.println(System.lineSeparator());
            System.out.println(System.lineSeparator());


            // Eliminar una fila (por ejemplo, la segunda fila con índice 1)
            tabla.eliminarFila(1);

            // Visualizar la tabla después de eliminar la fila
            System.out.println("Tabla después de eliminar la segunda fila:");
            tabla.visualizar(5, 5, 10, 0);

            System.out.println(System.lineSeparator());
            System.out.println(System.lineSeparator());


            // Mostrar solo las primeras 2 filas (head)
            System.out.println("Visualización de la primera fila:");
            tabla.head(1);

            System.out.println(System.lineSeparator());
            System.out.println(System.lineSeparator());


            // Mostrar solo las últimas 2 filas (tail)
            System.out.println("Visualización de la última fila:");
            tabla.tail(1);

            System.out.println(System.lineSeparator());
            System.out.println(System.lineSeparator());

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

            System.out.println(System.lineSeparator());

            System.out.println("Tabla 2:");
            tabla2.visualizar(3, 3, 10, 0);

            System.out.println(System.lineSeparator());

            // Concatenar las tablas
            Tabla tablaConcatenada = tabla1.concatenar(tabla2); 
            System.out.println("Tabla Concatenada:");
            tablaConcatenada.visualizar(10, 3, 10, 0);

            System.out.println(System.lineSeparator());
            System.out.println(System.lineSeparator());

            // Prueba copia profunda
            System.out.println("Tabla copia profunda:");
            Tabla tablaCopia = tabla1.hacerCopiaProfunda(tabla1);
            tablaCopia.visualizar(3, 3, 10, 0);

            System.out.println(System.lineSeparator());
            System.out.println(System.lineSeparator());

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

            System.out.println(System.lineSeparator());
            System.out.println(System.lineSeparator());

            int fila = miTabla.getCantidadFilas();
            System.out.println("Cantidad de filas: " + fila);
            miTabla.visualizar(5, 5, 15, 0);

            System.out.println(System.lineSeparator());
            System.out.println(System.lineSeparator());

            // Lista de etiquetas de columnas a ordenar
            Object[][] datosTablaO = {
                {"Nombre", "Edad", "Altura"}, // Nombres de columnas
                {"Lucas", 25, 1.75},
                {"Ana", 30, 1.75},
                {"Juan", 30, 1.65}
            };

            Tabla tablaOrdenar = new Tabla(datosTablaO);

            List<String> etiquetas = new ArrayList<>();
            etiquetas.add("Edad");
            etiquetas.add("Altura");

            // Crear una instancia de la clase que contiene el método ordenar (o puede ser un método estático)
            Tabla tabla3 = tablaOrdenar.ordenar(tablaOrdenar, etiquetas, true); // true para ascendente

            // Visualizar la tabla ordenada (puedes cambiar la implementación de visualizar según lo que necesites)
            tabla3.visualizar(5, 5, 10, 0);

            System.out.println(System.lineSeparator());
            System.out.println(System.lineSeparator());

            System.out.println("Muestreo aleatorio del 33% filas: (una sola fila porque son 3)");
            tabla3.muestrear(33, 15);

            System.out.println(System.lineSeparator());
            System.out.println(System.lineSeparator());

            System.out.println("Tabla para probar la eliminacion de Na");
            Object[][] datosNA = {
                {"Nombre", "Edad", 1}, // Nombres de columnas
                {"Lucas", 20, true},
                {"NA", null, false},
                {"Ana", 30, false},
                {null, 40, null}
            };
            
            // Crear la tabla
            Tabla tablaNA = new Tabla(datosNA);

            // Mostrar la tabla antes de eliminar los NA
            System.out.println("Tabla antes de eliminar NAs:");
            tablaNA.visualizar(5, 5, 10, 0);
            
            // Llamar al método para eliminar los NAs
            tablaNA.eliminarTodosNAs();
            
            // Mostrar la tabla después de eliminar los NA
            System.out.println("\nTabla después de eliminar NAs:");
            tablaNA.visualizar(5, 5, 10, 0);
            
            Class N = tablaNA.getTipoDatoColumna("Edad");
            System.out.println(N);

            /* List<Object> etiquetas1 = tablaNA.getEtiquetasFilas();
            System.out.println(etiquetas1); */

            System.out.println(System.lineSeparator());
            System.out.println(System.lineSeparator());

            System.out.println("Pruebas de agrupar");
            Object[][] datosVentas = {
                {"Región", "Producto", "Ventas"}, // Etiquetas de columnas
                {"Norte", "A", 100},
                {"Norte", "B", 200},
                {"Sur", "A", 150},
                {"Sur", "B", 250},
                {"Este", "A", 300},
                {"Este", "B", 400}};

            Tabla tablaVentas = new Tabla(datosVentas);
            List<String> columnasAgrupamiento = Arrays.asList("Producto");
            Operacion operacion = Operacion.SUMA;

            // 4. Llamar al método agregarPor para agrupar y calcular la suma de ventas por región
            Tabla tablaAgrupada = tablaVentas.agregarPor(columnasAgrupamiento, operacion);
            tablaAgrupada.visualizar(5, 5, 10, 0);

    }
}

