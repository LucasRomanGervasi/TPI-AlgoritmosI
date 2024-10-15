import java.util.ArrayList;
import java.util.List;

import Excepciones.TipoIncompatible;

public class Columna<T> {
    private String etiqueta;
    private List<Celda<T>> celdas;
    private Class<T> tipoDato;

    public Columna(String etiqueta, Class<T> tipoDato) {
        this.etiqueta = etiqueta;
        this.celdas = new ArrayList<>();
        this.tipoDato = tipoDato;
    }

    public String getEtiquetaColumna() {
        return etiqueta; //retorna la etiqueta de la columna
    }

    public Class<T> getTipoDato() {
        return tipoDato; //retorna el tipo de dato de la columna
    }

    public void agregarCelda(T valor) throws TipoIncompatible {
        // Verificamos si el valor es compatible con el tipo T usando instanceof
        if (celdas.size() > 0) { // Asegura que haya una celda para comparar
            T tipoReferencia = celdas.get(0).getValor(); // Obtenemos el valor de la primera celda como referencia
            if (!tipoReferencia.getClass().isInstance(valor)) {
                throw new TipoIncompatible("El tipo de dato no es compatible con el tipo de la columna.");
            }
        }
        Celda<T> nuevaCelda = new Celda<>(valor); // crea una nueva celda con el valor
        celdas.add(nuevaCelda); // agrega la celda a la lista de celdas
    }

    public List<Object> getColumna() {
        List<Object> datosConEtiqueta = new ArrayList<>();
        datosConEtiqueta.add(etiqueta); // Agrega la etiqueta al principio

        for (Celda<T> celda : celdas) {
            datosConEtiqueta.add(celda.getValor()); // Agrega cada valor de la celda
        }

        return datosConEtiqueta; // Retorna la lista con etiqueta y valores
    }

}

