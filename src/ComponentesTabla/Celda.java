package ComponentesTabla;
public class Celda<T> {
    private T valor;

    public Celda(T valor) {
        this.valor = valor;
    }

    public T getValor() {
        return valor;
    }

   /*  public void setValor(Object valor2) {
        this.valor = (T) valor2;
    } */

    /* public boolean esNA() {
        return valor == null;
    } */

    @Override
    public String toString() {
        return valor != null ? valor.toString() : "null";
    }
}
