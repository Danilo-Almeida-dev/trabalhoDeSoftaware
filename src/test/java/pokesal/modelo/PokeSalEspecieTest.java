package pokesal.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PokeSalEspecieTest {

    @Test
    @DisplayName("RF-01: existem exatamente seis PokéSal iniciais")
    void existemSeisEspecies() {
        assertEquals(6, PokeSalEspecie.values().length);
    }

    @ParameterizedTest(name = "{0}")
    @DisplayName("Atributos-base conferem com a tabela do documento (1.2)")
    @CsvSource({
        "BULBASAL,PLANTA,45,49,49,45",
        "CHARSAL,FOGO,39,52,43,65",
        "SQUIRTSAL,AGUA,44,48,65,43",
        "CHIKOSAL,PLANTA,45,49,65,45",
        "CYNDASAL,FOGO,39,52,43,65",
        "TOTOSAL,AGUA,50,65,64,43"
    })
    void atributosBase(final PokeSalEspecie especie, final Tipo tipo,
                       final int hp, final int atk, final int def, final int spd) {
        assertEquals(tipo, especie.getTipo());
        assertEquals(hp, especie.getHpBase());
        assertEquals(atk, especie.getAtkBase());
        assertEquals(def, especie.getDefBase());
        assertEquals(spd, especie.getSpdBase());
    }
}
