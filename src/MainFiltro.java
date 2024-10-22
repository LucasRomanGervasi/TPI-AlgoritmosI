import java.util.ArrayList;
import java.util.List;

import ComponentesTabla.Tabla;
import Excepciones.EtiquetaInvalida;
import Excepciones.TipoIncompatible;

public class MainFiltro {
    public static void main(String[] args) throws TipoIncompatible, EtiquetaInvalida {
        // Datos de prueba
        Object[][] datosFiltrado = {
            {"Edad", "EsEstudiante"}, // Nombres de columnas
            {25, true},
            {30, false},
            {22, true}
        };
    
        // Crear la tabla desde una matriz de objetos
        Tabla miTabla = new Tabla(datosFiltrado);
        System.out.println("Tabla original:");
        miTabla.visualizar(5, 5, 15, 0);
    
        /* try {
            Tabla tablaFiltrada = miTabla.filtrar("Edad > 20 and EsEstudiante = false");
            System.out.println("Tabla filtrada:");
            tablaFiltrada.visualizar(5, 5, 30, 0);
        } catch (EtiquetaInvalida | TipoIncompatible | IllegalArgumentException e) {
            System.err.println("Error al filtrar la tabla: " + e.getMessage());
        } */


        Tabla tablaCSV = new Tabla("C:/Users/lucas/OneDrive/Escritorio/dato (2).csv");
        tablaCSV.visualizar(5, 5, 15, 0);
        /* try {
            Class tipodato = tablaCSV.getTipoDatoColumna("Columna1");
            System.out.println("El tipo de dato de la columna es: " + tipodato.getName());
        } catch (EtiquetaInvalida e) {
            e.printStackTrace();
        } */
        /* tablaCSV.agregarFila();
        tablaCSV.visualizar(5, 5, 15, 0);
        try {
            tablaCSV.setValorCelda(5, "Columna1", "a");
            //tablaCSV.setValorCelda(5, "Columna2", "a");
            //tablaCSV.setValorCelda(5, "Columna3", "a");
            tablaCSV.visualizar(6, 5, 15, 0);
        } catch (TipoIncompatible e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (EtiquetaInvalida e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } */

        /* try {
            List<Object> filas = tablaCSV.getFila(0);
            System.out.println("Fila 0: " + filas);
        } catch (EtiquetaInvalida e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } */

        /* try {
            System.out.println("Celda: " + tablaCSV.getCelda(0, "Columna1"));
        } catch (EtiquetaInvalida e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } */

        /* tablaCSV.agregarColumna("Columna4", Boolean.class);
        for (int i = 0; i < tablaCSV.getCantidadFilas(); i++) {
            try {
                tablaCSV.setValorCelda(i, "Columna4", true);
            } catch (TipoIncompatible | EtiquetaInvalida e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        tablaCSV.visualizar(5, 5, 15, 0); */

        Object[][] datos = {
                {"Columna1", "Columna2", "Columna3"},
                {5, "b", 7},
                {3, "a", 9},
                {8, "c", 2},
                {1, "d", 4}
            };

            // Crear la tabla a partir de la matriz
            Tabla tabla = new Tabla(datos);
            //tabla.visualizar(5, 5, 10, 0);
            //System.out.println("tipodato: " + tabla.getTipoDatoColumna("Columna1"));

            // Lista de etiquetas de columnas a ordenar
            List<String> etiquetas = new ArrayList<>();
            etiquetas.add("Columna2");
            etiquetas.add("Columna3");

            // Crear una instancia de la clase que contiene el método ordenar (o puede ser un método estático)
            Tabla tabla2 = tabla.ordenar(tabla, etiquetas, true); // true para ascendente

            // Visualizar la tabla ordenada (puedes cambiar la implementación de visualizar según lo que necesites)
            tabla2.visualizar(5, 5, 10, 0);

        
    }
    

}
