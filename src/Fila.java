import java.util.ArrayList;
import java.util.List;

import Excepciones.TipoIncompatible;

public class Fila {
    private int id; // ID que identifica la fila
    private List<Celda<?>> celdas; // Lista de celdas en la fila

    // Constructor que inicializa la fila con su ID
    public Fila(int id) {
        this.id = id;
        this.celdas = new ArrayList<>();
    }

    // Retorna el ID de la fila
    public int getID() {
        return id;
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

        Celda<?> celda = celdas.get(i);
        if (!celda.getValor().getClass().isInstance(valor)) {
            throw new TipoIncompatible("El tipo de dato no es compatible con la celda.");
        }

        celda.setValor(valor);
    }

    // Método para agregar una celda a la fila
    public void agregarCelda(Celda<?> celda) {
        celdas.add(celda);
    }

    // Método para visualizar la fila, incluyendo su ID al principio
    public void visualizarFila() {
        System.out.print("ID: " + id + " | "); // Imprimir el ID de la fila
        for (Celda<?> celda : celdas) {
            Object valor = celda.getValor();
            System.out.print((valor != null ? valor.toString() : "NA") + " | ");
        }
        System.out.println();
    }
}


