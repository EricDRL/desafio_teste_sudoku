import java.util.*;

/**
 * SUDOKU BINARIO (Takuzu / Binairo) 6 x 6 - console.
 * (Arquivo 100% ASCII: sem acentos e sem simbolos especiais.)
 *
 * Regras:
 *  1) Cada casa recebe 0 ou 1.
 *  2) Nao pode haver mais de dois numeros iguais seguidos (na linha ou na coluna).
 *  3) Cada linha e cada coluna tem exatamente tres 0 e tres 1.
 *  4) Nao pode haver duas linhas iguais, nem duas colunas iguais.
 *
 * Comandos (colunas A-F, linhas 1-6):
 *   C3 1      -> coloca 1 na casa C3
 *   C3 0      -> coloca 0 na casa C3
 *   C3 x      -> apaga a casa C3 (tambem funciona com '.')
 *   dica      -> revela uma casa vazia
 *   verificar -> mostra as casas que voce preencheu errado
 *   resolver  -> mostra a solucao e encerra a rodada
 *   regras    -> mostra as regras
 *   novo      -> novo jogo (escolhe a dificuldade de novo)
 *   sair      -> encerra
 *
 * No tabuleiro: [1] = casa fixa do puzzle | " 1 " = casa preenchida por voce | " . " = vazia
 * Todo puzzle gerado tem solucao unica.
 *
 * Compilar: javac SudokuBinario.java
 * Executar: java SudokuBinario
 */
public class SudokuBinario {

    static final int N = 6;

    static int[][] sol = new int[N][N];
    static int[][] atual = new int[N][N];
    static boolean[][] fixo = new boolean[N][N];
    static boolean fim, saiu, reiniciar, desistiu;
    static long inicio;
    static int dicas;

    static final Scanner sc = new Scanner(System.in);
    static final Random rnd = new Random();

    // ===================== Main =====================
    public static void main(String[] args) {
        System.out.println("=====================================");
        System.out.println("        SUDOKU BINARIO  6 x 6");
        System.out.println("=====================================");
        mostrarRegras();

        boolean continuar = true;
        while (continuar) {
            iniciarRodada();
            fim = false;
            reiniciar = false;
            desistiu = false;

            while (!fim && !saiu && !reiniciar) {
                desenhar();
                System.out.print("Comando: ");
                processar(ler());
            }
            if (saiu) break;
            if (reiniciar) continue;

            desenhar();
            if (desistiu) {
                System.out.println("Solucao mostrada. Mais sorte na proxima!");
            } else {
                long seg = (System.currentTimeMillis() - inicio) / 1000;
                System.out.println("PARABENS! Puzzle resolvido em " + seg + " segundos"
                        + (dicas > 0 ? " usando " + dicas + " dica(s)." : " sem dicas!"));
            }
            System.out.print("\nJogar novamente? (s/n): ");
            continuar = ler().startsWith("s");
        }
        System.out.println("\nAte a proxima!");
    }

    static void mostrarRegras() {
        System.out.println("Regras:");
        System.out.println(" 1) Preencha com 0 e 1.");
        System.out.println(" 2) No maximo dois numeros iguais seguidos (linha ou coluna).");
        System.out.println(" 3) Cada linha e cada coluna tem tres 0 e tres 1.");
        System.out.println(" 4) Nao pode haver duas linhas iguais nem duas colunas iguais.");
        System.out.println("Comandos: C3 1 | C3 0 | C3 x (apagar) | dica | verificar | resolver | regras | novo | sair");
    }

    // ===================== Preparacao =====================
    static void iniciarRodada() {
        System.out.println("\nEscolha a dificuldade:");
        System.out.println(" [1] Facil   [2] Medio   [3] Dificil");
        int alvo = 0;
        while (true) {
            System.out.print("Opcao: ");
            String s = ler();
            if (s.equals("1")) { alvo = 22; break; }
            if (s.equals("2")) { alvo = 17; break; }
            if (s.equals("3")) { alvo = 0; break; }
            if (s.equals("sair")) { saiu = true; return; }
            System.out.println("Opcao invalida.");
        }

        // Gera uma solucao completa aleatoria
        for (int[] linha : sol) Arrays.fill(linha, -1);
        gerar(sol, 0);

        // Remove casas mantendo a solucao unica
        int[][] p = copiar(sol);
        List<Integer> celulas = new ArrayList<>();
        for (int i = 0; i < N * N; i++) celulas.add(i);
        Collections.shuffle(celulas, rnd);
        int dados = N * N;
        for (int idx : celulas) {
            if (dados <= alvo) break;
            int r = idx / N, c = idx % N;
            int backup = p[r][c];
            p[r][c] = -1;
            if (contar(copiar(p), 2) != 1) {
                p[r][c] = backup; // remover quebraria a unicidade
            } else {
                dados--;
            }
        }

        atual = copiar(p);
        for (int r = 0; r < N; r++)
            for (int c = 0; c < N; c++)
                fixo[r][c] = p[r][c] != -1;

        dicas = 0;
        inicio = System.currentTimeMillis();
        System.out.println("Novo puzzle gerado com " + dados + " casas preenchidas.");
    }

