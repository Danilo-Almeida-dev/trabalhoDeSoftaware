# Registro de prompts

Prompts enviados ao Claude (Claude Code, VS Code) na sessão de **20/09/2026**,
na ordem em que foram feitos, com erros de digitação mantidos. O resultado de
cada um está descrito na coluna da direita.

| # | Prompt | O que a IA fez |
|---|---|---|
| 1 | "vamos la claude ta vendo esté tire TODOS OS COMENTARIOS DELE faça oque pedi TIRE TODOS OS COMENTARIOS DE TODOS OS CODIGOS CLASSES ETC..." | Removeu todos os comentários dos 19 arquivos `.java` e do `pom.xml` e `checkstyle.xml` |
| 2 | "Quero que voce coloque poucos comentarios apenas como mode de iniciação como ponta pe inicial para a gente mesmo mas que nao fique tão robotico" | Inseriu 10 comentários curtos em `Batalha`, `CalculadoraDano`, `PokeSal` e `FonteCriticidade` |
| 3 | "consegue subir esse codigo nesse repositorio no github [link] De forma organizada tá" | Clonou o repositório (vazio), organizou o projeto Maven na raiz, criou `.gitignore` e fez o commit. O push falhou por permissão (403) |
| 4 | "Mas no caso voce ja comitou?" | Explicou que o commit era só local |
| 5 | "Pode subir" | Tentou o push de novo (403) |
| 6 | "Então vc quer dizer que eu nao consigo subir no repositorio dele, ele teria que mandar um convite ai eu aceito e depois disso eu conseguiria subir" | Confirmou a necessidade do convite de colaborador |
| 7 | "Pronto ja aceitei o convite sobe la" / "Chat como voce nao consegue subir sendo que o repositorio dele esta publico e ele ainda me fez um convite para participar do projeto" | Consultou a API do GitHub, viu que o convite ainda estava pendente e, depois do aceite, fez o push |
| 8 | "vamos la proximo passo, tem algo para fazer esse codigo rodar , no caso rodar o jogo, logo apos isso vamos tentar fazer testes que ele pede aqui no vscode mesmo" | Verificou que não havia JDK e propôs a instalação |
| 9 | "Instale tudo que é necessário para rodar o código e fazer os testes unitários." / "Tente instalar de novo." | Instalou o JDK 17 (winget) e o Maven 3.9.9, configurou o PATH e rodou o `Main` |
| 10 | "Já não está no arquivo que eu mandei?" | Leu os PDFs do grupo e escreveu 96 testes unitários JUnit 5 a partir dos critérios do documento de análise |
| 11 | "Sim, eu gostaria." | Publicou os testes no repositório |
| 12 | "Faça os dois." / "Faça tudo." | Ajustou o código no item 1.23 (validação isolada de item e descanso), corrigiu o Checkstyle, criou os diagramas, o relatório de contribuição (rascunho), o `AI_DECLARATION.md` e este registro |

## Prompts anteriores a esta sessão

**[PREENCHER]** Prompts usados para gerar o esqueleto do código Java e para
formatar o documento de análise e as atas (período de 10/09/2026 a 19/09/2026,
segundo o `AI_DECLARATION.pdf`). O grupo deve colar aqui os textos originais.
