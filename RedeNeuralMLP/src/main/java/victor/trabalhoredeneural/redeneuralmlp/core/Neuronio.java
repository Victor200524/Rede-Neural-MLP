package victor.trabalhoredeneural.redeneuralmlp.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Representa um unico neuronio na rede
 * Cada neuronio mantem sua lista de pesos, seu ultimo valor de saida
 * e seu gradiente de erro (delta) para o backpropagation
 */
public class Neuronio {

    private final List<Double> pesos; // Lista de pesos (incluindo o peso do bias no índice 0)
    private double saida;             // Última saída calculada (f(net))
    private double gradiente;         // Gradiente de erro (delta / δ)

    public Neuronio(int numEntradas) {
        this.pesos = new ArrayList<>();
        Random rand = new Random();

        // Inicializa os pesos (numEntradas + 1 para o bias) com valores aleatórios pequenos
        // O peso 0 (this.pesos.get(0)) será o peso do BIAS
        for (int i = 0; i <= numEntradas; i++) {
            // Valores aleatórios entre -0.5 e +0.5
            pesos.add(rand.nextDouble() - 0.5);
        }
    }

    //Etapa de Feedforward: Calcula a saída do neurônio
    public double calcularSaida(List<Double> entradas, FuncaoAtivacao funcao) {
        double net = calcularNet(entradas);
        this.saida = funcao.ativar(net); // Armazena a saída
        return this.saida;
    }

    //Calcula o somatório ponderado
    private double calcularNet(List<Double> entradas) {
        // Começa com o peso do bias (multiplicado por uma entrada constante de 1.0)
        double net = pesos.get(0); // peso_bias * 1.0

        // Soma os outros pesos multiplicados pelas entradas
        for (int i = 1; i < pesos.size(); i++) {
            net += pesos.get(i) * entradas.get(i - 1); // peso_i * entrada_i
        }
        return net;
    }

    //Calcula o gradiente (delta) para neurônios da camada de saída
    public void calcularGradienteSaida(double valorDesejado, FuncaoAtivacao funcao) {
        // Fórmula: (Desejado - Saida) * f'(net)
        // Onde f'(net) é calculado usando a 'saida' (f(net))
        double erro = valorDesejado - this.saida;
        this.gradiente = erro * funcao.derivada(this.saida);
    }

    //Etapa de Backpropagation: Calcula o gradiente (delta) para neurônios da camada oculta
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

    //Atualiza todos os pesos deste neurônio
    public void atualizarPesos(List<Double> entradas, double taxaAprendizado) {
        // Fórmula: novo_peso = peso_antigo + (N * gradiente * entrada)

        //Atualiza o peso do Bias (entrada é 1.0)
        double novoPesoBias = pesos.get(0) + (taxaAprendizado * this.gradiente * 1.0);
        pesos.set(0, novoPesoBias);

        // 2. Atualiza os demais pesos
        for (int i = 1; i < pesos.size(); i++) {
            double entrada = entradas.get(i - 1);
            double novoPeso = pesos.get(i) + (taxaAprendizado * this.gradiente * entrada);
            pesos.set(i, novoPeso);
        }
    }

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