package ComponentesTabla;
import java.util.ArrayList;
import java.util.List;
import Excepciones.TipoIncompatible;

public class Columna<T> {
    private String etiqueta;
    private List<Celda<T>> celdas;
    private Class<T> tipoDato;

    // Constructor que inicializa la etiqueta y el tipo de dato
    public Columna(String etiqueta, Class<T> tipoDato) throws TipoIncompatible {
        this.etiqueta = etiqueta;
        this.celdas = new ArrayList<>();
        this.tipoDato = tipoDato;
        
        // Verificar que el tipo de dato sea válido (String, Number o Boolean)
        if (!esTipoValido(tipoDato)) {
            throw new TipoIncompatible("El tipo de dato no es permitido. Solo se permiten String, Number o Boolean.");
        }
    }

    // Método para verificar si el tipo es válido
    private boolean esTipoValido(Class<T> tipo) {
        return tipo == String.class || tipo == Integer.class  || tipo == Boolean.class || tipo == null || tipo == Double.class;
    }

    public T getValor(int index) {
        return celdas.get(index).getValor();
    }

    // Obtener la etiqueta de la columna
    public String getEtiquetaColumna() {
        return etiqueta;
    }

    // Obtener el tipo de dato de la columna
    public Class<T> getTipoDato() {
        return tipoDato;
    }

    public void agregarCelda(Object valor) {
        Celda<T> nuevaCelda = new Celda<>((T) valor); // Crea una nueva celda con el valor
        celdas.add(nuevaCelda); // Agrega la celda a la lista de celdas
    }
    

    // Establecer un valor en un índice específico de la columna
    public void setValor(int index, Object valor) throws TipoIncompatible {
        if (valor != null && !tipoDato.isInstance(valor)) {
            throw new TipoIncompatible("El tipo de dato no es compatible con el tipo de la columna.");
        }
        Celda<T> nuevaCelda = new Celda<>((T) valor); // Crea una nueva instancia de Celda
        celdas.set(index, nuevaCelda);
    }

    public void agregarValor(T valor) {
        Celda<T> nuevaCelda = new Celda<>(valor); // Crear una nueva instancia de Celda con el valor
        celdas.add(nuevaCelda);  // Agregar la celda a la lista de celdas
    }

    // Obtener todos los valores de la columna
    public List<Celda<T>> getCeldas() {
        return celdas;
    }

    // Eliminar un valor en un índice específico
    public void eliminarValor(int index) {
        celdas.remove(index);
    }

    // Obtener el tamaño de la columna (cantidad de valores)
    /* public int getTamanio() {
        return celdas.size();
    } */

    // Completar la columna con valores null hasta un tamaño específico
    /* public void rellenarConNull(int nuevoTamanio) {
        while (celdas.size() < nuevoTamanio) {
            celdas.add(null);
        }
    } */

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Columna{etiqueta='").append(etiqueta).append("', datos=[");
        for (Celda<T> celda : celdas) {
            sb.append(celda.toString()).append(", "); // Asumiendo que `Celda` tiene un método `toString`.
        }
        // Eliminar la última coma y espacio
        if (celdas.size() > 0) {
            sb.setLength(sb.length() - 2);
        }
        sb.append("]}");
        return sb.toString();
    }
}


