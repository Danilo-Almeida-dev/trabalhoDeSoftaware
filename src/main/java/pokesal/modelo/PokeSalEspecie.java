package pokesal.modelo;

public enum PokeSalEspecie {

    BULBASAL(Tipo.PLANTA, 45, 49, 49, 45),
    CHARSAL(Tipo.FOGO, 39, 52, 43, 65),
    SQUIRTSAL(Tipo.AGUA, 44, 48, 65, 43),
    CHIKOSAL(Tipo.PLANTA, 45, 49, 65, 45),
    CYNDASAL(Tipo.FOGO, 39, 52, 43, 65),
    TOTOSAL(Tipo.AGUA, 50, 65, 64, 43);

    private final Tipo tipo;
    private final int hpBase;
    private final int atkBase;
    private final int defBase;
    private final int spdBase;

    PokeSalEspecie(final Tipo tipo, final int hpBase, final int atkBase,
                   final int defBase, final int spdBase) {
        this.tipo = tipo;
        this.hpBase = hpBase;
        this.atkBase = atkBase;
        this.defBase = defBase;
        this.spdBase = spdBase;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public int getHpBase() {
        return hpBase;
    }

    public int getAtkBase() {
        return atkBase;
    }

    public int getDefBase() {
        return defBase;
    }

    public int getSpdBase() {
        return spdBase;
    }
}
