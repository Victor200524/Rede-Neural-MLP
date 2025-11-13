package victor.trabalhoredeneural.redeneuralmlp.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Representa um único neurônio na rede.
 * Cada neurônio mantém sua lista de pesos, seu último valor de saída
 * e seu gradiente de erro (delta) para o backpropagation.
 */
public class Neuronio {

    private final List<Double> pesos; // Lista de pesos (incluindo o peso do bias no índice 0)
    private double saida;             // Última saída calculada (f(net))
    private double gradiente;         // Gradiente de erro (delta / δ)

    /**
     * Construtor para criar um neurônio.
     * @param numEntradas O número de entradas que este neurônio receberá
     * (sem contar o bias).
     */
    public Neuronio(int numEntradas) {
        this.pesos = new ArrayList<>();
        Random rand = new Random();

        // Inicializa os pesos (numEntradas + 1 para o bias) com valores aleatórios pequenos
        // O peso 0 (this.pesos.get(0)) será o peso do BIAS.
        for (int i = 0; i <= numEntradas; i++) {
            // Valores aleatórios entre -0.5 e +0.5
            pesos.add(rand.nextDouble() - 0.5);
        }
    }

    /**
     * Etapa de Feedforward: Calcula a saída do neurônio.
     * @param entradas A lista de entradas da camada anterior.
     * @param funcao A função de ativação a ser usada.
     * @return A saída calculada (f(net)).
     */
    public double calcularSaida(List<Double> entradas, FuncaoAtivacao funcao) {
        double net = calcularNet(entradas);
        this.saida = funcao.ativar(net); // Armazena a saída
        return this.saida;
    }

    /**
     * Calcula o somatório ponderado (net).
     * @param entradas A lista de entradas.
     * @return O valor de 'net'.
     */
    private double calcularNet(List<Double> entradas) {
        // Começa com o peso do bias (multiplicado por uma entrada constante de 1.0)
        double net = pesos.get(0); // peso_bias * 1.0

        // Soma os outros pesos multiplicados pelas entradas
        for (int i = 1; i < pesos.size(); i++) {
            net += pesos.get(i) * entradas.get(i - 1); // peso_i * entrada_i
        }
        return net;
    }

    /**
     * Etapa de Backpropagation: Calcula o gradiente (delta) para neurônios da CAMADA DE SAÍDA.
     * @param valorDesejado O valor alvo (ex: 1.0 ou 0.0) para este neurônio.
     * @param funcao A função de ativação que foi usada.
     */
    public void calcularGradienteSaida(double valorDesejado, FuncaoAtivacao funcao) {
        // Fórmula: (Desejado - Saida) * f'(net)
        // Onde f'(net) é calculado usando a 'saida' (f(net))
        double erro = valorDesejado - this.saida;
        this.gradiente = erro * funcao.derivada(this.saida);
    }

    /**
     * Etapa de Backpropagation: Calcula o gradiente (delta) para neurônios da CAMADA OCULTA.
     * @param camadaSeguinte A camada da frente (ex: camada de saída).
     * @param indiceNeuronio O índice deste neurônio (para saber qual peso pegar na camada da frente).
     * @param funcao A função de ativação que foi usada.
     */
    public void calcularGradienteOculta(Camada camadaSeguinte, int indiceNeuronio, FuncaoAtivacao funcao) {
        // Fórmula: (Σ (gradiente_seguinte * peso_correspondente)) * f'(net)

        double somaGradientesPonderados = 0.0;
        for (Neuronio n : camadaSeguinte.getNeuronios()) {
            // O peso [indiceNeuronio + 1] do neurônio da frente é o que se conecta a este neurônio
            // (+1 pois o índice 0 é o bias)
            somaGradientesPonderados += n.getGradiente() * n.getPeso(indiceNeuronio + 1);
        }

        this.gradiente = somaGradientesPonderados * funcao.derivada(this.saida);
    }

    /**
     * Etapa de Backpropagation: Atualiza todos os pesos deste neurônio.
     * @param entradas As entradas que este neurônio recebeu na etapa de feedforward.
     * @param taxaAprendizado O 'N' (eta) definido pelo usuário.
     */
    public void atualizarPesos(List<Double> entradas, double taxaAprendizado) {
        // Fórmula: novo_peso = peso_antigo + (N * gradiente * entrada)

        // 1. Atualiza o peso do Bias (entrada é 1.0)
        double novoPesoBias = pesos.get(0) + (taxaAprendizado * this.gradiente * 1.0);
        pesos.set(0, novoPesoBias);

        // 2. Atualiza os demais pesos
        for (int i = 1; i < pesos.size(); i++) {
            double entrada = entradas.get(i - 1);
            double novoPeso = pesos.get(i) + (taxaAprendizado * this.gradiente * entrada);
            pesos.set(i, novoPeso);
        }
    }

    // --- Getters ---

    public double getSaida() {
        return saida;
    }

    public double getGradiente() {
        return gradiente;
    }

    public double getPeso(int indice) {
        return pesos.get(indice);
    }

    public List<Double> getPesos() {
        return pesos;
    }
}