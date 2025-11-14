package victor.trabalhoredeneural.redeneuralmlp.core;

import java.util.ArrayList;
import java.util.List;

public class Camada {

    private final List<Neuronio> neuronios;

    //Faz a construção da camada
    public Camada(int numNeuronios, int numEntradasPorNeuronio) {
        this.neuronios = new ArrayList<>();

        // Cria os neurônios para esta camada
        for (int i = 0; i < numNeuronios; i++) {
            neuronios.add(new Neuronio(numEntradasPorNeuronio));
        }
    }

    //Calcula a saída de TODOS os neurônios desta camada
    public List<Double> calcularSaidas(List<Double> entradas, FuncaoAtivacao funcao) {
        List<Double> saidasDaCamada = new ArrayList<>();

        for (Neuronio n : neuronios) {
            saidasDaCamada.add(n.calcularSaida(entradas, funcao));
        }

        return saidasDaCamada;
    }

    public List<Neuronio> getNeuronios() {
        return neuronios;
    }

    //Retorna um neurônio específico pelo seu índice
    public Neuronio getNeuronio(int indice) {
        return neuronios.get(indice);
    }
}