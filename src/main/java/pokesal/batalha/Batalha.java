package pokesal.batalha;

import java.util.ArrayList;
import java.util.List;
import pokesal.acao.Acao;
import pokesal.excecao.AcaoInvalidaException;
import pokesal.excecao.LimiteDeItensExcedidoException;
import pokesal.modelo.ItemTipo;
import pokesal.modelo.PokeSal;
import pokesal.modelo.StatusEfeito;
import pokesal.modelo.Terreno;
import pokesal.modelo.Treinador;
import pokesal.util.Constantes;

public final class Batalha {

    private final Treinador treinador1;
    private final Treinador treinador2;
    private final Terreno terrenoAtivo;
    private final CalculadoraDano calculadoraDano;

    private boolean encerrada;
    private boolean empateTecnico;
    private Treinador vencedor;

    public Batalha(final Treinador treinador1, final Treinador treinador2,
                    final Terreno terrenoAtivo, final CalculadoraDano calculadoraDano) {
        this.treinador1 = treinador1;
        this.treinador2 = treinador2;
        this.terrenoAtivo = terrenoAtivo;
        this.calculadoraDano = calculadoraDano;
        this.encerrada = false;
        this.empateTecnico = false;
        this.vencedor = null;
    }

    public boolean isEncerrada() {
        return encerrada;
    }

    public boolean isEmpateTecnico() {
        return empateTecnico;
    }

    public Treinador getVencedor() {
        return vencedor;
    }

    public void executarTurno(final Acao acaoDeUmTreinador, final Acao acaoDoOutroTreinador) {
        if (encerrada) {
            throw new AcaoInvalidaException("A batalha já foi encerrada.");
        }
        for (final Acao acao : ordenarPorIniciativa(acaoDeUmTreinador, acaoDoOutroTreinador)) {
            if (encerrada) {
                break;
            }
            if (acao.getAutor().getPokeSalAtivo().isDerrotado()) {
                // quem já caiu antes de agir perde a vez
                continue;
            }
            executarAcao(acao);
        }
        if (!encerrada) {
            processarFimDeTurno();
        }
    }

    // quem tem mais SPD age primeiro; empatou, desempata por ATK base e depois pelo treinador 1
    private List<Acao> ordenarPorIniciativa(final Acao acaoA, final Acao acaoB) {
        final List<Acao> acoes = new ArrayList<>(List.of(acaoA, acaoB));
        acoes.sort((a1, a2) -> {
            final PokeSal p1 = a1.getAutor().getPokeSalAtivo();
            final PokeSal p2 = a2.getAutor().getPokeSalAtivo();

            final int comparacaoSpd = Integer.compare(p2.getSpdEfetivo(), p1.getSpdEfetivo());
            if (comparacaoSpd != 0) {
                return comparacaoSpd;
            }
            final int comparacaoAtk = Integer.compare(
                    p2.getEspecie().getAtkBase(), p1.getEspecie().getAtkBase());
            if (comparacaoAtk != 0) {
                return comparacaoAtk;
            }

            return a1.getAutor() == treinador1 ? -1 : 1;
        });
        return acoes;
    }

    private void executarAcao(final Acao acao) {
        switch (acao.getTipoAcao()) {
            case ATACAR -> executarAtaque(acao);
            case USAR_ITEM -> executarUsoDeItem(acao);
            case DESCANSAR -> executarDescanso(acao);
            default -> throw new AcaoInvalidaException("Tipo de ação desconhecido.");
        }
    }

    private void executarAtaque(final Acao acao) {
        final Treinador atacante = acao.getAutor();
        final Treinador defensorTreinador = outroTreinador(atacante);
        final PokeSal pokeAtacante = atacante.getPokeSalAtivo();
        final PokeSal pokeDefensor = defensorTreinador.getPokeSalAtivo();

        if (pokeDefensor.isDerrotado()) {
            // alvo já caiu, não tem o que atacar
            return;
        }
        final ResultadoAtaque resultado =
                calculadoraDano.calcularDano(pokeAtacante, pokeDefensor, terrenoAtivo);
        pokeDefensor.receberDano(resultado.dano());
    }

