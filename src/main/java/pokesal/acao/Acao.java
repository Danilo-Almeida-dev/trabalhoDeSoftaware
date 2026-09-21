package pokesal.acao;

import pokesal.modelo.ItemTipo;
import pokesal.modelo.Treinador;

public final class Acao {

    private final Treinador autor;
    private final TipoAcao tipoAcao;
    private final ItemTipo item;

    private Acao(final Treinador autor, final TipoAcao tipoAcao, final ItemTipo item) {
        this.autor = autor;
        this.tipoAcao = tipoAcao;
        this.item = item;
    }

    public static Acao atacar(final Treinador autor) {
        return new Acao(autor, TipoAcao.ATACAR, null);
    }

    public static Acao usarItem(final Treinador autor, final ItemTipo item) {
        return new Acao(autor, TipoAcao.USAR_ITEM, item);
    }

    public static Acao descansar(final Treinador autor) {
        return new Acao(autor, TipoAcao.DESCANSAR, null);
    }

    public Treinador getAutor() {
        return autor;
    }

    public TipoAcao getTipoAcao() {
        return tipoAcao;
    }

    public ItemTipo getItem() {
        return item;
    }
}
