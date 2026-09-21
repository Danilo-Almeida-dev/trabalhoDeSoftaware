package pokesal.batalha;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pokesal.acao.Acao;
import pokesal.critico.FonteCriticidadeFixa;
import pokesal.excecao.AcaoInvalidaException;
import pokesal.excecao.LimiteDeItensExcedidoException;
import pokesal.modelo.ItemTipo;
import pokesal.modelo.PokeSal;
import pokesal.modelo.PokeSalEspecie;
import pokesal.modelo.StatusEfeito;
import pokesal.modelo.Terreno;
import pokesal.modelo.Treinador;

class BatalhaTest {

    private static Treinador treinador(final String nome, final PokeSalEspecie especie) {
        return new Treinador(nome, new PokeSal(especie));
    }

    private static Batalha batalha(final Treinador t1, final Treinador t2, final Terreno terreno) {
        return new Batalha(t1, t2, terreno, new CalculadoraDano(new FonteCriticidadeFixa(false)));
    }

    private static void deixarComHp(final Treinador treinador, final int hp) {
        final PokeSal pokeSal = treinador.getPokeSalAtivo();
        pokeSal.receberDano(pokeSal.getHpAtual() - hp);
    }

    private static void envenenadoNoTeto(final Treinador treinador) {
        final PokeSal pokeSal = treinador.getPokeSalAtivo();
        pokeSal.aplicarStatus(StatusEfeito.ENVENENADO);
        for (int i = 0; i < 7; i++) {
            pokeSal.aplicarDanoDeStatusNoFimDoTurno();
        }
        pokeSal.curar(pokeSal.getHpMaximo());
    }

    @Nested
    @DisplayName("RF-04: iniciativa por SPD (ambos com 1 HP: quem age primeiro vence)")
    class Iniciativa {

        @Test
        @DisplayName("Maior SPD age primeiro, mesmo quando é o Treinador 2")
        void maiorSpdAgePrimeiro() {
            final Treinador lento = treinador("T1", PokeSalEspecie.SQUIRTSAL);
            final Treinador rapido = treinador("T2", PokeSalEspecie.CHARSAL);
            deixarComHp(lento, 1);
            deixarComHp(rapido, 1);
            final Batalha batalha = batalha(lento, rapido, Terreno.NEUTRO);

            batalha.executarTurno(Acao.atacar(lento), Acao.atacar(rapido));

            assertSame(rapido, batalha.getVencedor());
            assertEquals(1, rapido.getPokeSalAtivo().getHpAtual());
        }

        @Test
        @DisplayName("A ordem dos argumentos não influencia a iniciativa")
        void ordemDosArgumentosNaoImporta() {
            final Treinador lento = treinador("T1", PokeSalEspecie.SQUIRTSAL);
            final Treinador rapido = treinador("T2", PokeSalEspecie.CHARSAL);
            deixarComHp(lento, 1);
            deixarComHp(rapido, 1);
            final Batalha batalha = batalha(lento, rapido, Terreno.NEUTRO);

            batalha.executarTurno(Acao.atacar(rapido), Acao.atacar(lento));

            assertSame(rapido, batalha.getVencedor());
        }

        @Test
        @DisplayName("Empate de SPD: maior ATK-base age primeiro (TotoSal 65 x SquirtSal 48)")
        void empateDeSpdDesempataPorAtk() {
            final Treinador t1 = treinador("T1", PokeSalEspecie.SQUIRTSAL);
            final Treinador t2 = treinador("T2", PokeSalEspecie.TOTOSAL);
            deixarComHp(t1, 1);
            deixarComHp(t2, 1);
            final Batalha batalha = batalha(t1, t2, Terreno.NEUTRO);

            batalha.executarTurno(Acao.atacar(t1), Acao.atacar(t2));

            assertSame(t2, batalha.getVencedor());
        }

