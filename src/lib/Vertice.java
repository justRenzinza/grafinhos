package lib;

// essa aqui é uma classe genérica chamada Vertice, onde T pode ser qualquer tipo de dado.
public class Vertice<T> {

    // aqui a gente declara um atributo privado chamado valor, que vai ser do tipo T (pode ser qualquer tipo)
    private T valor; 

    // esse é o construtor da classe, ele recebe um parâmetro e atribui ao atributo valor
    public Vertice(T valor) {
        this.valor = valor;
    }

    // pega o atrivuto de valor
    public T getValor() {
        return valor;
    }

    // e esse aqui é pra modificar o valor do atributo valor
    public void setValor(T valor) {
        this.valor = valor;
    }

}
