package pokesal.critico;

public final class FonteCriticidadeFixa implements FonteCriticidade {

    private final boolean sempreCritico;

    public FonteCriticidadeFixa(final boolean sempreCritico) {
        this.sempreCritico = sempreCritico;
    }

    @Override
    public boolean isCritico() {
        return sempreCritico;
    }
}
