import java.util.ArrayList;
import java.util.List;

import Excepciones.TipoIncompatible;

public class Fila {
    private String etiqueta;
    private List<Celda<?>> celdas; // Lista de celdas en la fila

    // Constructor que inicializa la fila con su etiqueta
    public Fila(String etiqueta) {
        this.etiqueta = etiqueta;
        this.celdas = new ArrayList<>();
    }

    // Retorna la etiqueta de la fila
    public String getEtiquetaFila() {
        return etiqueta;
    }

    // Retorna la lista de celdas de la fila
    public List<Celda<?>> getFila() {
        return celdas;
    }

    // Permite modificar el valor de una celda específica en la fila
    public void setValorCelda(int i, Object valor) throws TipoIncompatible {
        if (i < 0 || i >= celdas.size()) {
            throw new IndexOutOfBoundsException("El índice está fuera de rango");
        }

        // Verificamos si el valor de la celda actual y el valor nuevo son del mismo tipo
        if (!celdas.get(i).getValor().getClass().equals(valor.getClass())) {
            throw new TipoIncompatible("El tipo de dato no es compatible con el tipo de la celda");
        }

        // Si los tipos coinciden, actualizamos el valor de la celda
        celdas.get(i).setValor(valor);
    }

    // Método para agregar una celda a la fila
    public void agregarCelda(Celda<?> celda) {
        celdas.add(celda);
    }
}

