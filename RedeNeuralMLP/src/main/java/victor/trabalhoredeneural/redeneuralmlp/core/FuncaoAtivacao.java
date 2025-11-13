package victor.trabalhoredeneural.redeneuralmlp.core;

import javafx.scene.control.RadioButton;

/**
 * Enum que encapsula as Funções de Transferência (Ativação) e suas derivadas.
 * Cada função (Linear, Logística, Hiperbólica) é uma instância deste enum
 * e implementa os métodos 'ativar' e 'derivada'.
 * * As fórmulas são baseadas no documento do trabalho.
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
            return net / 10.0; // [cite: 180]
        }

        @Override
        public double derivada(double saida) {
            // A 'saida' não é necessária aqui, a derivada é constante
            return 1.0 / 10.0; // [cite: 180]
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
            return 1.0 / (1.0 + Math.exp(-net)); // [cite: 181]
        }

        @Override
        public double derivada(double saida) {
            // 'saida' aqui é o f(net) que já foi calculado
            return saida * (1.0 - saida); // [cite: 182]
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
            // Math.tanh() é a implementação padrão do Java para a fórmula do PDF [cite: 190]
            return Math.tanh(net);
        }

        @Override
        public double derivada(double saida) {
            // 'saida' aqui é o f(net) que já foi calculado
            return 1.0 - (saida * saida); // [cite: 191]
        }
    };

    /**
     * Método abstrato para calcular a saída da função de ativação.
     * @param net O somatório ponderado das entradas do neurônio.
     * @return O valor de ativação (saída).
     */
    public abstract double ativar(double net);

    /**
     * Método abstrato para calcular a derivada da função de ativação.
     * Importante: A derivada é calculada com base na SAÍDA (o f(net) já calculado),
     * pois é mais eficiente (conforme fórmulas f'(net) = f(net)*(1-f(net))).
     * * @param saida O valor da SAÍDA do neurônio (o f(net)).
     * @return O valor da derivada.
     */
    public abstract double derivada(double saida);

    /**
     * Método utilitário para pegar a função selecionada nos RadioButtons da UI.
     * @param radioLinear RadioButton para Linear
     * @param radioLogistica RadioButton para Logística
     * @param radioHiperbolica RadioButton para Hiperbólica
     * @return A instância do enum FuncaoAtivacao correspondente.
     */
    public static FuncaoAtivacao getFuncaoSelecionada(RadioButton radioLinear, RadioButton radioLogistica, RadioButton radioHiperbolica) {
        if (radioLinear.isSelected()) {
            return LINEAR;
        }
        if (radioHiperbolica.isSelected()) {
            return HIPERBOLICA;
        }
        // Padrão é Logística
        return LOGISTICA;
    }
}