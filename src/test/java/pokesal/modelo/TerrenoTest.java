package pokesal.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TerrenoTest {

    @Test
    @DisplayName("Poça de Chuva dá +10% para golpes de Água")
    void pocaDeChuvaBeneficiaAgua() {
        assertEquals(1.10, Terreno.POCA_DE_CHUVA.multiplicadorParaAtaque(Tipo.AGUA), 1e-9);
    }

    @Test
    @DisplayName("Poça de Chuva não altera golpes de Fogo nem de Planta")
    void pocaDeChuvaNaoAlteraOutrosTipos() {
        assertEquals(1.0, Terreno.POCA_DE_CHUVA.multiplicadorParaAtaque(Tipo.FOGO));
        assertEquals(1.0, Terreno.POCA_DE_CHUVA.multiplicadorParaAtaque(Tipo.PLANTA));
    }

    @Test
    @DisplayName("Asfalto Quente dá +15% para golpes de Fogo")
    void asfaltoQuenteBeneficiaFogo() {
        assertEquals(1.15, Terreno.ASFALTO_QUENTE.multiplicadorParaAtaque(Tipo.FOGO), 1e-9);
    }

    @Test
    @DisplayName("Asfalto Quente não altera golpes de Água nem de Planta")
    void asfaltoQuenteNaoAlteraOutrosTipos() {
        assertEquals(1.0, Terreno.ASFALTO_QUENTE.multiplicadorParaAtaque(Tipo.AGUA));
        assertEquals(1.0, Terreno.ASFALTO_QUENTE.multiplicadorParaAtaque(Tipo.PLANTA));
    }

    @Test
    @DisplayName("Terreno neutro e Canteiro Central não alteram o dano")
    void terrenosSemBonusDeDano() {
        for (final Tipo tipo : Tipo.values()) {
            assertEquals(1.0, Terreno.NEUTRO.multiplicadorParaAtaque(tipo));
            assertEquals(1.0, Terreno.CANTEIRO_CENTRAL.multiplicadorParaAtaque(tipo));
        }
    }

    @Test
    @DisplayName("Somente o Canteiro Central cura no fim do turno (5%)")
    void apenasCanteiroCentralCura() {
        assertEquals(0.05, Terreno.CANTEIRO_CENTRAL.percentualCuraFimDeTurno(), 1e-9);
        assertEquals(0.0, Terreno.NEUTRO.percentualCuraFimDeTurno());
        assertEquals(0.0, Terreno.POCA_DE_CHUVA.percentualCuraFimDeTurno());
        assertEquals(0.0, Terreno.ASFALTO_QUENTE.percentualCuraFimDeTurno());
    }
}
