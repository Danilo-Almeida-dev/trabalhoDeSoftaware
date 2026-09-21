# Declaração de Uso de Inteligência Artificial

**Projeto:** Simulador de Batalha PokeSal · Fase 1
**Grupo:** João Breno Oliveira Gonçalves, Danilo Santos Almeida, Ícaro Mocitaíba Cerqueira da Cruz, Keurim Kauana Moreira Dias
**Ferramenta:** Claude (Anthropic), modelo Claude Sonnet 5, via Claude Code (extensão do VS Code)

> **RASCUNHO PARA REVISÃO DO GRUPO.** Este texto descreve o que é possível
> comprovar pelo histórico desta sessão e pelo próprio README do código. Os
> trechos em **[PREENCHER]** só o grupo sabe responder. **Ele diverge do
> `AI_DECLARATION.pdf` assinado anteriormente**, que diz que a IA foi usada
> "apenas para formatação de textos" e que o código foi desenvolvido
> "integralmente pelos integrantes". Isso não bate com o README do projeto
> ("Este código foi gerado com apoio de IA como ponto de partida") nem com os
> itens abaixo. Antes da entrega, o grupo precisa decidir qual texto vale e
> assinar uma versão que descreva o uso real.

## 1. Onde a IA foi usada

### 1.1 Código-fonte (`src/main/java`)

- O esqueleto inicial do código Java foi gerado com apoio de IA, conforme o
  README do projeto. **[PREENCHER: quem gerou, em que data, com que prompt e o
  que o grupo alterou depois.]**
- Nesta sessão (20/09/2026), a IA:
  - removeu todos os comentários e Javadoc do código e depois inseriu 10
    comentários curtos de orientação;
  - extraiu os métodos públicos `Batalha.validarUsoDeItem` e
    `Batalha.validarDescanso` (a regra do item 1.23 do documento de análise:
    item em PokéSal com 0 HP lança exceção);
  - removeu um import não usado (`Batalha`) e criou a constante
    `MAXIMO_TURNOS_DEMONSTRACAO` no lugar de um número mágico em `Main`;
  - corrigiu o `checkstyle.xml`, que não executava (propriedade `scope` inexistente
    em `JavadocMethod`).

### 1.2 Testes (`src/test/java`)

- Os 100 testes unitários JUnit 5 foram escritos pela IA nesta sessão, com base
  nos critérios de aceitação e na matriz de rastreabilidade do documento de
  análise estática. O grupo deve revisar cada teste antes de entregar.

### 1.3 Documentos e ambiente

- Diagrama de Classes e Diagrama de Casos de Uso (em Mermaid), rascunho do
  relatório de contribuição, este arquivo e o `registro-prompts.md`.
- Instalação do JDK 17 e do Maven 3.9.9 no computador de um dos integrantes do grupo,
  configuração de `JAVA_HOME`/`PATH`, e publicação dos arquivos no GitHub.

### 1.4 Formatação de documentos

- Segundo o `AI_DECLARATION.pdf` anterior, a IA ajudou a formatar em LaTeX o
  documento de análise, as atas e outros arquivos da entrega.
  **[PREENCHER/confirmar.]**

## 2. O que não foi gerado por IA

Segundo o grupo **[confirmar]**: o enunciado do trabalho, as reuniões e a divisão
de tarefas, e as decisões de análise e os três requisitos autorais (Golpe
Crítico, Descanso e Bônus de Sobrevivência) descritos no documento de análise.

## 3. Como o grupo verificou o resultado

- Os 100 testes passam com `mvn test` (verificado em 20/09/2026).
- Uma checagem de mutação simples (alterar o multiplicador do crítico e o limite
  do bônus de sobrevivência) faz 4 testes falharem, o que indica que eles
  detectam alterações nessas regras.
- **[PREENCHER: quem do grupo leu e validou o código e os testes.]**

## 4. Registro de prompts

Ver [`docs/registro-prompts.md`](docs/registro-prompts.md).

## 5. Responsabilidade

O grupo permanece responsável pelo conteúdo entregue, inclusive pelo que foi
gerado com apoio de IA.

Assinaturas (após a revisão do grupo): _______________________________
