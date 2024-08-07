import java.util.*;

public class Grafo<T> {

    private List<Vertice<T>> vertices = new ArrayList<>();
    private List<Aresta<T>> arestas = new ArrayList<>();

    public Grafo() {
        this.vertices = new ArrayList<>();
    }

    public List<Vertice<T>> getVertices() {
        return this.vertices;
    }

    public List<Aresta<T>> getArestas() {
        return this.arestas;
    }

    public Vertice<T> adicionaVertice(T valor) {
        Vertice<T> novo = new Vertice<>(valor);
        this.vertices.add(novo);
        return novo;
    }

    public Vertice<T> obterVertice(T valor) {
        for (Vertice<T> vertice : this.getVertices()) {
            if (vertice.getValor().equals(valor))
                return vertice;
        }
        return null;
    }

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

    public Grafo<T> calcularArvoreGeradoraMinima() {
        Grafo<T> arvoreGeradoraMinima = new Grafo<>();

        Map<Vertice<T>, Vertice<T>> parent = new HashMap<>();
        Map<Vertice<T>, Integer> rank = new HashMap<>();

        for (Vertice<T> vertice : vertices) {
            arvoreGeradoraMinima.adicionaVertice(vertice.getValor());
            parent.put(vertice, vertice);
            rank.put(vertice, 0);
        }

        List<Aresta<T>> arestasOrdenadas = new ArrayList<>(arestas);
        arestasOrdenadas.sort(Comparator.comparingDouble(Aresta::getPeso));

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

    private Vertice<T> findSet(Map<Vertice<T>, Vertice<T>> parent, Vertice<T> vertice) {
        if (!vertice.equals(parent.get(vertice))) {
            parent.put(vertice, findSet(parent, parent.get(vertice)));
        }
        return parent.get(vertice);
    }

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

            for (Aresta<T> aresta : arestas) {
                if (aresta.getOrigem().equals(atual)) {
                    Vertice<T> vizinho = aresta.getDestino();
                    float novaDistancia = distancias.get(atual) + aresta.getPeso();
                    if (novaDistancia < distancias.get(vizinho)) {
                        distancias.put(vizinho, novaDistancia);
                        anteriores.put(vizinho, atual);
                        fila.add(vizinho);
                    }
                }
            }
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

    private List<Vertice<T>> verticesSemArestaEntrada() {
        List<Vertice<T>> vertices = new ArrayList<>();
        for (Vertice<T> vertice : this.vertices) {
            boolean semAresta = false;
            for (Aresta<T> aresta : this.arestas) {
                if (aresta.getDestino().equals(vertice)) {
                    semAresta = true;
                    break;
                }
            }
            if (!semAresta) {
                vertices.add(vertice);
            }
        }
        return vertices;
    }

}