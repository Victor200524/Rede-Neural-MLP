package victor.trabalhoredeneural.redeneuralmlp.model;

import victor.trabalhoredeneural.redeneuralmlp.core.RedeNeural;

// 1. ADICIONE O IMPORT DA LISTA
import java.util.List;

/**
 * POJO simples para carregar os resultados do treinamento da Task (thread)
 * de volta para o MainController (UI thread).
 */
public class ResultadoTreinamento {

    private final RedeNeural redeTreinada;
    private final double erroFinal;
    private final int epocasTotal;

    // 2. ADICIONE A LISTA DE ERROS
    private final List<Double> historicoErros; // Lista com o erro de cada época

    // 3. ATUALIZE O CONSTRUTOR
    public ResultadoTreinamento(RedeNeural redeTreinada, double erroFinal, int epocasTotal, List<Double> historicoErros) {
        this.redeTreinada = redeTreinada;
        this.erroFinal = erroFinal;
        this.epocasTotal = epocasTotal;
        this.historicoErros = historicoErros; // Adicione esta linha
    }

    // --- Getters ---
    public RedeNeural getRedeTreinada() {
        return redeTreinada;
    }

    public double getErroFinal() {
        return erroFinal;
    }

    public int getEpocasTotal() {
        return epocasTotal;
    }

    // 4. ADICIONE O GETTER PARA A LISTA
    public List<Double> getHistoricoErros() {
        return historicoErros;
    }
}