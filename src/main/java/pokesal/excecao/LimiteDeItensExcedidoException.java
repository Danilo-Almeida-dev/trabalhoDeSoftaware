package pokesal.excecao;

public class LimiteDeItensExcedidoException extends AcaoInvalidaException {

    private static final long serialVersionUID = 1L;

    public LimiteDeItensExcedidoException(final String mensagem) {
        super(mensagem);
    }
}