        @Test
        @DisplayName("Empate total: o Treinador 1 age primeiro")
        void empateTotalFavoreceTreinador1() {
            final Treinador t1 = treinador("T1", PokeSalEspecie.CHARSAL);
            final Treinador t2 = treinador("T2", PokeSalEspecie.CYNDASAL);
            deixarComHp(t1, 1);
            deixarComHp(t2, 1);
            final Batalha batalha = batalha(t1, t2, Terreno.NEUTRO);

            batalha.executarTurno(Acao.atacar(t2), Acao.atacar(t1));

            assertSame(t1, batalha.getVencedor());
        }

        @Test
        @DisplayName("A iniciativa usa o SPD efetivo: Paralisado perde a vez")
        void usaSpdEfetivo() {
            final Treinador paralisado = treinador("T1", PokeSalEspecie.CHARSAL);
            final Treinador normal = treinador("T2", PokeSalEspecie.SQUIRTSAL);
            paralisado.getPokeSalAtivo().aplicarStatus(StatusEfeito.PARALISADO);
            deixarComHp(paralisado, 1);
            deixarComHp(normal, 1);
            final Batalha batalha = batalha(paralisado, normal, Terreno.NEUTRO);

            batalha.executarTurno(Acao.atacar(paralisado), Acao.atacar(normal));

            assertSame(normal, batalha.getVencedor());
        }

        @Test
        @DisplayName("Usar item entra no mesmo fluxo de iniciativa que o ataque")
        void itemParticipaDaIniciativa() {
            final Treinador lento = treinador("T1", PokeSalEspecie.SQUIRTSAL);
            final Treinador rapido = treinador("T2", PokeSalEspecie.CHARSAL);
            deixarComHp(lento, 1);
            final Batalha batalha = batalha(lento, rapido, Terreno.NEUTRO);

            batalha.executarTurno(Acao.usarItem(lento, ItemTipo.POTION), Acao.atacar(rapido));

            assertTrue(lento.getPokeSalAtivo().isDerrotado());
            assertEquals(2, lento.getEstoque(ItemTipo.POTION));
        }
    }

    @Nested
    @DisplayName("RF-08: fim da batalha")
    class FimDaBatalha {

        @Test
        @DisplayName("Derrota simples: o outro treinador vence")
        void derrotaSimples() {
            final Treinador t1 = treinador("T1", PokeSalEspecie.CHARSAL);
            final Treinador t2 = treinador("T2", PokeSalEspecie.SQUIRTSAL);
            deixarComHp(t2, 1);
            final Batalha batalha = batalha(t1, t2, Terreno.NEUTRO);

            batalha.executarTurno(Acao.atacar(t1), Acao.atacar(t2));

            assertTrue(batalha.isEncerrada());
            assertFalse(batalha.isEmpateTecnico());
            assertSame(t1, batalha.getVencedor());
        }

        @Test
        @DisplayName("Ações do PokéSal derrotado não são executadas")
        void derrotadoNaoAge() {
            final Treinador t1 = treinador("T1", PokeSalEspecie.CHARSAL);
            final Treinador t2 = treinador("T2", PokeSalEspecie.SQUIRTSAL);
            deixarComHp(t2, 1);
            final Batalha batalha = batalha(t1, t2, Terreno.NEUTRO);

            batalha.executarTurno(Acao.atacar(t1), Acao.atacar(t2));

            assertEquals(39, t1.getPokeSalAtivo().getHpAtual());
        }

        @Test
        @DisplayName("Descanso de PokéSal derrotado não é executado nem consome o uso")
        void descansoDeDerrotadoNaoConsomeUso() {
            final Treinador t1 = treinador("T1", PokeSalEspecie.SQUIRTSAL);
            final Treinador t2 = treinador("T2", PokeSalEspecie.SQUIRTSAL);
            deixarComHp(t1, 0);
            final Batalha batalha = batalha(t1, t2, Terreno.NEUTRO);

            batalha.executarTurno(Acao.descansar(t1), Acao.atacar(t2));

            assertFalse(t1.jaDescansou());
            assertEquals(0, t1.getPokeSalAtivo().getHpAtual());
            assertSame(t2, batalha.getVencedor());
        }

