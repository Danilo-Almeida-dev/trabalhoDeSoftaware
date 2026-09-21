package pokesal.critico;

@FunctionalInterface
public interface FonteCriticidade {

    // interface separada pra dar pra trocar o sorteio por um valor fixo nos testes
    boolean isCritico();
}
