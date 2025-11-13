package victor.trabalhoredeneural.redeneuralmlp.util;

import victor.trabalhoredeneural.redeneuralmlp.core.RedeNeural;
import victor.trabalhoredeneural.redeneuralmlp.model.ConjuntoDados;
import victor.trabalhoredeneural.redeneuralmlp.model.Instancia;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Calcula e armazena os dados da Matriz de Confusão.
 * Testa a rede treinada contra um conjunto de dados de teste.
 */
public class MatrizConfusao {

    // A matriz é um Mapa onde a Chave é a Classe Real (ex: "CA")
    // e o Valor é outro Mapa (Chave: Classe Predita, Valor: Contagem)
    // Ex: { "CA" -> { "CA": 13, "CB": 2, "CC": 0 ... },
    //       "CB" -> { "CA": 2, "CB": 10, "CC": 3 ... } }
    private final Map<String, Map<String, Integer>> matriz;
    private final List<String> listaClasses; // Lista ordenada de classes (para as colunas/linhas)

    /**
     * Construtor que gera a matriz de confusão.
     * @param rede A rede neural já treinada.
     * @param dadosTeste O conjunto de dados para teste.
     */
    public MatrizConfusao(RedeNeural rede, ConjuntoDados dadosTeste) {
        this.matriz = new TreeMap<>(); // TreeMap para manter as linhas (Classes Reais) ordenadas
        this.listaClasses = dadosTeste.getListaClassesUnicas(); // Pega "CA", "CB", ...

        // 1. Inicializa a matriz com zeros
        for (String classeReal : listaClasses) {
            Map<String, Integer> predicoes = new TreeMap<>(); // TreeMap para manter as colunas ordenadas
            for (String classePredita : listaClasses) {
                predicoes.put(classePredita, 0);
            }
            matriz.put(classeReal, predicoes);
        }

        // 2. Preenche a matriz testando cada instância
        for (Instancia inst : dadosTeste.getInstancias()) {
            String classeReal = inst.getClasse();

            // a. Normaliza as entradas de teste (usando min/max do TREINO)
            List<Double> entradasNormalizadas = rede.normalizar(inst.getEntradas());

            // b. Faz o feedforward (predição)
            List<Double> saidaRede = rede.feedforward(entradasNormalizadas);

            // c. Converte a saída (ex: [0.1, 0.8]) para um nome de classe (ex: "CB")
            String classePredita = rede.getClassificacao(saidaRede);

            // d. Incrementa o contador na matriz
            // Pega a linha (classeReal), depois a coluna (classePredita) e soma 1
            Map<String, Integer> linha = matriz.get(classeReal);
            int contagemAtual = linha.get(classePredita);
            linha.put(classePredita, contagemAtual + 1);
        }
    }

    // --- Getters ---

    /**
     * Retorna a lista ordenada de classes.
     * Usado para criar as colunas e linhas da tabela na UI.
     * @return Lista de nomes de classes.
     */
    public List<String> getListaClasses() {
        return listaClasses;
    }

    /**
     * Retorna a matriz de confusão completa.
     * @return O mapa da matriz.
     */
    public Map<String, Map<String, Integer>> getMatriz() {
        return matriz;
    }

    /**
     * Retorna o valor de uma célula específica.
     * @param classeReal A classe da linha.
     * @param classePredita A classe da coluna.
     * @return A contagem de predições.
     */
    public int getValor(String classeReal, String classePredita) {
        return matriz.get(classeReal).get(classePredita);
    }
}