        @Test
        @DisplayName("Duplo KO no fim do turno termina em empate técnico, sem vencedor")
        void duploKoEmpateTecnico() {
            final Treinador t1 = treinador("T1", PokeSalEspecie.SQUIRTSAL);
            final Treinador t2 = treinador("T2", PokeSalEspecie.SQUIRTSAL);
            envenenadoNoTeto(t1);
            envenenadoNoTeto(t2);
            deixarComHp(t1, 18);
            deixarComHp(t2, 18);
            final Batalha batalha = batalha(t1, t2, Terreno.NEUTRO);

            batalha.executarTurno(Acao.descansar(t1), Acao.descansar(t2));

            assertTrue(batalha.isEncerrada());
            assertTrue(batalha.isEmpateTecnico());
            assertNull(batalha.getVencedor());
        }

        @Test
        @DisplayName("Duplo KO: nenhum dos dois recebe a cura do Canteiro Central")
        void duploKoNaoRecebeCuraDoTerreno() {
            final Treinador t1 = treinador("T1", PokeSalEspecie.SQUIRTSAL);
            final Treinador t2 = treinador("T2", PokeSalEspecie.SQUIRTSAL);
            envenenadoNoTeto(t1);
            envenenadoNoTeto(t2);
            deixarComHp(t1, 18);
            deixarComHp(t2, 18);
            final Batalha batalha = batalha(t1, t2, Terreno.CANTEIRO_CENTRAL);

            batalha.executarTurno(Acao.descansar(t1), Acao.descansar(t2));

            assertEquals(0, t1.getPokeSalAtivo().getHpAtual());
            assertEquals(0, t2.getPokeSalAtivo().getHpAtual());
        }

        @Test
        @DisplayName("Derrotado por veneno não é revivido pela cura do terreno")
        void derrotadoPorStatusNaoRecebeCura() {
            final Treinador t1 = treinador("T1", PokeSalEspecie.SQUIRTSAL);
            final Treinador t2 = treinador("T2", PokeSalEspecie.SQUIRTSAL);
            envenenadoNoTeto(t1);
            deixarComHp(t1, 18);
            final Batalha batalha = batalha(t1, t2, Terreno.CANTEIRO_CENTRAL);

            batalha.executarTurno(Acao.descansar(t1), Acao.descansar(t2));

            assertEquals(0, t1.getPokeSalAtivo().getHpAtual());
            assertSame(t2, batalha.getVencedor());
        }

        @Test
        @DisplayName("Executar turno depois do fim da batalha lança exceção")
        void turnoAposEncerramento() {
            final Treinador t1 = treinador("T1", PokeSalEspecie.CHARSAL);
            final Treinador t2 = treinador("T2", PokeSalEspecie.SQUIRTSAL);
            deixarComHp(t2, 1);
            final Batalha batalha = batalha(t1, t2, Terreno.NEUTRO);
            batalha.executarTurno(Acao.atacar(t1), Acao.atacar(t2));

            assertThrows(AcaoInvalidaException.class,
                    () -> batalha.executarTurno(Acao.atacar(t1), Acao.atacar(t2)));
        }
    }

    @Nested
    @DisplayName("RF-05: fim de turno e Canteiro Central")
    class FimDeTurno {

        @Test
        @DisplayName("Canteiro Central cura 5% do HP máximo (descanso 4 + terreno 2)")
        void canteiroCentralCura() {
            final Treinador t1 = treinador("T1", PokeSalEspecie.SQUIRTSAL);
            final Treinador t2 = treinador("T2", PokeSalEspecie.SQUIRTSAL);
            deixarComHp(t1, 20);
            deixarComHp(t2, 20);
            final Batalha batalha = batalha(t1, t2, Terreno.CANTEIRO_CENTRAL);

            batalha.executarTurno(Acao.descansar(t1), Acao.descansar(t2));

            assertEquals(26, t1.getPokeSalAtivo().getHpAtual());
            assertEquals(26, t2.getPokeSalAtivo().getHpAtual());
        }

