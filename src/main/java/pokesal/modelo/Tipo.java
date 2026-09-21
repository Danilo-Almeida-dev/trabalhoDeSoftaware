package pokesal.modelo;

import pokesal.util.Constantes;

public enum Tipo {

    FOGO,
    AGUA,
    PLANTA;

    public double multiplicadorContra(final Tipo defensor) {
        if (this == defensor) {
            return Constantes.MULTIPLICADOR_NEUTRO_ELEMENTAL;
        }
        final boolean vantagem = (this == FOGO && defensor == PLANTA)
                || (this == AGUA && defensor == FOGO)
                || (this == PLANTA && defensor == AGUA);
        if (vantagem) {
            return Constantes.MULTIPLICADOR_VANTAGEM_ELEMENTAL;
        }
        final boolean desvantagem = (this == FOGO && defensor == AGUA)
                || (this == AGUA && defensor == PLANTA)
                || (this == PLANTA && defensor == FOGO);
        if (desvantagem) {
            return Constantes.MULTIPLICADOR_DESVANTAGEM_ELEMENTAL;
        }
        return Constantes.MULTIPLICADOR_NEUTRO_ELEMENTAL;
    }
}
