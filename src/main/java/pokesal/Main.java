package pokesal;

import pokesal.acao.Acao;
import pokesal.batalha.Batalha;
import pokesal.batalha.CalculadoraDano;
import pokesal.critico.FonteCriticidadeFixa;
import pokesal.modelo.PokeSal;
import pokesal.modelo.PokeSalEspecie;
import pokesal.modelo.StatusEfeito;
import pokesal.modelo.Terreno;
import pokesal.modelo.Treinador;

public final class Main {

    private Main() {
    }

    public static void main(final String[] args) {
        final Treinador treinador1 = new Treinador("Treinador 1", new PokeSal(PokeSalEspecie.CHARSAL));
        final Treinador treinador2 = new Treinador("Treinador 2", new PokeSal(PokeSalEspecie.SQUIRTSAL));

        final CalculadoraDano calculadoraDano = new CalculadoraDano(new FonteCriticidadeFixa(false));
        final Batalha batalha = new Batalha(treinador1, treinador2, Terreno.NEUTRO, calculadoraDano);

        treinador2.getPokeSalAtivo().aplicarStatus(StatusEfeito.QUEIMADO);

        int numeroDoTurno = 1;
        while (!batalha.isEncerrada() && numeroDoTurno <= 10) {
            System.out.println("--- Turno " + numeroDoTurno + " ---");
            batalha.executarTurno(Acao.atacar(treinador1), Acao.atacar(treinador2));
            imprimirEstado(treinador1);
            imprimirEstado(treinador2);
            numeroDoTurno++;
        }

        if (batalha.isEmpateTecnico()) {
            System.out.println("Resultado: empate técnico (duplo KO).");
        } else if (batalha.getVencedor() != null) {
            System.out.println("Resultado: vencedor " + batalha.getVencedor().getNome());
        }
    }

    private static void imprimirEstado(final Treinador treinador) {
        final PokeSal pokeSal = treinador.getPokeSalAtivo();
        System.out.printf(
                "%s (%s): HP %d/%d, status=%s%n",
                treinador.getNome(), pokeSal.getEspecie(),
                pokeSal.getHpAtual(), pokeSal.getHpMaximo(), pokeSal.getStatus());
    }
}