        @Test
        @DisplayName("Terreno neutro não cura no fim do turno")
        void terrenoNeutroNaoCura() {
            final Treinador t1 = treinador("T1", PokeSalEspecie.SQUIRTSAL);
            final Treinador t2 = treinador("T2", PokeSalEspecie.SQUIRTSAL);
            deixarComHp(t1, 20);
            deixarComHp(t2, 20);
            final Batalha batalha = batalha(t1, t2, Terreno.NEUTRO);

            batalha.executarTurno(Acao.descansar(t1), Acao.descansar(t2));

            assertEquals(24, t1.getPokeSalAtivo().getHpAtual());
            assertEquals(24, t2.getPokeSalAtivo().getHpAtual());
        }

        @Test
        @DisplayName("Dano de Queimado é aplicado ao fim do turno")
        void queimadoNoFimDoTurno() {
            final Treinador t1 = treinador("T1", PokeSalEspecie.SQUIRTSAL);
            final Treinador t2 = treinador("T2", PokeSalEspecie.SQUIRTSAL);
            deixarComHp(t1, 20);
            t1.getPokeSalAtivo().aplicarStatus(StatusEfeito.QUEIMADO);
            final Batalha batalha = batalha(t1, t2, Terreno.NEUTRO);

            batalha.executarTurno(Acao.descansar(t1), Acao.descansar(t2));

            assertEquals(22, t1.getPokeSalAtivo().getHpAtual());
        }

        @Test
        @DisplayName("Crítico não altera o dano causado por status")
        void criticoNaoAfetaStatus() {
            final Treinador t1 = treinador("T1", PokeSalEspecie.SQUIRTSAL);
            final Treinador t2 = treinador("T2", PokeSalEspecie.SQUIRTSAL);
            deixarComHp(t1, 20);
            t1.getPokeSalAtivo().aplicarStatus(StatusEfeito.QUEIMADO);
            final Batalha batalha = new Batalha(t1, t2, Terreno.NEUTRO,
                    new CalculadoraDano(new FonteCriticidadeFixa(true)));

            batalha.executarTurno(Acao.descansar(t1), Acao.descansar(t2));

            assertEquals(22, t1.getPokeSalAtivo().getHpAtual());
        }
    }

    @Nested
    @DisplayName("RA-02: descanso")
    class Descanso {

        @Test
        @DisplayName("Recupera 10% do HP máximo (4 de 44)")
        void curaDezPorCento() {
            final Treinador t1 = treinador("T1", PokeSalEspecie.SQUIRTSAL);
            final Treinador t2 = treinador("T2", PokeSalEspecie.SQUIRTSAL);
            deixarComHp(t1, 20);
            final Batalha batalha = batalha(t1, t2, Terreno.NEUTRO);

            batalha.executarTurno(Acao.descansar(t1), Acao.descansar(t2));

            assertEquals(24, t1.getPokeSalAtivo().getHpAtual());
            assertTrue(t1.jaDescansou());
        }

        @Test
        @DisplayName("HP nunca ultrapassa o máximo")
        void naoUltrapassaMaximo() {
            final Treinador t1 = treinador("T1", PokeSalEspecie.SQUIRTSAL);
            final Treinador t2 = treinador("T2", PokeSalEspecie.SQUIRTSAL);
            deixarComHp(t1, 43);
            final Batalha batalha = batalha(t1, t2, Terreno.NEUTRO);

            batalha.executarTurno(Acao.descansar(t1), Acao.descansar(t2));

            assertEquals(44, t1.getPokeSalAtivo().getHpAtual());
        }

        @Test
        @DisplayName("Descansar com HP cheio ainda consome a única utilização")
        void hpCheioConsomeUso() {
            final Treinador t1 = treinador("T1", PokeSalEspecie.SQUIRTSAL);
            final Treinador t2 = treinador("T2", PokeSalEspecie.SQUIRTSAL);
            final Batalha batalha = batalha(t1, t2, Terreno.NEUTRO);

            batalha.executarTurno(Acao.descansar(t1), Acao.atacar(t2));

            assertTrue(t1.jaDescansou());
            assertThrows(AcaoInvalidaException.class,
                    () -> batalha.executarTurno(Acao.descansar(t1), Acao.atacar(t2)));
        }

