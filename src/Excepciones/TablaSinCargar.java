package Excepciones;

public class TablaSinCargar extends Exception {
    public TablaSinCargar(String mensaje) {
        super(mensaje);
    }
}
