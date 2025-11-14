package victor.trabalhoredeneural.redeneuralmlp.model;

import victor.trabalhoredeneural.redeneuralmlp.core.RedeNeural;

import java.util.List;

/**
 * POJO simples para carregar os resultados do treinamento da Task (thread)
 * de volta para o MainController (UI thread)
 */
public class ResultadoTreinamento {

    private final RedeNeural redeTreinada;
    private final double erroFinal;
    private final int epocasTotal;

    private final List<Double> historicoErros;

    public ResultadoTreinamento(RedeNeural redeTreinada, double erroFinal, int epocasTotal, List<Double> historicoErros) {
        this.redeTreinada = redeTreinada;
        this.erroFinal = erroFinal;
        this.epocasTotal = epocasTotal;
        this.historicoErros = historicoErros; // Adicione esta linha
    }

    public RedeNeural getRedeTreinada() {
        return redeTreinada;
    }

    public double getErroFinal() {
        return erroFinal;
    }

    public int getEpocasTotal() {
        return epocasTotal;
    }

    public List<Double> getHistoricoErros() {
        return historicoErros;
    }
}