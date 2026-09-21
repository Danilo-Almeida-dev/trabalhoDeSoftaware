package pokesal.modelo;

import pokesal.util.Constantes;

public final class PokeSal {

    private final PokeSalEspecie especie;
    private int hpAtual;
    private StatusEfeito status;
    private int contadorEnvenenado;

    public PokeSal(final PokeSalEspecie especie) {
        this.especie = especie;
        this.hpAtual = especie.getHpBase();
        this.status = StatusEfeito.NENHUM;
        this.contadorEnvenenado = 0;
    }

    public PokeSalEspecie getEspecie() {
        return especie;
    }

    public Tipo getTipo() {
        return especie.getTipo();
    }

    public int getHpMaximo() {
        return especie.getHpBase();
    }

    public int getHpAtual() {
        return hpAtual;
    }

    public StatusEfeito getStatus() {
        return status;
    }

    public boolean isDerrotado() {
        return hpAtual <= 0;
    }

    public int getAtkEfetivo() {
        double multiplicador = 1.0;
        if (status == StatusEfeito.QUEIMADO) {
            multiplicador *= Constantes.MULTIPLICADOR_ATK_QUEIMADO;
        }
        if (isBonusDeSobrevivenciaAtivo()) {
            multiplicador *= Constantes.MULTIPLICADOR_ATK_SOBREVIVENCIA;
        }
        return (int) Math.floor(especie.getAtkBase() * multiplicador);
    }

    public int getDefEfetivo() {
        return especie.getDefBase();
    }

    public int getSpdEfetivo() {
        double multiplicador = 1.0;
        if (status == StatusEfeito.PARALISADO) {
            multiplicador *= Constantes.MULTIPLICADOR_SPD_PARALISADO;
        }
        return (int) Math.floor(especie.getSpdBase() * multiplicador);
    }

    // com pouca vida (até 25% do HP máximo) o ATK ganha um empurrãozinho
    public boolean isBonusDeSobrevivenciaAtivo() {
        final double limiteHp = getHpMaximo() * Constantes.LIMIAR_HP_SOBREVIVENCIA;
        return hpAtual > 0 && hpAtual <= limiteHp;
    }

    public void receberDano(final int quantidade) {
        hpAtual = Math.max(0, hpAtual - quantidade);
    }

    public void curar(final int quantidade) {
        hpAtual = Math.min(getHpMaximo(), hpAtual + quantidade);
    }

    public void aplicarStatus(final StatusEfeito novoStatus) {
        this.status = novoStatus;
        this.contadorEnvenenado = (novoStatus == StatusEfeito.ENVENENADO)
                ? Constantes.CONTADOR_INICIAL_ENVENENADO
                : 0;
    }

    public void removerStatus() {
        this.status = StatusEfeito.NENHUM;
        this.contadorEnvenenado = 0;
    }

    public int aplicarDanoDeStatusNoFimDoTurno() {
        if (status == StatusEfeito.QUEIMADO) {
            final int dano = Math.max(1, getHpMaximo() / Constantes.DIVISOR_DANO_QUEIMADO);
            receberDano(dano);
            return dano;
        }
        if (status == StatusEfeito.ENVENENADO) {
            // o veneno piora a cada turno, até bater no teto do contador
            final int dano = (getHpMaximo() * contadorEnvenenado) / Constantes.DIVISOR_DANO_ENVENENADO;
            receberDano(dano);
            if (contadorEnvenenado < Constantes.TETO_CONTADOR_ENVENENADO) {
                contadorEnvenenado++;
            }
            return dano;
        }
        return 0;
    }
}