    private void executarUsoDeItem(final Acao acao) {
        final Treinador treinador = acao.getAutor();
        final ItemTipo item = acao.getItem();
        final PokeSal pokeSal = treinador.getPokeSalAtivo();

        if (treinador.atingiuLimiteDeItens()) {
            throw new LimiteDeItensExcedidoException(
                    "Treinador " + treinador.getNome() + " já usou o máximo de "
                            + Constantes.MAXIMO_ITENS_POR_TREINADOR + " itens nesta batalha.");
        }
        if (treinador.getEstoque(item) <= 0) {
            throw new AcaoInvalidaException("Sem estoque de " + item + " para " + treinador.getNome() + ".");
        }
        if (!item.temEfeitoPossivelSobre(pokeSal)) {
            throw new AcaoInvalidaException(item + " não teria efeito sobre o PokéSal de "
                    + treinador.getNome() + ".");
        }

        if (item.isRemoveStatus()) {
            pokeSal.removerStatus();
        } else {
            pokeSal.curar(item.getCuraFixa());
        }
        treinador.registrarUsoDeItem(item);
    }

    private void executarDescanso(final Acao acao) {
        final Treinador treinador = acao.getAutor();
        final PokeSal pokeSal = treinador.getPokeSalAtivo();

        if (pokeSal.isDerrotado()) {
            throw new AcaoInvalidaException("PokéSal derrotado não pode descansar.");
        }
        if (treinador.jaDescansou()) {
            throw new AcaoInvalidaException(
                    "Treinador " + treinador.getNome() + " já descansou nesta batalha.");
        }

        pokeSal.curar(curaPercentualComMinimo(pokeSal, Constantes.PERCENTUAL_CURA_DESCANSO));
        treinador.registrarDescanso();
    }

    private void processarFimDeTurno() {
        final PokeSal poke1 = treinador1.getPokeSalAtivo();
        final PokeSal poke2 = treinador2.getPokeSalAtivo();

        // ordem do fim de turno: dano de status -> checa derrota -> cura do terreno
        poke1.aplicarDanoDeStatusNoFimDoTurno();
        poke2.aplicarDanoDeStatusNoFimDoTurno();

        final boolean poke1Derrotado = poke1.isDerrotado();
        final boolean poke2Derrotado = poke2.isDerrotado();

        // os dois caíram no mesmo turno = empate técnico
        if (poke1Derrotado && poke2Derrotado) {
            encerrada = true;
            empateTecnico = true;
            vencedor = null;
            return;
        }
        if (poke1Derrotado) {
            encerrada = true;
            vencedor = treinador2;
        } else if (poke2Derrotado) {
            encerrada = true;
            vencedor = treinador1;
        }

        final double percentualCura = terrenoAtivo.percentualCuraFimDeTurno();
        if (percentualCura > 0) {
            if (!poke1.isDerrotado()) {
                poke1.curar(curaPercentualComMinimo(poke1, percentualCura));
            }
            if (!poke2.isDerrotado()) {
                poke2.curar(curaPercentualComMinimo(poke2, percentualCura));
            }
        }
    }

    private int curaPercentualComMinimo(final PokeSal pokeSal, final double percentualDeCura) {
        final int curaBruta = (int) Math.floor(pokeSal.getHpMaximo() * percentualDeCura);
        final boolean ferido = pokeSal.getHpAtual() < pokeSal.getHpMaximo();
        return (ferido && curaBruta < Constantes.CURA_MINIMA) ? Constantes.CURA_MINIMA : curaBruta;
    }

    private Treinador outroTreinador(final Treinador treinador) {
        return treinador == treinador1 ? treinador2 : treinador1;
    }
}
