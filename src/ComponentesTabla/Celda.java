package ComponentesTabla;
public class Celda<T> {
    private T valor;

    public Celda(T valor) {
        this.valor = valor;
    }

    public T getValor() {
        return valor;
    }

    @Override
    public String toString() { //Al igual que con columna, se uso para debuggear principalmente
        return valor != null ? valor.toString() : "null";
    }
}
