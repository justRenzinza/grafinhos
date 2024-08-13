package lib;

import java.util.*;

public class Grafo<T> {

    //lista de vértices do grafo, cada um pode ser de qualquer tipo T
    private List<Vertice<T>> vertices = new ArrayList<>();
    //lista de arestas do grafo, que conectam os vértices
    private List<Aresta<T>> arestas = new ArrayList<>();

    public Grafo() {
        this.vertices = new ArrayList<>();
    }
    //retorna a lista de vértices

    public List<Vertice<T>> getVertices() {
        return this.vertices;
    }
    
    //retorna a lista de arestas

    public List<Aresta<T>> getArestas() {
        return this.arestas;
    }

     //método para adicionar um novo vértice ao grafo
    //ele cria um novo vértice com o valor dado e o adiciona à lista de vértices
    public Vertice<T> adicionaVertice(T valor) {
        Vertice<T> novo = new Vertice<>(valor);
        this.vertices.add(novo);
        return novo;
    }

    //método para encontrar um vértice na lista pelo valor
    //ele percorre a lista de vértices e retorna aquele que tiver o valor igual ao parâmetro  q foi passado
    public Vertice<T> obterVertice(T valor) {
        for (Vertice<T> vertice : this.getVertices()) {
            if (vertice.getValor().equals(valor))
                return vertice;
        }
        return null;
    }

     //método para adicionar uma nova aresta entre dois vértices
    //ele também cria os vértices de origem e destino se não existirem ainda no grafo
    public void adicionarAresta(T origem, T destino, float peso) {
        Vertice<T> verticeOrigem = obterVertice(origem);
        if (verticeOrigem == null) {
            verticeOrigem = adicionaVertice(origem);
        }
        Vertice<T> verticeDestino = obterVertice(destino);
        if (verticeDestino == null) {
            verticeDestino = adicionaVertice(destino);
        }
        Aresta<T> novaAresta = new Aresta<>(verticeOrigem, verticeDestino, peso);
        this.arestas.add(novaAresta);
    }

    //método que calcula a árvore geradora mínima
    //ele busca a menor conexão entre todo os vértices, conectando tudo com o menor custo possível
    public Grafo<T> calcularArvoreGeradoraMinima() {
        Grafo<T> arvoreGeradoraMinima = new Grafo<>();

        Map<Vertice<T>, Vertice<T>> parent = new HashMap<>();
        Map<Vertice<T>, Integer> rank = new HashMap<>();

        for (Vertice<T> vertice : vertices) {
            arvoreGeradoraMinima.adicionaVertice(vertice.getValor());
            parent.put(vertice, vertice);
            rank.put(vertice, 0);
        }

        //ordena as arestas por peso em ordem crescente
        List<Aresta<T>> arestasOrdenadas = new ArrayList<>(arestas);
        arestasOrdenadas.sort(Comparator.comparingDouble(Aresta::getPeso));

        //percorre as arestas ordenadas para construir a árvore geradora mínima
        for (Aresta<T> aresta : arestasOrdenadas) {
            Vertice<T> u = findSet(parent, aresta.getOrigem());
            Vertice<T> v = findSet(parent, aresta.getDestino());

            if (!u.equals(v)) {
                arvoreGeradoraMinima.adicionarAresta(aresta.getOrigem().getValor(), aresta.getDestino().getValor(), aresta.getPeso());
                union(parent, rank, u, v);
            }
        }

        return arvoreGeradoraMinima;
    }
    
// método auxiliar para encontrar o conjunto ao qual um vértice pertence
    private Vertice<T> findSet(Map<Vertice<T>, Vertice<T>> parent, Vertice<T> vertice) {
        if (!vertice.equals(parent.get(vertice))) {
            parent.put(vertice, findSet(parent, parent.get(vertice)));
        }
        return parent.get(vertice);
    }
// método auxiliar para unir dois conjuntos diferentes
    private void union(Map<Vertice<T>, Vertice<T>> parent, Map<Vertice<T>, Integer> rank, Vertice<T> u, Vertice<T> v) {
        Vertice<T> rootU = findSet(parent, u);
        Vertice<T> rootV = findSet(parent, v);

        if (rank.get(rootU) > rank.get(rootV)) {
            parent.put(rootV, rootU);
        } else if (rank.get(rootU) < rank.get(rootV)) {
            parent.put(rootU, rootV);
        } else {
            parent.put(rootV, rootU);
            rank.put(rootU, rank.get(rootU) + 1);
        }
    }

    private List<Aresta<T>> pegarDestinos(Vertice<T> v) {
        List<Aresta<T>> destinos = new ArrayList<>();
        try {
            for (Aresta<T> aresta : this.arestas) {
                if (aresta.getOrigem().equals(v) || aresta.getDestino().equals(v)) {
                    destinos.add(aresta);
                }
            }
        } catch (Exception e) {
            System.err.println("Ocorreu um erro ao pegar destinos: " + e.getMessage());
            e.printStackTrace();
        }
        return destinos;
    }

