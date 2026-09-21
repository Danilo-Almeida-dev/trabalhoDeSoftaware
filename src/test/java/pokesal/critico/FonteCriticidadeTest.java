package pokesal.critico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FonteCriticidadeTest {

    @Test
    @DisplayName("Fonte fixa força crítico")
    void fixaSempreCritico() {
        assertTrue(new FonteCriticidadeFixa(true).isCritico());
    }

    @Test
    @DisplayName("Fonte fixa força não crítico")
    void fixaNuncaCritico() {
        assertFalse(new FonteCriticidadeFixa(false).isCritico());
    }

    @Test
    @DisplayName("Fonte aleatória com a mesma semente gera a mesma sequência")
    void aleatoriaEhReproduzivelComSemente() {
        final FonteCriticidade a = new FonteCriticidadeAleatoria(42L);
        final FonteCriticidade b = new FonteCriticidadeAleatoria(42L);
        for (int i = 0; i < 1000; i++) {
            assertEquals(a.isCritico(), b.isCritico());
        }
    }

    @Test
    @DisplayName("Fonte aleatória produz críticos em torno de 10%")
    void aleatoriaTemChanceDeCercaDe10Porcento() {
        final FonteCriticidade fonte = new FonteCriticidadeAleatoria(123L);
        final int amostras = 100_000;
        int criticos = 0;
        for (int i = 0; i < amostras; i++) {
            if (fonte.isCritico()) {
                criticos++;
            }
        }
        assertEquals(0.10, (double) criticos / amostras, 0.01);
    }
}
