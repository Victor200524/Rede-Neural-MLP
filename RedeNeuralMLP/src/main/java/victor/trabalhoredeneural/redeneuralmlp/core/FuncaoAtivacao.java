package victor.trabalhoredeneural.redeneuralmlp.core;

import javafx.scene.control.RadioButton;

/**
 * Enum que encapsula as Funções de Transferência (Ativação) e suas derivadas
 * Cada função (Linear, Logística, Hiperbólica) é uma instância deste enum
 * e implementa os métodos 'ativar' e 'derivada'.
 * * As fórmulas são baseadas no documento do trabalho
 */
public enum FuncaoAtivacao {

    /**
     * Função Linear
     * f(net) = net / 10
     * f'(net) = 1 / 10
     */
    LINEAR {
        @Override
        public double ativar(double net) {
            return net / 10.0;
        }

        @Override
        public double derivada(double saida) {
            return 1.0 / 10.0;
        }
    },

    /**
     * Função Logística (Sigmoid)
     * f(net) = 1 / (1 + e^(-net))
     * f'(net) = f(net) * (1 - f(net))
     */
    LOGISTICA {
        @Override
        public double ativar(double net) {
            return 1.0 / (1.0 + Math.exp(-net));
        }

        @Override
        public double derivada(double saida) {
            return saida * (1.0 - saida);
        }
    },

    /**
     * Função Tangente Hiperbólica (tanh)
     * f(net) = (1 - e^(-2*net)) / (1 + e^(-2*net))  (Equivalente a Math.tanh(net))
     * f'(net) = 1 - (f(net)^2)
     */
    HIPERBOLICA {
        @Override
        public double ativar(double net) {
            return Math.tanh(net);
        }

        @Override
        public double derivada(double saida) {
            return 1.0 - (saida * saida);
        }
    };

    //Metodo abstrato para calcular a saida da função de ativação
    public abstract double ativar(double net);

    //Metodo abstrato para calcular a derivada da função de ativação
    public abstract double derivada(double saida);

    //Metodo utilitário para pegar a função selecionada nos RadioButtons da UI
    public static FuncaoAtivacao getFuncaoSelecionada(RadioButton radioLinear, RadioButton radioLogistica, RadioButton radioHiperbolica) {
        if (radioLinear.isSelected()) {
            return LINEAR;
        }
        if (radioHiperbolica.isSelected()) {
            return HIPERBOLICA;
        }
        return LOGISTICA;
    }
}