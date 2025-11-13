package victor.trabalhoredeneural.redeneuralmlp;

// Imports do JavaFX
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart; // Import para o gráfico
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

// Imports do seu projeto
import victor.trabalhoredeneural.redeneuralmlp.core.RedeNeural;
import victor.trabalhoredeneural.redeneuralmlp.model.ConjuntoDados;
import victor.trabalhoredeneural.redeneuralmlp.util.MatrizConfusao;

// Imports do Java
import java.util.List;
import java.util.Map;

public class ResultadosController {

    @FXML
    private Label lblEpocas;
    @FXML
    private Label lblErroFinal;
    @FXML
    private TableView<Map.Entry<String, Map<String, Integer>>> tabelaMatriz;
    @FXML
    private LineChart<String, Number> graficoErro;

    /**
     * Este método é chamado pelo MainController para popular esta tela.
     */
    public void iniciar(RedeNeural rede, ConjuntoDados dadosTeste, double erroFinal,
                        int epocasTotal, List<Double> historicoErros) {

        // 1. Preencher os rótulos de estatísticas
        lblEpocas.setText(String.valueOf(epocasTotal));
        lblErroFinal.setText(String.format("%.10f", erroFinal)); // Formata o erro

        // 2. Criar e exibir a Matriz de Confusão
        MatrizConfusao matriz = new MatrizConfusao(rede, dadosTeste);
        construirTabelaMatriz(matriz); // <--- Método existe

        // 3. Popular o gráfico de erro
        popularGraficoErro(historicoErros); // <--- Método existe
    }

    /**
     * Constrói dinamicamente as colunas e linhas da Tabela da Matriz de Confusão.
     */
    private void construirTabelaMatriz(MatrizConfusao matriz) {
        // Pega a lista de classes (ex: "CA", "CB", ...)
        List<String> classes = matriz.getListaClasses();

        // 1. Criar a primeira coluna (Classe Real)
        TableColumn<Map.Entry<String, Map<String, Integer>>, String> colReal = new TableColumn<>("Real");

        // Define como pegar o valor: Pega o Map.Entry e usa a Chave (Key)
        colReal.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        tabelaMatriz.getColumns().add(colReal);

        // 2. Criar colunas dinâmicas (Classes Preditas)
        for (String classePredita : classes) {
            // Cria uma coluna com o nome da classe (ex: "CA")
            TableColumn<Map.Entry<String, Map<String, Integer>>, String> colPredita = new TableColumn<>(classePredita);

            // Define como pegar o valor:
            colPredita.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getValue().get(classePredita).toString())
            );
            tabelaMatriz.getColumns().add(colPredita);
        }

        // 3. Adicionar os dados (linhas) na tabela
        tabelaMatriz.getItems().setAll(matriz.getMatriz().entrySet());
    }

    /**
     * Popula o gráfico de linha com o histórico de erros.
     */
    private void popularGraficoErro(List<Double> historicoErros) {
        // 1. Criar uma série de dados (uma linha no gráfico)
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Erro Médio");

        // 2. Adicionar cada erro da lista à série
        for (int i = 0; i < historicoErros.size(); i++) {
            int epoca = i + 1;
            double erro = historicoErros.get(i);

            // Adiciona o ponto (X, Y) -> (Época, Erro)
            series.getData().add(new XYChart.Data<>(String.valueOf(epoca), erro));
        }

        // 3. Adicionar a série (linha) ao gráfico
        graficoErro.getData().add(series);
    }
}