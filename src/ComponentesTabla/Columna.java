package ComponentesTabla;
import java.util.ArrayList;
import java.util.List;
import Excepciones.TipoIncompatible;

public class Columna<T> {
    private String etiqueta;
    private List<Celda<T>> celdas;
    private Class<T> tipoDato;

    public Columna(String etiqueta, Class<T> tipoDato) throws TipoIncompatible {
        this.etiqueta = etiqueta;
        this.celdas = new ArrayList<>();
        this.tipoDato = tipoDato;
        
        // Verificar que el tipo de dato sea válido (String, Number o Boolean)
        if (!esTipoValido(tipoDato)) {
            throw new TipoIncompatible("El tipo de dato no es permitido. Solo se permiten String, Number o Boolean.");
        }
    }

    private boolean esTipoValido(Class<T> tipo) {
        return tipo == String.class || tipo == Integer.class  || tipo == Boolean.class || tipo == null || tipo == Double.class;
    }

    public T getValor(int index) {
        return celdas.get(index).getValor();
    }

    public String getEtiquetaColumna() {
        return etiqueta;
    }

    public Class<T> getTipoDato() {
        return tipoDato;
    }

    public void agregarCelda(Object valor) {
        @SuppressWarnings("unchecked")
        Celda<T> nuevaCelda = new Celda<>((T) valor);
        celdas.add(nuevaCelda);
    }
    
    @SuppressWarnings("unchecked")
    public void setValor(int index, Object valor) throws TipoIncompatible {
        if (valor != null && !tipoDato.isInstance(valor)) {
            throw new TipoIncompatible("El tipo de dato no es compatible con el tipo de la columna.");
        }
        Celda<T> nuevaCelda = new Celda<>((T) valor);
        celdas.set(index, nuevaCelda);
    }

    public List<Celda<T>> getCeldas() {
        return celdas;
    }

    public void eliminarValor(int index) {
        celdas.remove(index);
    }

    public String toString() { //Lo usamos principalmente para debuggear, pero creimos util dejarlo
        StringBuilder sb = new StringBuilder();
        sb.append("Columna{etiqueta='").append(etiqueta).append("', datos=[");
        for (Celda<T> celda : celdas) {
            sb.append(celda.toString()).append(", ");
        }
        if (celdas.size() > 0) {
            sb.setLength(sb.length() - 2);
        }
        sb.append("]}");
        return sb.toString();
    }
}


