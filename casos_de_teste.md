# Casos de teste - Sudoku Binario 6x6 (console)

Tabuleiro 6x6, colunas A-F e linhas 1-6. Cada casa recebe 0 ou 1.

Regras do jogo:
1. No maximo dois numeros iguais seguidos (linha ou coluna).
2. Cada linha e cada coluna tem tres 0 e tres 1.
3. Nao pode haver duas linhas iguais nem duas colunas iguais.

Legenda do tabuleiro: `[1]` = casa fixa do puzzle, ` 1 ` = casa preenchida pelo jogador, ` . ` = casa vazia.

| ID | Objetivo | Entrada | Resultado esperado |
|----|----------|---------|--------------------|
| CT01 | Menu de dificuldade | Iniciar o jogo e digitar `1`, `2` ou `3` | Gera o puzzle e mostra "Novo puzzle gerado com N casas preenchidas." |
| CT02 | Casas por dificuldade | Gerar varios puzzles em cada nivel | Facil = 22 casas, Medio = 17, Dificil = menos de 17 |
| CT03 | Jogada valida | `C3 1` e depois `c3 0` em casa vazia nao fixa | A casa recebe o valor. Maiuscula e minuscula valem igual |
| CT04 | Coordenada invalida | `G7 1`, `Z9 1`, `A0 1` | "Coordenada invalida. Use letra A-F e numero 1-6 (ex: C3)." O tabuleiro nao muda |
| CT05 | Valor invalido | `A1 2` ou `A1 abc` | "Valor invalido. Use 0, 1 ou x (apagar)." O tabuleiro nao muda |
| CT06 | Casa fixa protegida | Jogar sobre uma casa `[n]` | "Essa casa e fixa e nao pode ser alterada." O valor continua igual |
| CT07 | Solucao valida e unica | Gerar 50 puzzles e resolver cada um por forca bruta | Toda solucao respeita as 3 regras. Cada puzzle tem uma unica solucao |
| CT08 | Dica | `dica` com casas vazias | Revela uma casa que nao estava certa, com o valor da solucao. Mostra "Dica: a casa X# e V." e conta 1 dica. Se nao houver o que revelar: "Nao ha o que revelar." |
| CT09 | Vitoria | Preencher todas as casas corretamente | "PARABENS! Puzzle resolvido em S segundos" com "sem dicas!" ou "usando N dica(s)." |
| CT10 | Resolver | `resolver` no meio da partida | Mostra a solucao completa e "Solucao mostrada. Mais sorte na proxima!" Nao mostra a mensagem de parabens |
