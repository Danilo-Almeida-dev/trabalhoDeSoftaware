package pokesal.modelo;

import java.util.EnumMap;
import java.util.Map;
import pokesal.util.Constantes;

public final class Treinador {

    private final String nome;
    private final PokeSal pokeSalAtivo;
    private final Map<ItemTipo, Integer> estoqueItens;
    private int itensUsados;
    private int descansosUsados;

    public Treinador(final String nome, final PokeSal pokeSalAtivo) {
        this.nome = nome;
        this.pokeSalAtivo = pokeSalAtivo;
        this.estoqueItens = new EnumMap<>(ItemTipo.class);
        this.estoqueItens.put(ItemTipo.POTION, Constantes.ESTOQUE_INICIAL_POTION);
        this.estoqueItens.put(ItemTipo.SUPER_POTION, Constantes.ESTOQUE_INICIAL_SUPER_POTION);
        this.estoqueItens.put(ItemTipo.ANTIDOTE, Constantes.ESTOQUE_INICIAL_ANTIDOTE);
        this.itensUsados = 0;
        this.descansosUsados = 0;
    }

    public String getNome() {
        return nome;
    }

    public PokeSal getPokeSalAtivo() {
        return pokeSalAtivo;
    }

    public int getItensUsados() {
        return itensUsados;
    }

    public boolean atingiuLimiteDeItens() {
        return itensUsados >= Constantes.MAXIMO_ITENS_POR_TREINADOR;
    }

    public int getEstoque(final ItemTipo item) {
        return estoqueItens.getOrDefault(item, 0);
    }

    public void registrarUsoDeItem(final ItemTipo item) {
        estoqueItens.put(item, getEstoque(item) - 1);
        itensUsados++;
    }

    public boolean jaDescansou() {
        return descansosUsados >= Constantes.MAXIMO_DESCANSOS_POR_TREINADOR;
    }

    public void registrarDescanso() {
        descansosUsados++;
    }
}
