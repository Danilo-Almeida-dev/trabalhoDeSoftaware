# PokeSal — Código Java (Fase 1)

Esqueleto de código Java correspondente às decisões do documento de
análise estática da Fase 1 (fórmula de dano, valores de status, itens,
ordem de fim de turno) e aos três requisitos autorais.

Este código foi gerado com apoio de IA como ponto de partida (ver
`AI_DECLARATION.md`); leia e valide cada classe antes de usá-la como
entrega oficial, e ajuste os valores marcados como "referência" no
documento de análise (atributos-base, fórmula de dano, valores de itens)
se o restante do projeto já usar outros valores.

## Estrutura

```
src/main/java/pokesal/
├── Main.java                      ponto de entrada de demonstração
├── modelo/
│   ├── Tipo.java                  tipos elementais e matriz de vantagem (1.3)
│   ├── StatusEfeito.java          Queimado / Envenenado / Paralisado (1.12-1.15)
│   ├── Terreno.java               terrenos e seus modificadores (1.5-1.7, 1.11)
│   ├── PokeSalEspecie.java        as 6 espécies e atributos-base (1.1-1.2)
│   ├── PokeSal.java               instância em batalha (HP, status, ATK/SPD efetivos)
│   ├── ItemTipo.java              Potion, Super Potion, Antidote (1.21)
│   └── Treinador.java             controle de itens e de descanso (1.22, 3.2)
├── acao/
│   ├── TipoAcao.java              ATACAR / USAR_ITEM / DESCANSAR (1.10)
│   └── Acao.java                  ação escolhida por um treinador no turno
├── critico/
│   ├── FonteCriticidade.java      abstração injetável de criticidade (3.1)
│   ├── FonteCriticidadeAleatoria.java   implementação de produção (10%)
│   └── FonteCriticidadeFixa.java  implementação para testes determinísticos
├── excecao/
│   ├── AcaoInvalidaException.java
│   └── LimiteDeItensExcedidoException.java
├── batalha/
│   ├── CalculadoraDano.java       fórmula de dano e ordem de modificadores (1.4, 1.17)
│   ├── ResultadoAtaque.java       dano final + flag de crítico
│   └── Batalha.java               ordenação por SPD, execução de turno, fim de turno
└── util/
    └── Constantes.java            todas as constantes nomeadas (sem magic numbers)
```

## Como compilar e rodar (sem Maven)

```bash
find src -name "*.java" > sources.txt
javac -d out @sources.txt
java -cp out pokesal.Main
```

O `Main` roda uma batalha de demonstração entre CharSal e SquirtSal (com
Queimado aplicado manualmente a um dos dois, para ilustrar o efeito),
sem terreno ativo e com crítico desligado, para dar um resultado sempre
igual quando executado de novo.

## Como compilar e rodar com Maven

Um `pom.xml` mínimo já está incluído, com a estrutura padrão
`src/main/java`, JUnit 5 configurado para os testes da Fase 2, e os
plugins de Checkstyle e Javadoc.

```bash
mvn compile                 # compila
mvn exec:java -Dexec.mainClass=pokesal.Main   # roda o Main (requer exec-maven-plugin)
mvn checkstyle:check         # valida contra checkstyle.xml
mvn javadoc:javadoc          # gera o Javadoc em target/site/apidocs
```

## Checkstyle

`checkstyle.xml`, na raiz deste projeto, cobre os quatro pontos citados
no enunciado:

- **Javadoc** obrigatório em classes e métodos públicos.
- **Naming** (classes em PascalCase, métodos/variáveis em camelCase,
  constantes em UPPER_SNAKE_CASE, pacotes em minúsculas).
- **Ausência de magic numbers** — apenas -1, 0, 1 e 2 são permitidos
  soltos no código (índices, incrementos, contagem de pares); qualquer
  outro valor numérico de regra de negócio deve vir de
  `pokesal.util.Constantes`.
- **Organização de imports** (sem import com `*`, sem imports não
  usados, ordem alfabética).

Este ambiente não tinha acesso ao `.jar` do Checkstyle nem ao repositório
Maven Central para baixá-lo (rede restrita), então a configuração não foi
executada de fato aqui — apenas escrita conforme a documentação do
Checkstyle. Rode-a no computador de vocês (com o Checkstyle instalado, ou
com `mvn checkstyle:check`) e ajuste o código onde ela apontar problemas.

## O que ainda falta (fora do escopo deste código)

- Testes automatizados (JUnit) para os critérios de aceitação da Fase 2 —
  o design já foi pensado para isso (fonte de criticidade injetável,
  exceções específicas, sem aleatoriedade fora de
  `FonteCriticidadeAleatoria`), mas os testes em si não foram escritos.
- Qualquer mecânica de aplicação automática de status por ataques
  específicos (item 1.16 do documento) — hoje só existe a operação
  testável `PokeSal.aplicarStatus(...)`.
- Ciclo de seleção interativa dos PokéSal (1.1) e qualquer interface de
  usuário — este código cobre a lógica de batalha, não a camada de
  entrada/saída.
