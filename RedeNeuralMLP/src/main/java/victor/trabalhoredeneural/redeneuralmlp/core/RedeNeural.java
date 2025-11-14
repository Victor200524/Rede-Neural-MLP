package victor.trabalhoredeneural.redeneuralmlp.core;

import victor.trabalhoredeneural.redeneuralmlp.model.ConjuntoDados;

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

    //Executa uma entrada pela rede
    public List<Double> feedforward(List<Double> entradas) {
        //Passa pela camada oculta
        List<Double> saidasOculta = camadaOculta.calcularSaidas(entradas, funcaoAtivacao);

        //Passa pela camada de saída
        List<Double> saidasFinal = camadaSaida.calcularSaidas(saidasOculta, funcaoAtivacao);

        return saidasFinal;
    }

    //Calcula erros e atualiza pesos
    public void backpropagation(List<Double> entradas, List<Double> vetorAlvo) {

        //Calcular Gradientes (Deltas) da Camada de Saída
        for (int i = 0; i < camadaSaida.getNeuronios().size(); i++) {
            Neuronio neuronio = camadaSaida.getNeuronio(i);
            double alvo = vetorAlvo.get(i);
            neuronio.calcularGradienteSaida(alvo, funcaoAtivacao);
        }

        //Calcular Gradientes da Camada Oculta
        for (int i = 0; i < camadaOculta.getNeuronios().size(); i++) {
            Neuronio neuronio = camadaOculta.getNeuronio(i);
            neuronio.calcularGradienteOculta(camadaSaida, i, funcaoAtivacao);
        }

        // Atualizar Pesos da Camada de Saída
        // A entrada para a camada de saída são as saídas da camada oculta
        List<Double> saidasOculta = new ArrayList<>();
        for (Neuronio n : camadaOculta.getNeuronios()) {
            saidasOculta.add(n.getSaida());
        }

        for (Neuronio neuronio : camadaSaida.getNeuronios()) {
            neuronio.atualizarPesos(saidasOculta, taxaAprendizado);
        }

        // Atualizar Pesos da Camada Oculta
        // A entrada para a camada oculta são as entradas da rede
        for (Neuronio neuronio : camadaOculta.getNeuronios()) {
            neuronio.atualizarPesos(entradas, taxaAprendizado);
        }
    }

    // Normaliza uma lista de valores de entrada, usa os Mínimos e Máximos guardados do conjunto de treino.

    public List<Double> normalizar(List<Double> entradas) {
        List<Double> entradasNormalizadas = new ArrayList<>();
        for (int i = 0; i < entradas.size(); i++) {
            double min = minimos.get(i);
            double max = maximos.get(i);
            double valor = entradas.get(i);

            double denominador = max - min;
            double normalizado;

            if (denominador == 0) {
                // Se max == min (ou seja, todos os valores da coluna são iguais)
                // definimos como 0.0 para evitar a divisão por zero (NaN)
                normalizado = 0.0;
            }
            else // Fórmula de Normalização: (Valor - Min) / (Max - Min)
                normalizado = (valor - min) / denominador;
            entradasNormalizadas.add(normalizado);
        }
        return entradasNormalizadas;
    }

    // Converte um nome de classe (ex: "CA") no vetor alvo (ex: [1, 0, 0])
    public List<Double> getVetorAlvo(String classe) {
        return mapaClasses.get(classe);
    }

    //Retorna o nome da classe com base na saída da rede
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