import java.util.ArrayList;
import java.util.List;

public class Columna<T> {
    private String etiqueta;
    private List<Celda<T>> celdas;

    public Columna(String etiqueta) {
        this.etiqueta = etiqueta;
        this.celdas = new ArrayList<>();
    }

    public String getEtiquetaColumna() {
        return etiqueta; //retorna la etiqueta de la columna
    }

    public void agregarCelda(T valor) {
        Celda<T> nuevaCelda = new Celda<>(valor); //crea una nueva celda con el valor recibido
        celdas.add(nuevaCelda); //agrega la celda a la lista de celdas
    }

    public List<Celda<T>> getColumna() {
        return celdas; //retorna las celdas de la columna
    }


}
