package pokesal.batalha;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pokesal.critico.FonteCriticidadeFixa;
import pokesal.modelo.PokeSal;
import pokesal.modelo.PokeSalEspecie;
import pokesal.modelo.StatusEfeito;
import pokesal.modelo.Terreno;

class CalculadoraDanoTest {

    private static final CalculadoraDano SEM_CRITICO = new CalculadoraDano(new FonteCriticidadeFixa(false));
    private static final CalculadoraDano COM_CRITICO = new CalculadoraDano(new FonteCriticidadeFixa(true));

    private static PokeSal novo(final PokeSalEspecie especie) {
        return new PokeSal(especie);
    }

    @Test
    @DisplayName("Dano-base = ATK - DEF/2 (CharSal 52 contra DEF 43 = 31)")
    void danoBase() {
        final ResultadoAtaque resultado = SEM_CRITICO.calcularDano(
                novo(PokeSalEspecie.CHARSAL), novo(PokeSalEspecie.CHARSAL), Terreno.NEUTRO);

        assertEquals(31, resultado.dano());
        assertFalse(resultado.critico());
    }

    @Nested
    @DisplayName("RF-02: multiplicador elemental")
    class Elemental {

        @Test
        @DisplayName("Vantagem dobra o dano: Fogo contra Planta = 28 x 2")
        void vantagem() {
            assertEquals(56, SEM_CRITICO.calcularDano(
                    novo(PokeSalEspecie.CHARSAL), novo(PokeSalEspecie.BULBASAL), Terreno.NEUTRO).dano());
        }

        @Test
        @DisplayName("Desvantagem reduz à metade: Fogo contra Água = 20 x 0,5")
        void desvantagem() {
            assertEquals(10, SEM_CRITICO.calcularDano(
                    novo(PokeSalEspecie.CHARSAL), novo(PokeSalEspecie.SQUIRTSAL), Terreno.NEUTRO).dano());
        }

        @Test
        @DisplayName("Mesmo tipo é neutro")
        void mesmoTipoEhNeutro() {
            assertEquals(31, SEM_CRITICO.calcularDano(
                    novo(PokeSalEspecie.CHARSAL), novo(PokeSalEspecie.CYNDASAL), Terreno.NEUTRO).dano());
        }
    }

    @Nested
    @DisplayName("RF-03: terrenos")
    class Terrenos {

        @Test
        @DisplayName("Poça de Chuva: Água causa +10% (16 x 1,1 = 17)")
        void pocaDeChuva() {
            assertEquals(17, SEM_CRITICO.calcularDano(
                    novo(PokeSalEspecie.SQUIRTSAL), novo(PokeSalEspecie.SQUIRTSAL), Terreno.POCA_DE_CHUVA).dano());
        }

        @Test
        @DisplayName("Asfalto Quente: Fogo causa +15% (31 x 1,15 = 35)")
        void asfaltoQuente() {
            assertEquals(35, SEM_CRITICO.calcularDano(
                    novo(PokeSalEspecie.CHARSAL), novo(PokeSalEspecie.CHARSAL), Terreno.ASFALTO_QUENTE).dano());
        }

        @Test
        @DisplayName("Terreno não beneficia o tipo errado")
        void terrenoNaoBeneficiaTipoErrado() {
            assertEquals(31, SEM_CRITICO.calcularDano(
                    novo(PokeSalEspecie.CHARSAL), novo(PokeSalEspecie.CHARSAL), Terreno.POCA_DE_CHUVA).dano());
            assertEquals(16, SEM_CRITICO.calcularDano(
                    novo(PokeSalEspecie.SQUIRTSAL), novo(PokeSalEspecie.SQUIRTSAL), Terreno.ASFALTO_QUENTE).dano());
        }
    }

    @Nested
    @DisplayName("RA-01: golpe crítico")
    class Critico {

        @Test
        @DisplayName("Ataque não crítico causa dano normal")
        void naoCritico() {
            final ResultadoAtaque resultado = SEM_CRITICO.calcularDano(
                    novo(PokeSalEspecie.CHARSAL), novo(PokeSalEspecie.CHARSAL), Terreno.NEUTRO);

            assertEquals(31, resultado.dano());
            assertFalse(resultado.critico());
        }

        @Test
        @DisplayName("Ataque crítico aplica x1,5 e arredonda para baixo (31 x 1,5 = 46)")
        void criticoAplicaMultiplicadorEArredonda() {
            final ResultadoAtaque resultado = COM_CRITICO.calcularDano(
                    novo(PokeSalEspecie.CHARSAL), novo(PokeSalEspecie.CHARSAL), Terreno.NEUTRO);

            assertEquals(46, resultado.dano());
            assertTrue(resultado.critico());
        }

        @Test
        @DisplayName("Crítico não altera ATK, DEF nem atributos-base")
        void criticoNaoAlteraAtributos() {
            final PokeSal atacante = novo(PokeSalEspecie.CHARSAL);
            final PokeSal defensor = novo(PokeSalEspecie.CHARSAL);

            COM_CRITICO.calcularDano(atacante, defensor, Terreno.NEUTRO);

            assertEquals(52, atacante.getAtkEfetivo());
            assertEquals(52, atacante.getEspecie().getAtkBase());
            assertEquals(43, defensor.getDefEfetivo());
            assertEquals(39, defensor.getHpAtual());
        }

        @Test
        @DisplayName("Dano mínimo continua sendo 1 com crítico")
        void danoMinimoComCritico() {
            final PokeSal atacante = novo(PokeSalEspecie.SQUIRTSAL);
            atacante.aplicarStatus(StatusEfeito.QUEIMADO);

            assertEquals(1, COM_CRITICO.calcularDano(atacante, novo(PokeSalEspecie.SQUIRTSAL), Terreno.NEUTRO).dano());
        }
    }

    @Test
    @DisplayName("Ordem dos modificadores: elemental x terreno x crítico, com um único floor (28 x 2 x 1,15 x 1,5 = 96)")
    void modificadoresCombinados() {
        assertEquals(96, COM_CRITICO.calcularDano(
                novo(PokeSalEspecie.CHARSAL), novo(PokeSalEspecie.BULBASAL), Terreno.ASFALTO_QUENTE).dano());
    }

    @Test
    @DisplayName("Dano mínimo de 1 quando o dano-base zera")
    void danoMinimoQuandoBaseZera() {
        final PokeSal atacante = novo(PokeSalEspecie.SQUIRTSAL);
        atacante.aplicarStatus(StatusEfeito.QUEIMADO);

        assertEquals(1, SEM_CRITICO.calcularDano(atacante, novo(PokeSalEspecie.SQUIRTSAL), Terreno.NEUTRO).dano());
    }

    @Test
    @DisplayName("Queimado entra no dano com o ATK já reduzido (26 - 21 = 5)")
    void queimadoReduzODano() {
        final PokeSal atacante = novo(PokeSalEspecie.CHARSAL);
        atacante.aplicarStatus(StatusEfeito.QUEIMADO);

        assertEquals(5, SEM_CRITICO.calcularDano(atacante, novo(PokeSalEspecie.CHARSAL), Terreno.NEUTRO).dano());
    }

    @Test
    @DisplayName("Bônus de sobrevivência aumenta o dano (ATK 62 - 21 = 41)")
    void sobrevivenciaAumentaODano() {
        final PokeSal atacante = novo(PokeSalEspecie.CHARSAL);
        atacante.receberDano(30);

        assertEquals(41, SEM_CRITICO.calcularDano(atacante, novo(PokeSalEspecie.CHARSAL), Terreno.NEUTRO).dano());
    }
}