    // ===================== Solver / gerador =====================
    /** Gera uma grade completa valida, com ordem aleatoria de tentativa. */
    static boolean gerar(int[][] g, int pos) {
        if (pos == N * N) return true;
        int r = pos / N, c = pos % N;
        int a = rnd.nextInt(2);
        for (int k = 0; k < 2; k++) {
            g[r][c] = (a + k) % 2;
            if (valido(g, r, c) && gerar(g, pos + 1)) return true;
        }
        g[r][c] = -1;
        return false;
    }

    /** Conta solucoes (ate 'limite') a partir do estado atual de g. */
    static int contar(int[][] g, int limite) {
        int pos = -1;
        for (int i = 0; i < N * N; i++) {
            if (g[i / N][i % N] == -1) {
                pos = i;
                break;
            }
        }
        if (pos < 0) return 1;
        int r = pos / N, c = pos % N;
        int total = 0;
        for (int v = 0; v < 2; v++) {
            g[r][c] = v;
            if (valido(g, r, c)) total += contar(g, limite - total);
            if (total >= limite) break;
        }
        g[r][c] = -1;
        return total;
    }

    /** Verifica se o valor recem colocado em (r,c) respeita as regras. */
    static boolean valido(int[][] g, int r, int c) {
        int v = g[r][c];
        if (tripla(g, r, c, 0, 1, v)) return false;
        if (tripla(g, r, c, 1, 0, v)) return false;

        int nl = 0, nc = 0;
        for (int i = 0; i < N; i++) {
            if (g[r][i] == v) nl++;
            if (g[i][c] == v) nc++;
        }
        if (nl > N / 2 || nc > N / 2) return false;

        if (linhaCheia(g, r)) {
            for (int k = 0; k < N; k++)
                if (k != r && linhaCheia(g, k) && linhasIguais(g, r, k)) return false;
        }
        if (colunaCheia(g, c)) {
            for (int k = 0; k < N; k++)
                if (k != c && colunaCheia(g, k) && colunasIguais(g, c, k)) return false;
        }
        return true;
    }

    /** Existe uma sequencia de tres valores iguais a v passando por (r,c)? */
    static boolean tripla(int[][] g, int r, int c, int dr, int dc, int v) {
        for (int s = -2; s <= 0; s++) {
            boolean todos = true;
            for (int i = 0; i < 3; i++) {
                int rr = r + dr * (s + i), cc = c + dc * (s + i);
                if (rr < 0 || rr >= N || cc < 0 || cc >= N || g[rr][cc] != v) {
                    todos = false;
                    break;
                }
            }
            if (todos) return true;
        }
        return false;
    }

    static boolean linhaCheia(int[][] g, int r) {
        for (int c = 0; c < N; c++) if (g[r][c] == -1) return false;
        return true;
    }

    static boolean colunaCheia(int[][] g, int c) {
        for (int r = 0; r < N; r++) if (g[r][c] == -1) return false;
        return true;
    }

    static boolean linhasIguais(int[][] g, int a, int b) {
        for (int c = 0; c < N; c++) if (g[a][c] != g[b][c]) return false;
        return true;
    }

    static boolean colunasIguais(int[][] g, int a, int b) {
        for (int r = 0; r < N; r++) if (g[r][a] != g[r][b]) return false;
        return true;
    }

    static int[][] copiar(int[][] g) {
        int[][] t = new int[N][N];
        for (int r = 0; r < N; r++) t[r] = g[r].clone();
        return t;
    }

