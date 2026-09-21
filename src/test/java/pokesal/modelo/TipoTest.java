package pokesal.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TipoTest {

    @ParameterizedTest(name = "{0} contra {1} = {2}")
    @DisplayName("RF-02: matriz elemental")
    @CsvSource({
        "FOGO,PLANTA,2.0",
        "AGUA,FOGO,2.0",
        "PLANTA,AGUA,2.0",
        "FOGO,AGUA,0.5",
        "AGUA,PLANTA,0.5",
        "PLANTA,FOGO,0.5",
        "FOGO,FOGO,1.0",
        "AGUA,AGUA,1.0",
        "PLANTA,PLANTA,1.0"
    })
    void multiplicadorContra(final Tipo atacante, final Tipo defensor, final double esperado) {
        assertEquals(esperado, atacante.multiplicadorContra(defensor));
    }
}