    // método para calcular o caminho mínimo entre dois vértices
    // ele encontra o caminho mais curto do vértice origem até o vértice destino
    public void calcularCaminhoMinimo(T origem, T destino) {
        Vertice<T> verticeOrigem = obterVertice(origem);
        Vertice<T> verticeDestino = obterVertice(destino);
        if (verticeOrigem == null || verticeDestino == null) {
            System.out.println("Vértices não encontrados no grafo");
            return;
        }

        Map<Vertice<T>, Float> distancias = new HashMap<>();
        Map<Vertice<T>, Vertice<T>> anteriores = new HashMap<>();
        PriorityQueue<Vertice<T>> fila = new PriorityQueue<>(Comparator.comparing(distancias::get));

        for (Vertice<T> vertice : vertices) {
            distancias.put(vertice, Float.MAX_VALUE);
            anteriores.put(vertice, null);
        }
        distancias.put(verticeOrigem, 0f);
        fila.add(verticeOrigem);

        while (!fila.isEmpty()) {
            Vertice<T> atual = fila.poll();

            for (Aresta aresta : pegarDestinos(atual)) {
                Vertice<T> vizinho = aresta.getDestino();
                float novaDistancia = distancias.get(atual) + aresta.getPeso();

                if (distancias.get(vizinho) > novaDistancia) {
                    distancias.put(vizinho, novaDistancia);
                    anteriores.put(vizinho, atual);
                    fila.add(vizinho);
                }

                vizinho = aresta.getOrigem();
                novaDistancia = distancias.get(atual) + aresta.getPeso();
                if (distancias.get(vizinho) > novaDistancia) {
                    distancias.put(vizinho, novaDistancia);
                    anteriores.put(vizinho, atual);
                    fila.add(vizinho);
                }
            }
        }
        
        if (distancias.get(verticeDestino) == Float.MAX_VALUE) {
            System.out.println("Não há caminho entre " + origem + " e " + destino + ".");
            return;
        }

        List<Vertice<T>> caminho = new ArrayList<>();
        for (Vertice<T> at = verticeDestino; at != null; at = anteriores.get(at)) {
            caminho.add(at);
        }
        Collections.reverse(caminho);

        System.out.println("Caminho mínimo de " + origem + " até " + destino + ":");
        for (Vertice<T> vertice : caminho) {
            System.out.print(vertice.getValor() + " ");
        }
        System.out.println("\nDistância total: " + distancias.get(verticeDestino));
    }
//verifica se o grafo tem ciclos
    public boolean temCiclo() {
        boolean[] visitados = new boolean[this.vertices.size()];
        boolean[] noCaminho = new boolean[this.vertices.size()];
        for (Vertice<T> vertice : this.vertices) {
            int index = this.vertices.indexOf(vertice);
            if (!visitados[index]) {
                if (temCicloRecursivo(vertice, visitados, noCaminho, index)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean temCicloRecursivo(Vertice<T> vertice, boolean[] visitados, boolean[] noCaminho, int index) {
        visitados[index] = true;
        noCaminho[index] = true;
        for (Aresta<T> aresta : this.arestas) {
            if (aresta.getOrigem().equals(vertice)) {
                Vertice<T> verticeDestino = aresta.getDestino();
                int vizinhoIndex = this.vertices.indexOf(verticeDestino);
                if (!visitados[vizinhoIndex]) {
                    if (temCicloRecursivo(verticeDestino, visitados, noCaminho, vizinhoIndex)) {
                        return true;
                    }
                } else if (noCaminho[vizinhoIndex]) {
                    return true;
                }
            }
        }
        noCaminho[index] = false;
        return false;
    }

    public List<Vertice<T>> ordenacaoTopologica() {
        if (this.temCiclo()) {
            return null;
        }
        List<Vertice<T>> resultado = new ArrayList<>();
        List<Vertice<T>> semArestaEntrada = this.verticesSemArestaEntrada();
        boolean[] visitados = new boolean[this.vertices.size()];
        for (Vertice<T> vertice : semArestaEntrada) {
            int index = this.vertices.indexOf(vertice);
            if (!visitados[index]) {
                ordenacaoTopologicaRecursiva(vertice, visitados, resultado, index);
            }
        }
        Collections.reverse(resultado);
        return resultado;
    }
//metodo auxiliar na ordenação topológica q percorre os vértices e os adiciona ao resultado em ordem de finalização
    private void ordenacaoTopologicaRecursiva(Vertice<T> vertice, boolean[] visitados, List<Vertice<T>> resultado, int index) {
        visitados[index] = true;
        for (Aresta<T> aresta : this.arestas) {
            if (aresta.getOrigem().equals(vertice)) {
                Vertice<T> verticeDestino = aresta.getDestino();
                int destinoIndex = this.vertices.indexOf(verticeDestino);
                if (!visitados[destinoIndex]) {
                    ordenacaoTopologicaRecursiva(verticeDestino, visitados, resultado, destinoIndex);
                }
            }
        }
        resultado.add(vertice);
    }
//encontra os vértices que não têm arestas de entrada
    //esses vértices são usados como ponto de partida pra ordenação topológica
    private List<Vertice<T>> verticesSemArestaEntrada() {
        List<Vertice<T>> vertices = new ArrayList<>();
        for (Vertice<T> vertice : this.vertices) {
            boolean semAresta = false;
            for (Aresta<T> aresta : this.arestas) {
                if (aresta.getDestino().equals(vertice)) {
                    semAresta = true; //se encontrar uma aresta de entrada, marca como true e para a busca
                    break;
                }
            }
            if (!semAresta) {
                vertices.add(vertice);//adiciona à lista de vértices sem arestas de entrada
            }
        }
        return vertices;
    }

}
