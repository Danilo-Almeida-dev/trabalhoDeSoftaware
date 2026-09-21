package pokesal.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TreinadorTest {

    private Treinador treinador;

    @BeforeEach
    void criarTreinador() {
        treinador = new Treinador("Ash", new PokeSal(PokeSalEspecie.CHARSAL));
    }

    @Test
    @DisplayName("Estoque inicial: 2 Potion, 1 Super Potion, 1 Antidote")
    void estoqueInicial() {
        assertEquals(2, treinador.getEstoque(ItemTipo.POTION));
        assertEquals(1, treinador.getEstoque(ItemTipo.SUPER_POTION));
        assertEquals(1, treinador.getEstoque(ItemTipo.ANTIDOTE));
        assertEquals(0, treinador.getItensUsados());
    }

    @Test
    @DisplayName("Registrar uso consome estoque e incrementa o contador único")
    void registrarUsoConsomeEstoque() {
        treinador.registrarUsoDeItem(ItemTipo.POTION);

        assertEquals(1, treinador.getEstoque(ItemTipo.POTION));
        assertEquals(1, treinador.getItensUsados());
        assertFalse(treinador.atingiuLimiteDeItens());
    }

    @Test
    @DisplayName("RF-07: o limite de 2 usos soma todos os tipos de item")
    void limiteConsideraTodosOsTipos() {
        treinador.registrarUsoDeItem(ItemTipo.POTION);
        treinador.registrarUsoDeItem(ItemTipo.ANTIDOTE);

        assertTrue(treinador.atingiuLimiteDeItens());
    }

    @Test
    @DisplayName("RA-02: o descanso é liberado no início e bloqueado depois de usado")
    void controleDeDescanso() {
        assertFalse(treinador.jaDescansou());
        treinador.registrarDescanso();
        assertTrue(treinador.jaDescansou());
    }
}
