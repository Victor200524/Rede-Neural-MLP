package victor.trabalhoredeneural.redeneuralmlp.core;

import victor.trabalhoredeneural.redeneuralmlp.model.ConjuntoDados;
import victor.trabalhoredeneural.redeneuralmlp.model.Instancia;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * A classe principal que gerencia a Rede Neural Multilayer Perceptron (MLP).
 * Ela contém as camadas, a lógica de feedforward, backpropagation e o
 * loop de treinamento.
 */
public class RedeNeural {

    private final Camada camadaOculta;
    private final Camada camadaSaida;
    private final FuncaoAtivacao funcaoAtivacao;
    private double taxaAprendizado;

    // Mapa para converter nomes de classes (ex: "CA") em vetores (ex: [1, 0, 0])
    private final Map<String, List<Double>> mapaClasses;

    // Listas para guardar os min/max do conjunto de treino para normalização
    private final List<Double> minimos;
    private final List<Double> maximos;

    /**
     * Construtor da Rede Neural.
     * @param numEntrada Número de neurônios de entrada (ex: 6)
     * @param numOculta Número de neurônios na camada oculta
     * @param numSaida Número de neurônios na camada de saída (ex: 5 classes)
     * @param funcaoAtivacao O enum da função de ativação (Linear, Logistica, etc.)
     * @param taxaAprendizado O 'N' (eta)
     * @param dadosTreino O conjunto de dados de treino, usado para pegar os
     * min/max (para normalização) e a lista de classes.
     */
    public RedeNeural(int numEntrada, int numOculta, int numSaida,
                      FuncaoAtivacao funcaoAtivacao, double taxaAprendizado,
                      ConjuntoDados dadosTreino) {

        // Cria as camadas
        this.camadaOculta = new Camada(numOculta, numEntrada);
        this.camadaSaida = new Camada(numSaida, numOculta); // A entrada da Saída é a Oculta

        this.funcaoAtivacao = funcaoAtivacao;
        this.taxaAprendizado = taxaAprendizado;

        // Armazena os valores min/max para normalização
        this.minimos = dadosTreino.getMinimos();
        this.maximos = dadosTreino.getMaximos();

        // Cria o mapa de classes
        this.mapaClasses = new TreeMap<>(); // TreeMap para manter ordenado
        List<String> listaClasses = dadosTreino.getListaClassesUnicas();
        for (int i = 0; i < listaClasses.size(); i++) {
            String classe = listaClasses.get(i);
            List<Double> vetorAlvo = new ArrayList<>();
            for (int j = 0; j < numSaida; j++) {
                vetorAlvo.add((i == j) ? 1.0 : 0.0); // Ex: [1, 0, 0] para a classe 0
            }
            this.mapaClasses.put(classe, vetorAlvo);
        }

        System.out.println("Mapa de classes criado: " + this.mapaClasses);
    }

    /**
     * Etapa de Feedforward: Executa uma entrada pela rede.
     * @param entradas A lista de entradas (já normalizadas).
     * @return A lista de saídas da camada de saída.
     */
    public List<Double> feedforward(List<Double> entradas) {
        // 1. Passa pela camada oculta
        List<Double> saidasOculta = camadaOculta.calcularSaidas(entradas, funcaoAtivacao);

        // 2. Passa pela camada de saída
        List<Double> saidasFinal = camadaSaida.calcularSaidas(saidasOculta, funcaoAtivacao);

        return saidasFinal;
    }