        @Test
        @DisplayName("O segundo descanso é rejeitado e não altera o estado")
        void segundoDescansoRejeitado() {
            final Treinador t1 = treinador("T1", PokeSalEspecie.SQUIRTSAL);
            final Treinador t2 = treinador("T2", PokeSalEspecie.SQUIRTSAL);
            deixarComHp(t1, 20);
            final Batalha batalha = batalha(t1, t2, Terreno.NEUTRO);
            batalha.executarTurno(Acao.descansar(t1), Acao.atacar(t2));
            final int hpAntes = t1.getPokeSalAtivo().getHpAtual();

            assertThrows(AcaoInvalidaException.class,
                    () -> batalha.executarTurno(Acao.descansar(t1), Acao.atacar(t2)));

            assertEquals(hpAntes, t1.getPokeSalAtivo().getHpAtual());
            assertFalse(batalha.isEncerrada());
        }

        @Test
        @DisplayName("O controle é por treinador: os dois podem descansar uma vez cada")
        void controlePorTreinador() {
            final Treinador t1 = treinador("T1", PokeSalEspecie.SQUIRTSAL);
            final Treinador t2 = treinador("T2", PokeSalEspecie.SQUIRTSAL);
            final Batalha batalha = batalha(t1, t2, Terreno.NEUTRO);

            batalha.executarTurno(Acao.descansar(t1), Acao.descansar(t2));

            assertTrue(t1.jaDescansou());
            assertTrue(t2.jaDescansou());
        }
    }

    @Nested
    @DisplayName("RF-07: itens")
    class Itens {

        @Test
        @DisplayName("Potion cura 20 HP")
        void potionCura20() {
            final Treinador t1 = treinador("T1", PokeSalEspecie.SQUIRTSAL);
            final Treinador t2 = treinador("T2", PokeSalEspecie.SQUIRTSAL);
            deixarComHp(t1, 10);
            final Batalha batalha = batalha(t1, t2, Terreno.NEUTRO);

            batalha.executarTurno(Acao.usarItem(t1, ItemTipo.POTION), Acao.descansar(t2));

            assertEquals(30, t1.getPokeSalAtivo().getHpAtual());
            assertEquals(1, t1.getEstoque(ItemTipo.POTION));
            assertEquals(1, t1.getItensUsados());
        }

        @Test
        @DisplayName("Potion não ultrapassa o HP máximo")
        void potionLimitadaAoMaximo() {
            final Treinador t1 = treinador("T1", PokeSalEspecie.SQUIRTSAL);
            final Treinador t2 = treinador("T2", PokeSalEspecie.SQUIRTSAL);
            deixarComHp(t1, 30);
            final Batalha batalha = batalha(t1, t2, Terreno.NEUTRO);

            batalha.executarTurno(Acao.usarItem(t1, ItemTipo.POTION), Acao.descansar(t2));

            assertEquals(44, t1.getPokeSalAtivo().getHpAtual());
        }

        @Test
        @DisplayName("Super Potion cura 50 HP, limitado ao máximo")
        void superPotion() {
            final Treinador t1 = treinador("T1", PokeSalEspecie.SQUIRTSAL);
            final Treinador t2 = treinador("T2", PokeSalEspecie.SQUIRTSAL);
            deixarComHp(t1, 1);
            final Batalha batalha = batalha(t1, t2, Terreno.NEUTRO);

            batalha.executarTurno(Acao.usarItem(t1, ItemTipo.SUPER_POTION), Acao.descansar(t2));

            assertEquals(44, t1.getPokeSalAtivo().getHpAtual());
            assertEquals(0, t1.getEstoque(ItemTipo.SUPER_POTION));
        }

        @Test
        @DisplayName("Antidote remove o status e não recupera HP")
        void antidoteRemoveStatus() {
            final Treinador t1 = treinador("T1", PokeSalEspecie.SQUIRTSAL);
            final Treinador t2 = treinador("T2", PokeSalEspecie.SQUIRTSAL);
            deixarComHp(t1, 20);
            t1.getPokeSalAtivo().aplicarStatus(StatusEfeito.QUEIMADO);
            final Batalha batalha = batalha(t1, t2, Terreno.NEUTRO);

            batalha.executarTurno(Acao.usarItem(t1, ItemTipo.ANTIDOTE), Acao.descansar(t2));

            assertEquals(StatusEfeito.NENHUM, t1.getPokeSalAtivo().getStatus());
            assertEquals(20, t1.getPokeSalAtivo().getHpAtual());
        }

