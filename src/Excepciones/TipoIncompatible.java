package Excepciones;

public class TipoIncompatible extends Exception {
    public TipoIncompatible(String mensaje) {
        super(mensaje); //Esta ecxepcion se lanza cuando se intenta agregar un valor de un tipo incompatible en una celda
    }

}
