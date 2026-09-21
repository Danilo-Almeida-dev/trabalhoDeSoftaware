package pokesal.modelo;

import pokesal.util.Constantes;

public enum Terreno {

    NEUTRO,

    POCA_DE_CHUVA,

    ASFALTO_QUENTE,

    CANTEIRO_CENTRAL;

    public double multiplicadorParaAtaque(final Tipo tipoDoAtaque) {
        if (this == POCA_DE_CHUVA && tipoDoAtaque == Tipo.AGUA) {
            return 1.0 + Constantes.BONUS_DANO_AGUA_TERRENO_CHUVA;
        }
        if (this == ASFALTO_QUENTE && tipoDoAtaque == Tipo.FOGO) {
            return 1.0 + Constantes.BONUS_DANO_FOGO_ASFALTO_QUENTE;
        }
        return 1.0;
    }

    public double percentualCuraFimDeTurno() {
        return this == CANTEIRO_CENTRAL ? Constantes.PERCENTUAL_CURA_CANTEIRO_CENTRAL : 0.0;
    }
}
