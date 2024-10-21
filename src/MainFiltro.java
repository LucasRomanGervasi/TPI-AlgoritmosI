import ComponentesTabla.Tabla;
import Excepciones.EtiquetaInvalida;
import Excepciones.TipoIncompatible;

public class MainFiltro {
    public static void main(String[] args) {
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
    
        try {
            Tabla tablaFiltrada = miTabla.filtrar("Edad > 20 and EsEstudiante = false");
            System.out.println("Tabla filtrada:");
            tablaFiltrada.visualizar(5, 5, 30, 0);
        } catch (EtiquetaInvalida | TipoIncompatible | IllegalArgumentException e) {
            System.err.println("Error al filtrar la tabla: " + e.getMessage());
        }
    }
    

}
