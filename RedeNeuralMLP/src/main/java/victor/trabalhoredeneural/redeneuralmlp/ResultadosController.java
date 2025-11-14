package victor.trabalhoredeneural.redeneuralmlp;

// Imports do JavaFX
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
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

    //Este metodo é chamado pelo MainController para popular esta tela
    public void iniciar(RedeNeural rede, ConjuntoDados dadosTeste, double erroFinal,
                        int epocasTotal, List<Double> historicoErros) {

        // Preenche os rótulos de estatísticas
        lblEpocas.setText(String.valueOf(epocasTotal));
        lblErroFinal.setText(String.format("%.10f", erroFinal)); // Formata o erro

        // Cria e exibir a Matriz de Confusão
        MatrizConfusao matriz = new MatrizConfusao(rede, dadosTeste);
        construirTabelaMatriz(matriz);

        // Popula o gráfico de erro
        popularGraficoErro(historicoErros);
    }

    // Constrói dinamicamente as colunas e linhas da Tabela da Matriz de Confusão
    private void construirTabelaMatriz(MatrizConfusao matriz) {
        // Pega a lista de classes (ex: "CA", "CB", ...)
        List<String> classes = matriz.getListaClasses();

        // Cria a primeira coluna (Classe Real)
        TableColumn<Map.Entry<String, Map<String, Integer>>, String> colReal = new TableColumn<>("Real");

        // Define como pegar o valor: Pega o Map.Entry e usa a Chave (Key)
        colReal.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        tabelaMatriz.getColumns().add(colReal);

        // Cria colunas dinâmicas (Classes Preditas)
        for (String classePredita : classes) {
            // Cria uma coluna com o nome da classe (ex: "CA")
            TableColumn<Map.Entry<String, Map<String, Integer>>, String> colPredita = new TableColumn<>(classePredita);

            // Define como pegar o valor:
            colPredita.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getValue().get(classePredita).toString())
            );
            tabelaMatriz.getColumns().add(colPredita);
        }

        // Adiciona os dados (linhas) na tabela
        tabelaMatriz.getItems().setAll(matriz.getMatriz().entrySet());
    }

    // Popula o gráfico de linha com o histórico de erros
    private void popularGraficoErro(List<Double> historicoErros) {
        // Cria uma série de dados (uma linha no gráfico)
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Erro Médio");

        // Adiciona cada erro da lista à série
        for (int i = 0; i < historicoErros.size(); i++) {
            int epoca = i + 1;
            double erro = historicoErros.get(i);

            // Adiciona o ponto (X, Y) -> (Época, Erro)
            series.getData().add(new XYChart.Data<>(String.valueOf(epoca), erro));
        }

        // Adiciona a série linha ao gráfico
        graficoErro.getData().add(series);
    }
}