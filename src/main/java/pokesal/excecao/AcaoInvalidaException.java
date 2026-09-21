package pokesal.excecao;

public class AcaoInvalidaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AcaoInvalidaException(final String mensagem) {
        super(mensagem);
    }
}
