import java.io.*;
import java.util.*;

public class App {

    private static Grafo<String> grafo;

    public static void main(String[] args) {
        grafo = new Grafo<>();

        // caminho para ler o arquivo entrada.txt do professor
        carregarGrafoDoArquivo("libs/entrada.txt");

        // menu dos crias
        Scanner scanner = new Scanner(System.in);
        boolean rodando = true;

        while (rodando) {
            System.out.println("\nMenu:");
            System.out.println("1. Acrescentar cidade");
            System.out.println("2. Acrescentar rota");
            System.out.println("3. Calcular árvore geradora mínima (AGM)");
            System.out.println("4. Calcular caminho mínimo entre duas cidades");
            System.out.println("5. Calcular caminho mínimo entre duas cidades considerando apenas a AGM");
            System.out.println("6. Gravar e Sair");
            System.out.print("Escolha uma opção: ");
            int opcao = scanner.nextInt();
            scanner.nextLine(); // Consumir a nova linha

            switch (opcao) {
                case 1:
                    adicionarCidade(scanner);
                    break;
                case 2:
                    adicionarRota(scanner);
                    break;
                case 3:
                    calcularAGM();
                    break;
                case 4:
                    calcularCaminhoMinimo(scanner);
                    break;
                case 5:
                    calcularCaminhoMinimoAGM(scanner);
                    break;
                case 6:
                    gravarArquivos();
                    rodando = false;
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        }
        scanner.close();
    }

    private static void carregarGrafoDoArquivo(String nomeArquivo) {
        File arquivo = new File(nomeArquivo);
        if (!arquivo.exists()) {
            System.out.println("Arquivo não encontrado: " + nomeArquivo);
            return;
        }
        
        // basicamente um filtro pra tirar esse "BOM" que tava dando erro
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(arquivo), "UTF-8"))) {
            // remover o "BOM" 
            br.mark(1);
            if (br.read() != 0xFEFF) {
                br.reset(); // volta 1 caractere
            }
    
            // feita a verificação, ele começa a ler o arquivo
            String linha = br.readLine();
            if (linha != null) {
                int n = Integer.parseInt(linha.trim());
                System.out.println("Número de cidades: " + n);
    
                List<String> cidades = new ArrayList<>();
                for (int i = 0; i < n; i++) {
                    String cidade = br.readLine().trim();
                    cidades.add(cidade);
                    System.out.println("Cidade lida: " + cidade);
                }
    
                // matriz de adjacencia é lida aqui
                float[][] matrizAdjacencia = new float[n][n];
                for (int i = 0; i < n; i++) {
                    String[] valores = br.readLine().split(",");
                    for (int j = 0; j < n; j++) {
                        matrizAdjacencia[i][j] = Float.parseFloat(valores[j].trim());
                        System.out.print(matrizAdjacencia[i][j] + " ");
                    }
                    System.out.println();
                }
    
                // grafo criado
                Grafo<String> grafo = new Grafo<>();
                for (String cidade : cidades) {
                    grafo.adicionaVertice(cidade);
                }
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        if (matrizAdjacencia[i][j] != 0) {
                            grafo.adicionarAresta(cidades.get(i), cidades.get(j), matrizAdjacencia[i][j]);
                        }
                    }
                }
                System.out.println("Grafo carregado com sucesso.");
    
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Erro de formato no arquivo: " + e.getMessage());
        }
    }

    private static void adicionarCidade(Scanner scanner) {
        // algoritmo pra adicionar cidades com verificações de erro
        System.out.print("Nome da cidade: ");
        String nomeCidade = scanner.nextLine();
        if (grafo.obterVertice(nomeCidade) == null) {
            grafo.adicionaVertice(nomeCidade);
            System.out.println("Cidade adicionada.");
        } else {
            System.out.println("Cidade já existe.");
        }
    }

    private static void adicionarRota(Scanner scanner) {
        System.out.print("Cidade de origem: ");
        String origem = scanner.nextLine();
        System.out.print("Cidade de destino: ");
        String destino = scanner.nextLine();
        System.out.print("Distância: ");
        float distancia = scanner.nextFloat();
        scanner.nextLine(); 
        
        if (grafo.obterVertice(origem) == null) {
            grafo.adicionaVertice(origem);
        }
        if (grafo.obterVertice(destino) == null) {
            grafo.adicionaVertice(destino);
        }
        grafo.adicionarAresta(origem, destino, distancia);
        System.out.println("Rota adicionada.");
    }

    private static void calcularAGM() {
        Grafo<String> agm = grafo.calcularArvoreGeradoraMinima();
        float somaPesos = 0;
        for (Aresta<String> aresta : agm.getArestas()) {
            System.out.println("Origem: " + aresta.getOrigem().getValor() + ", Destino: " + aresta.getDestino().getValor() + ", Peso: " + aresta.getPeso());
            somaPesos += aresta.getPeso();
        }
        System.out.println("Soma total dos pesos: " + somaPesos);
    }

    private static void calcularCaminhoMinimo(Scanner scanner) {
        System.out.print("Cidade de origem: ");
        String origem = scanner.nextLine();
        System.out.print("Cidade de destino: ");
        String destino = scanner.nextLine();
        grafo.calcularCaminhoMinimo(origem, destino);
    }

    private static void calcularCaminhoMinimoAGM(Scanner scanner) {
        Grafo<String> agm = grafo.calcularArvoreGeradoraMinima();
        System.out.print("Cidade de origem: ");
        String origem = scanner.nextLine();
        System.out.print("Cidade de destino: ");
        String destino = scanner.nextLine();
        agm.calcularCaminhoMinimo(origem, destino);
    }

    private static void gravarArquivos() {
        try {
            // Gravar grafo completo
            try (PrintWriter pw = new PrintWriter(new FileWriter("grafoCompleto.txt"))) {
                List<Vertice<String>> vertices = grafo.getVertices();
                pw.println(vertices.size());
                for (Vertice<String> vertice : vertices) {
                    pw.println(vertice.getValor());
                }
                // cria a matriz se ainda nao foi criada, e preenche com valores
                float[][] matrizAdjacencia = new float[vertices.size()][vertices.size()];
                for (Aresta<String> aresta : grafo.getArestas()) {
                    int origemIndex = vertices.indexOf(aresta.getOrigem());
                    int destinoIndex = vertices.indexOf(aresta.getDestino());
                    matrizAdjacencia[origemIndex][destinoIndex] = aresta.getPeso();
                }
                for (int i = 0; i < matrizAdjacencia.length; i++) {
                    for (int j = 0; j < matrizAdjacencia[i].length; j++) {
                        pw.print(matrizAdjacencia[i][j]);
                        if (j < matrizAdjacencia[i].length - 1) {
                            pw.print(",");
                        }
                    }
                    pw.println();
                }
            }

            // Gravar AGM
            try (PrintWriter pw = new PrintWriter(new FileWriter("agm.txt"))) {
                Grafo<String> agm = grafo.calcularArvoreGeradoraMinima();
                List<Vertice<String>> vertices = agm.getVertices();
                pw.println(vertices.size());
                for (Vertice<String> vertice : vertices) {
                    pw.println(vertice.getValor());
                }
                // Criar e preencher matriz de adjacência da AGM
                float[][] matrizAdjacencia = new float[vertices.size()][vertices.size()];
                for (Aresta<String> aresta : agm.getArestas()) {
                    int origemIndex = vertices.indexOf(aresta.getOrigem());
                    int destinoIndex = vertices.indexOf(aresta.getDestino());
                    matrizAdjacencia[origemIndex][destinoIndex] = aresta.getPeso();
                }
                for (int i = 0; i < matrizAdjacencia.length; i++) {
                    for (int j = 0; j < matrizAdjacencia[i].length; j++) {
                        pw.print(matrizAdjacencia[i][j]);
                        if (j < matrizAdjacencia[i].length - 1) {
                            pw.print(",");
                        }
                    }
                    pw.println();
                }
            }

        } catch (IOException e) {
            System.out.println("Erro ao gravar os arquivos: " + e.getMessage());
        }
    } 
}
