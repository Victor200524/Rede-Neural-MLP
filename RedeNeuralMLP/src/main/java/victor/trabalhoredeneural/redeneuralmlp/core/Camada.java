package victor.trabalhoredeneural.redeneuralmlp.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa uma camada da Rede Neural (Oculta ou Saída).
 * Uma camada é simplesmente uma lista de neurônios.
 */
public class Camada {

    private final List<Neuronio> neuronios;

    /**
     * Construtor da Camada.
     * @param numNeuronios O número de neurônios nesta camada.
     * @param numEntradasPorNeuronio O número de entradas que cada neurônio
     * desta camada receberá (ou seja, o número de
     * neurônios da camada anterior).
     */
    public Camada(int numNeuronios, int numEntradasPorNeuronio) {
        this.neuronios = new ArrayList<>();

        // Cria os neurônios para esta camada
        for (int i = 0; i < numNeuronios; i++) {
            neuronios.add(new Neuronio(numEntradasPorNeuronio));
        }
    }

    /**
     * Etapa de Feedforward: Calcula a saída de TODOS os neurônios desta camada.
     * @param entradas A lista de saídas da camada anterior.
     * @param funcao A função de ativação a ser usada.
     * @return Uma lista de Doubles com as saídas de cada neurônio.
     */
    public List<Double> calcularSaidas(List<Double> entradas, FuncaoAtivacao funcao) {
        List<Double> saidasDaCamada = new ArrayList<>();

        for (Neuronio n : neuronios) {
            saidasDaCamada.add(n.calcularSaida(entradas, funcao));
        }

        return saidasDaCamada;
    }

    // --- Getters ---

    /**
     * Retorna a lista de neurônios desta camada.
     * (Este é o método que corrige o erro no Neuronio.java)
     * @return A lista de neurônios.
     */
    public List<Neuronio> getNeuronios() {
        return neuronios;
    }

    /**
     * Retorna um neurônio específico pelo seu índice.
     * @param indice O índice do neurônio.
     * @return O neurônio.
     */
    public Neuronio getNeuronio(int indice) {
        return neuronios.get(indice);
    }
}