    /**
     * Etapa de Backpropagation: Calcula erros e atualiza pesos.
     * @param entradas A lista de entradas (já normalizadas).
     * @param vetorAlvo O vetor de saída desejado (ex: [1, 0, 0]).
     */
    public void backpropagation(List<Double> entradas, List<Double> vetorAlvo) {

        // 1. Calcular Gradientes (Deltas) da Camada de Saída
        for (int i = 0; i < camadaSaida.getNeuronios().size(); i++) {
            Neuronio neuronio = camadaSaida.getNeuronio(i);
            double alvo = vetorAlvo.get(i);
            neuronio.calcularGradienteSaida(alvo, funcaoAtivacao);
        }

        // 2. Calcular Gradientes (Deltas) da Camada Oculta
        for (int i = 0; i < camadaOculta.getNeuronios().size(); i++) {
            Neuronio neuronio = camadaOculta.getNeuronio(i);
            neuronio.calcularGradienteOculta(camadaSaida, i, funcaoAtivacao);
        }

        // 3. Atualizar Pesos da Camada de Saída
        // A entrada para a camada de saída são as saídas da camada oculta
        List<Double> saidasOculta = new ArrayList<>();
        for (Neuronio n : camadaOculta.getNeuronios()) {
            saidasOculta.add(n.getSaida());
        }

        for (Neuronio neuronio : camadaSaida.getNeuronios()) {
            neuronio.atualizarPesos(saidasOculta, taxaAprendizado);
        }

        // 4. Atualizar Pesos da Camada Oculta
        // A entrada para a camada oculta são as entradas da rede
        for (Neuronio neuronio : camadaOculta.getNeuronios()) {
            neuronio.atualizarPesos(entradas, taxaAprendizado);
        }
    }

    /**
     * Normaliza uma lista de valores de entrada.
     * Usa os Mínimos e Máximos guardados do conjunto de treino.
     * @param entradas A lista de entradas "cruas".
     * @return A lista de entradas normalizadas (0 a 1).
     */
    public List<Double> normalizar(List<Double> entradas) {
        List<Double> entradasNormalizadas = new ArrayList<>();
        for (int i = 0; i < entradas.size(); i++) {
            double min = minimos.get(i);
            double max = maximos.get(i);
            double valor = entradas.get(i);

            // --- INÍCIO DA CORREÇÃO DO BUG ---

            double denominador = max - min;
            double normalizado;

            if (denominador == 0) {
                // Se max == min (ou seja, todos os valores da coluna são iguais),
                // definimos como 0.0 para evitar a divisão por zero (NaN).
                normalizado = 0.0;
            } else {
                // Fórmula de Normalização: (Valor - Min) / (Max - Min)
                normalizado = (valor - min) / denominador;
            }

            // --- FIM DA CORREÇÃO DO BUG ---

            entradasNormalizadas.add(normalizado);
        }
        return entradasNormalizadas;
    }

    /**
     * Converte um nome de classe (ex: "CA") no vetor alvo (ex: [1, 0, 0]).
     * @param classe O nome da classe.
     * @return O vetor alvo.
     */
    public List<Double> getVetorAlvo(String classe) {
        return mapaClasses.get(classe);
    }

    /**
     * Retorna o nome da classe com base na saída da rede.
     * (Usado para testes)
     * @param saidaDaRede A lista de saídas (ex: [0.1, 0.8, 0.05])
     * @return O nome da classe (ex: "CB")
     */
    public String getClassificacao(List<Double> saidaDaRede) {
        int indiceMax = 0;
        double valorMax = -1;

        // Encontra o neurônio de saída com o maior valor
        for (int i = 0; i < saidaDaRede.size(); i++) {
            if (saidaDaRede.get(i) > valorMax) {
                valorMax = saidaDaRede.get(i);
                indiceMax = i;
            }
        }

        // Encontra qual classe corresponde a esse índice
        for (Map.Entry<String, List<Double>> entry : mapaClasses.entrySet()) {
            if (entry.getValue().get(indiceMax) == 1.0) {
                return entry.getKey();
            }
        }
        return "Desconhecida"; // Não deve acontecer
    }
    public void setTaxaAprendizado(double novaTaxa) {
        this.taxaAprendizado = novaTaxa;
        System.out.println(">>> Taxa de aprendizado reduzida para: " + this.taxaAprendizado);
    }
}