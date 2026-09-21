package pokesal.modelo;

import pokesal.util.Constantes;

public enum ItemTipo {

    POTION(Constantes.CURA_POTION, false),

    SUPER_POTION(Constantes.CURA_SUPER_POTION, false),

    ANTIDOTE(0, true);

    private final int curaFixa;
    private final boolean removeStatus;

    ItemTipo(final int curaFixa, final boolean removeStatus) {
        this.curaFixa = curaFixa;
        this.removeStatus = removeStatus;
    }

    public int getCuraFixa() {
        return curaFixa;
    }

    public boolean isRemoveStatus() {
        return removeStatus;
    }

    public boolean temEfeitoPossivelSobre(final PokeSal alvo) {
        if (removeStatus) {
            return alvo.getStatus() != StatusEfeito.NENHUM;
        }
        return alvo.getHpAtual() < alvo.getHpMaximo();
    }
}