    // ===================== Comandos =====================
    static void processar(String entrada) {
        if (entrada.isEmpty()) return;

        switch (entrada) {
            case "sair":
                saiu = true;
                return;
            case "novo":
                reiniciar = true;
                return;
            case "regras":
                mostrarRegras();
                return;
            case "dica":
                dar_dica();
                return;
            case "verificar":
                verificar();
                return;
            case "resolver":
                atual = copiar(sol);
                desistiu = true;
                fim = true;
                return;
            default:
                break;
        }

        String[] partes = entrada.split("\\s+");
        if (partes.length != 2) {
            System.out.println("Comando invalido. Exemplo: C3 1");
            return;
        }
        int[] pos = lerCoordenada(partes[0]);
        if (pos == null) {
            System.out.println("Coordenada invalida. Use letra A-F e numero 1-6 (ex: C3).");
            return;
        }
        int r = pos[0], c = pos[1];
        if (fixo[r][c]) {
            System.out.println("Essa casa e fixa e nao pode ser alterada.");
            return;
        }

        String v = partes[1];
        if (v.equals("0") || v.equals("1")) {
            atual[r][c] = Integer.parseInt(v);
        } else if (v.equals("x") || v.equals(".")) {
            atual[r][c] = -1;
        } else {
            System.out.println("Valor invalido. Use 0, 1 ou x (apagar).");
            return;
        }
        checarFim();
    }

    static int[] lerCoordenada(String s) {
        if (s.length() != 2) return null;
        int c = s.charAt(0) - 'a';
        int r = s.charAt(1) - '1';
        if (r < 0 || r >= N || c < 0 || c >= N) return null;
        return new int[]{r, c};
    }

    static void dar_dica() {
        List<int[]> vazias = new ArrayList<>();
        for (int r = 0; r < N; r++)
            for (int c = 0; c < N; c++)
                if (atual[r][c] != sol[r][c]) vazias.add(new int[]{r, c});
        if (vazias.isEmpty()) {
            System.out.println("Nao ha o que revelar.");
            return;
        }
        int[] p = vazias.get(rnd.nextInt(vazias.size()));
        atual[p[0]][p[1]] = sol[p[0]][p[1]];
        dicas++;
        System.out.println("Dica: a casa " + (char) ('A' + p[1]) + (p[0] + 1) + " e " + sol[p[0]][p[1]] + ".");
        checarFim();
    }

    static void verificar() {
        StringBuilder erros = new StringBuilder();
        int n = 0;
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                if (atual[r][c] != -1 && atual[r][c] != sol[r][c]) {
                    if (n > 0) erros.append(", ");
                    erros.append((char) ('A' + c)).append(r + 1);
                    n++;
                }
            }
        }
        if (n == 0) System.out.println("Nenhum erro ate agora. Continue assim!");
        else System.out.println("Casas incorretas (" + n + "): " + erros);
    }

    static void checarFim() {
        for (int r = 0; r < N; r++)
            for (int c = 0; c < N; c++)
                if (atual[r][c] == -1) return;
        for (int r = 0; r < N; r++)
            for (int c = 0; c < N; c++)
                if (atual[r][c] != sol[r][c]) {
                    System.out.println("Tabuleiro completo, mas ha erros. Use 'verificar' para achar as casas.");
                    return;
                }
        fim = true;
    }

    // ===================== Tela =====================
    static void desenhar() {
        StringBuilder sep = new StringBuilder("   +");
        for (int c = 0; c < N; c++) sep.append("---+");

        StringBuilder cab = new StringBuilder("    ");
        for (int c = 0; c < N; c++) cab.append(' ').append((char) ('A' + c)).append("  ");

        System.out.println();
        System.out.println(cab);
        System.out.println(sep);
        for (int r = 0; r < N; r++) {
            StringBuilder sb = new StringBuilder(" " + (r + 1) + " |");
            for (int c = 0; c < N; c++) {
                int v = atual[r][c];
                String s;
                if (v == -1) s = " . ";
                else if (fixo[r][c]) s = "[" + v + "]";
                else s = " " + v + " ";
                sb.append(s).append('|');
            }
            System.out.println(sb);
            System.out.println(sep);
        }
        System.out.println();
    }

    static String ler() {
        if (!sc.hasNextLine()) {
            System.out.println("\nEntrada encerrada.");
            System.exit(0);
        }
        return sc.nextLine().trim().toLowerCase();
    }
}