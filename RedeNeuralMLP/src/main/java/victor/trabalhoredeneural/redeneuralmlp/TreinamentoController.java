package victor.trabalhoredeneural.redeneuralmlp;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.concurrent.CompletableFuture;

public class TreinamentoController {

    // Enum para as ações do usuário
    public enum AcaoPlato {
        PARAR,
        CONTINUAR,
        REDUZIR_TAXA
    }

    @FXML
    private Label lblEpocaAtual;
    @FXML
    private Label lblErroAtual;
    @FXML
    private VBox boxPlato;

    // Um "Future" é uma promessa de que daremos uma resposta.
    // A thread de treino vai PAUSAR e esperar por esta resposta.
    private CompletableFuture<AcaoPlato> acaoPlatoFuture;

    /**
     * Atualiza os labels de status (Época e Erro).
     * Este método é chamado PELA THREAD DE TREINAMENTO.
     */
    public void atualizarStatus(int epoca, double erro) {
        // Platform.runLater é OBRIGATÓRIO para atualizar a UI
        // a partir de outra thread.
        Platform.runLater(() -> {
            lblEpocaAtual.setText(String.valueOf(epoca));
            lblErroAtual.setText(String.format("%.10f", erro));
        });
    }

    /**
     * Mostra a caixa de diálogo do platô e espera uma resposta.
     * @return Um "Future" que será completado com a ação do usuário.
     */
    public CompletableFuture<AcaoPlato> aguardarAcaoPlato() {
        acaoPlatoFuture = new CompletableFuture<>();

        // Mostra a caixa de diálogo na UI thread
        Platform.runLater(() -> boxPlato.setVisible(true));

        return acaoPlatoFuture;
    }

    // --- Métodos de Clique dos Botões ---

    @FXML
    private void onPararClick() {
        // Envia a resposta "PARAR" para a thread de treino
        acaoPlatoFuture.complete(AcaoPlato.PARAR);
        esconderBoxPlato();
    }

    @FXML
    private void onContinuarClick() {
        // Envia a resposta "CONTINUAR"
        acaoPlatoFuture.complete(AcaoPlato.CONTINUAR);
        esconderBoxPlato();
    }

    @FXML
    private void onReduzirClick() {
        // Envia a resposta "REDUZIR_TAXA"
        acaoPlatoFuture.complete(AcaoPlato.REDUZIR_TAXA);
        esconderBoxPlato();
    }

    private void esconderBoxPlato() {
        boxPlato.setVisible(false);
    }
}