        @Test
        @DisplayName("Potion não remove status")
        void potionNaoRemoveStatus() {
            final Treinador t1 = treinador("T1", PokeSalEspecie.SQUIRTSAL);
            final Treinador t2 = treinador("T2", PokeSalEspecie.SQUIRTSAL);
            deixarComHp(t1, 20);
            t1.getPokeSalAtivo().aplicarStatus(StatusEfeito.QUEIMADO);
            final Batalha batalha = batalha(t1, t2, Terreno.NEUTRO);

            batalha.executarTurno(Acao.usarItem(t1, ItemTipo.POTION), Acao.descansar(t2));

            assertEquals(StatusEfeito.QUEIMADO, t1.getPokeSalAtivo().getStatus());
        }

        @Test
        @DisplayName("Sem estoque: lança exceção e não consome uso")
        void semEstoque() {
            final Treinador t1 = treinador("T1", PokeSalEspecie.SQUIRTSAL);
            final Treinador t2 = treinador("T2", PokeSalEspecie.SQUIRTSAL);
            deixarComHp(t1, 20);
            final Batalha batalha = batalha(t1, t2, Terreno.NEUTRO);
            batalha.executarTurno(Acao.usarItem(t1, ItemTipo.SUPER_POTION), Acao.atacar(t2));
            final int hpAntes = t1.getPokeSalAtivo().getHpAtual();

            final AcaoInvalidaException erro = assertThrows(AcaoInvalidaException.class,
                    () -> batalha.executarTurno(Acao.usarItem(t1, ItemTipo.SUPER_POTION), Acao.atacar(t2)));

            assertFalse(erro instanceof LimiteDeItensExcedidoException);
            assertEquals(1, t1.getItensUsados());
            assertEquals(hpAntes, t1.getPokeSalAtivo().getHpAtual());
        }

        @Test
        @DisplayName("Item sem efeito possível: lança exceção e não consome estoque nem uso")
        void itemSemEfeito() {
            final Treinador t1 = treinador("T1", PokeSalEspecie.SQUIRTSAL);
            final Treinador t2 = treinador("T2", PokeSalEspecie.SQUIRTSAL);
            final Batalha batalha = batalha(t1, t2, Terreno.NEUTRO);

            assertThrows(AcaoInvalidaException.class,
                    () -> batalha.executarTurno(Acao.usarItem(t1, ItemTipo.POTION), Acao.atacar(t2)));
            assertThrows(AcaoInvalidaException.class,
                    () -> batalha.executarTurno(Acao.usarItem(t1, ItemTipo.ANTIDOTE), Acao.atacar(t2)));

            assertEquals(2, t1.getEstoque(ItemTipo.POTION));
            assertEquals(1, t1.getEstoque(ItemTipo.ANTIDOTE));
            assertEquals(0, t1.getItensUsados());
        }

        @Test
        @DisplayName("O terceiro uso de item lança LimiteDeItensExcedidoException e o contador fica em 2")
        void terceiroUsoLancaExcecaoEspecifica() {
            final Treinador t1 = treinador("T1", PokeSalEspecie.SQUIRTSAL);
            final Treinador t2 = treinador("T2", PokeSalEspecie.SQUIRTSAL);
            deixarComHp(t1, 10);
            final Batalha batalha = batalha(t1, t2, Terreno.NEUTRO);
            batalha.executarTurno(Acao.usarItem(t1, ItemTipo.POTION), Acao.descansar(t2));
            batalha.executarTurno(Acao.usarItem(t1, ItemTipo.POTION), Acao.atacar(t2));

            assertThrows(LimiteDeItensExcedidoException.class,
                    () -> batalha.executarTurno(Acao.usarItem(t1, ItemTipo.SUPER_POTION), Acao.atacar(t2)));

            assertEquals(2, t1.getItensUsados());
            assertEquals(1, t1.getEstoque(ItemTipo.SUPER_POTION));
        }
    }
}
