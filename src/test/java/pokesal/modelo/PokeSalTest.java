package pokesal.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PokeSalTest {

    private static PokeSal comHp(final PokeSalEspecie especie, final int hp) {
        final PokeSal pokeSal = new PokeSal(especie);
        pokeSal.receberDano(pokeSal.getHpMaximo() - hp);
        return pokeSal;
    }

    @Test
    @DisplayName("Começa com HP cheio e sem status")
    void estadoInicial() {
        final PokeSal pokeSal = new PokeSal(PokeSalEspecie.CHARSAL);
        assertEquals(39, pokeSal.getHpAtual());
        assertEquals(StatusEfeito.NENHUM, pokeSal.getStatus());
        assertFalse(pokeSal.isDerrotado());
    }

    @Nested
    @DisplayName("RF-06: dano e cura")
    class DanoECura {

        @Test
        void danoReduzHp() {
            final PokeSal pokeSal = new PokeSal(PokeSalEspecie.CHARSAL);
            pokeSal.receberDano(10);
            assertEquals(29, pokeSal.getHpAtual());
        }

        @Test
        @DisplayName("HP nunca fica abaixo de zero")
        void hpNaoFicaNegativo() {
            final PokeSal pokeSal = new PokeSal(PokeSalEspecie.CHARSAL);
            pokeSal.receberDano(1000);
            assertEquals(0, pokeSal.getHpAtual());
            assertTrue(pokeSal.isDerrotado());
        }

        @Test
        @DisplayName("Cura nunca ultrapassa o HP máximo")
        void curaLimitadaAoHpMaximo() {
            final PokeSal pokeSal = comHp(PokeSalEspecie.CHARSAL, 30);
            pokeSal.curar(100);
            assertEquals(39, pokeSal.getHpAtual());
        }

        @Test
        void curaSomaQuandoNaoHaExcesso() {
            final PokeSal pokeSal = comHp(PokeSalEspecie.CHARSAL, 10);
            pokeSal.curar(5);
            assertEquals(15, pokeSal.getHpAtual());
        }
    }

    @Nested
    @DisplayName("RA-03: bônus de sobrevivência (SquirtSal, HP máx. 44, limite 11)")
    class BonusDeSobrevivencia {

        @Test
        @DisplayName("Ativa exatamente em 25% do HP")
        void ativaEmExatamente25Porcento() {
            assertTrue(comHp(PokeSalEspecie.SQUIRTSAL, 11).isBonusDeSobrevivenciaAtivo());
        }

        @Test
        @DisplayName("Ativa abaixo de 25%")
        void ativaAbaixoDe25Porcento() {
            assertTrue(comHp(PokeSalEspecie.SQUIRTSAL, 10).isBonusDeSobrevivenciaAtivo());
            assertTrue(comHp(PokeSalEspecie.SQUIRTSAL, 1).isBonusDeSobrevivenciaAtivo());
        }

        @Test
        @DisplayName("Fica inativo acima de 25%")
        void inativoAcimaDe25Porcento() {
            assertFalse(comHp(PokeSalEspecie.SQUIRTSAL, 12).isBonusDeSobrevivenciaAtivo());
            assertFalse(new PokeSal(PokeSalEspecie.SQUIRTSAL).isBonusDeSobrevivenciaAtivo());
        }

        @Test
        @DisplayName("Não se aplica com 0 HP")
        void inativoComZeroHp() {
            assertFalse(comHp(PokeSalEspecie.SQUIRTSAL, 0).isBonusDeSobrevivenciaAtivo());
        }

        @Test
        @DisplayName("Reavalia imediatamente após dano e após cura")
        void reavaliaAposDanoECura() {
            final PokeSal pokeSal = comHp(PokeSalEspecie.SQUIRTSAL, 12);
            assertEquals(48, pokeSal.getAtkEfetivo());

            pokeSal.receberDano(1);
            assertEquals(57, pokeSal.getAtkEfetivo());

            pokeSal.curar(1);
            assertEquals(48, pokeSal.getAtkEfetivo());
        }

        @Test
        @DisplayName("Multiplica o ATK efetivo por 1,20 sem alterar o ATK base")
        void multiplicaAtkSemAlterarBase() {
            final PokeSal pokeSal = comHp(PokeSalEspecie.SQUIRTSAL, 5);
            assertEquals(57, pokeSal.getAtkEfetivo());
            assertEquals(48, pokeSal.getEspecie().getAtkBase());
        }

        @Test
        @DisplayName("Combina com Queimado e arredonda uma única vez (BulbaSal: 49 x 0,5 x 1,2 = 29)")
        void combinaComQueimadoComUmUnicoArredondamento() {
            final PokeSal pokeSal = comHp(PokeSalEspecie.BULBASAL, 11);
            pokeSal.aplicarStatus(StatusEfeito.QUEIMADO);
            assertEquals(29, pokeSal.getAtkEfetivo());
        }
    }

    @Nested
    @DisplayName("RF-05: status")
    class Status {

        @Test
        @DisplayName("Queimado: ATK efetivo x 0,5")
        void queimadoReduzAtk() {
            final PokeSal pokeSal = new PokeSal(PokeSalEspecie.CHARSAL);
            pokeSal.aplicarStatus(StatusEfeito.QUEIMADO);
            assertEquals(26, pokeSal.getAtkEfetivo());
            assertEquals(52, pokeSal.getEspecie().getAtkBase());
        }

        @Test
        @DisplayName("Queimado: perde HP máx / 16 por turno")
        void queimadoCausaDanoPorTurno() {
            final PokeSal pokeSal = new PokeSal(PokeSalEspecie.SQUIRTSAL);
            pokeSal.aplicarStatus(StatusEfeito.QUEIMADO);
            assertEquals(2, pokeSal.aplicarDanoDeStatusNoFimDoTurno());
            assertEquals(42, pokeSal.getHpAtual());
        }

        @Test
        @DisplayName("Envenenado: dano progressivo HP máx x n / 16, com teto em n = 8")
        void envenenadoEProgressivoComTeto() {
            final PokeSal pokeSal = new PokeSal(PokeSalEspecie.SQUIRTSAL);
            pokeSal.aplicarStatus(StatusEfeito.ENVENENADO);

            final int[] esperado = {2, 5, 8, 11, 13, 16, 19, 22, 22, 22};
            for (final int dano : esperado) {
                assertEquals(dano, pokeSal.aplicarDanoDeStatusNoFimDoTurno());
            }
        }

        @Test
        @DisplayName("Envenenado: reaplicar o status reinicia o contador")
        void envenenadoReiniciaContador() {
            final PokeSal pokeSal = new PokeSal(PokeSalEspecie.SQUIRTSAL);
            pokeSal.aplicarStatus(StatusEfeito.ENVENENADO);
            pokeSal.aplicarDanoDeStatusNoFimDoTurno();
            pokeSal.aplicarDanoDeStatusNoFimDoTurno();

            pokeSal.aplicarStatus(StatusEfeito.ENVENENADO);
            assertEquals(2, pokeSal.aplicarDanoDeStatusNoFimDoTurno());
        }

        @Test
        @DisplayName("Paralisado: SPD efetivo x 0,5")
        void paralisadoReduzSpd() {
            final PokeSal pokeSal = new PokeSal(PokeSalEspecie.CHARSAL);
            pokeSal.aplicarStatus(StatusEfeito.PARALISADO);
            assertEquals(32, pokeSal.getSpdEfetivo());
            assertEquals(65, pokeSal.getEspecie().getSpdBase());
        }

        @Test
        @DisplayName("Paralisado não causa dano no fim do turno")
        void paralisadoNaoCausaDano() {
            final PokeSal pokeSal = new PokeSal(PokeSalEspecie.CHARSAL);
            pokeSal.aplicarStatus(StatusEfeito.PARALISADO);
            assertEquals(0, pokeSal.aplicarDanoDeStatusNoFimDoTurno());
            assertEquals(39, pokeSal.getHpAtual());
        }

        @Test
        @DisplayName("Sem status não há dano nem alteração de atributos")
        void semStatusNaoAltera() {
            final PokeSal pokeSal = new PokeSal(PokeSalEspecie.CHARSAL);
            assertEquals(0, pokeSal.aplicarDanoDeStatusNoFimDoTurno());
            assertEquals(52, pokeSal.getAtkEfetivo());
            assertEquals(65, pokeSal.getSpdEfetivo());
        }

        @Test
        @DisplayName("Só um status por vez: o novo substitui o anterior")
        void novoStatusSubstituiOAnterior() {
            final PokeSal pokeSal = new PokeSal(PokeSalEspecie.CHARSAL);
            pokeSal.aplicarStatus(StatusEfeito.QUEIMADO);
            pokeSal.aplicarStatus(StatusEfeito.PARALISADO);

            assertEquals(StatusEfeito.PARALISADO, pokeSal.getStatus());
            assertEquals(52, pokeSal.getAtkEfetivo());
            assertEquals(32, pokeSal.getSpdEfetivo());
        }

        @Test
        void removerStatusLimpaEfeitos() {
            final PokeSal pokeSal = new PokeSal(PokeSalEspecie.CHARSAL);
            pokeSal.aplicarStatus(StatusEfeito.QUEIMADO);
            pokeSal.removerStatus();

            assertEquals(StatusEfeito.NENHUM, pokeSal.getStatus());
            assertEquals(52, pokeSal.getAtkEfetivo());
            assertEquals(0, pokeSal.aplicarDanoDeStatusNoFimDoTurno());
        }
    }
}
