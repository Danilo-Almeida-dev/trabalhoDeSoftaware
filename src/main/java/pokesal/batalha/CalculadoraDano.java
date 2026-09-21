package pokesal.batalha;

import pokesal.critico.FonteCriticidade;
import pokesal.modelo.PokeSal;
import pokesal.modelo.Terreno;
import pokesal.util.Constantes;

public final class CalculadoraDano {

    private final FonteCriticidade fonteCriticidade;

    public CalculadoraDano(final FonteCriticidade fonteCriticidade) {
        this.fonteCriticidade = fonteCriticidade;
    }

    public ResultadoAtaque calcularDano(final PokeSal atacante, final PokeSal defensor,
                                         final Terreno terreno) {
        // dano base = ATK - DEF/2, nunca negativo
        final int danoBase = Math.max(0,
                atacante.getAtkEfetivo() - (defensor.getDefEfetivo() / Constantes.DIVISOR_DEFESA_DANO_BASE));

        final double multiplicadorElemental = atacante.getTipo().multiplicadorContra(defensor.getTipo());
        final double multiplicadorTerreno = terreno.multiplicadorParaAtaque(atacante.getTipo());

        final boolean critico = fonteCriticidade.isCritico();
        final double multiplicadorCritico = critico ? Constantes.MULTIPLICADOR_CRITICO : 1.0;

        final double danoComModificadores =
                danoBase * multiplicadorElemental * multiplicadorTerreno * multiplicadorCritico;

        // arredonda pra baixo só no final, mas garante o dano mínimo
        final int danoComFloor = (int) Math.floor(danoComModificadores);
        final int danoFinal = Math.max(Constantes.DANO_MINIMO, danoComFloor);

        return new ResultadoAtaque(danoFinal, critico);
    }
}
