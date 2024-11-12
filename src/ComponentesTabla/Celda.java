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
    public String toString() {
        return valor != null ? valor.toString() : "null";
    }
}
