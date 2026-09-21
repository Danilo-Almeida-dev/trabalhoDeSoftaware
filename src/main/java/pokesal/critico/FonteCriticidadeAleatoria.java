package pokesal.critico;

import java.util.Random;
import pokesal.util.Constantes;

public final class FonteCriticidadeAleatoria implements FonteCriticidade {

    private final Random gerador;

    public FonteCriticidadeAleatoria() {
        this.gerador = new Random();
    }

    public FonteCriticidadeAleatoria(final long semente) {
        this.gerador = new Random(semente);
    }

    @Override
    public boolean isCritico() {
        return gerador.nextDouble() < Constantes.CHANCE_CRITICO;
    }
}
