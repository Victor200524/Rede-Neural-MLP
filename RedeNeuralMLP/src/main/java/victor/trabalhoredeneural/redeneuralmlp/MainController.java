package victor.trabalhoredeneural.redeneuralmlp;

// Imports de UI e FXML
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Pair; // Import para o Pair

// Imports da lógica
import victor.trabalhoredeneural.redeneuralmlp.core.FuncaoAtivacao;
import victor.trabalhoredeneural.redeneuralmlp.core.RedeNeural;
import victor.trabalhoredeneural.redeneuralmlp.model.ConjuntoDados;
import victor.trabalhoredeneural.redeneuralmlp.model.Instancia;
import victor.trabalhoredeneural.redeneuralmlp.model.ResultadoTreinamento;
import victor.trabalhoredeneural.redeneuralmlp.util.LeitorCSV;

// Imports de arquivos e concorrência (Threads)
import java.io.File;
import java.io.IOException;
import java.util.*; // Import para List, Collections, etc.
import java.util.concurrent.ExecutionException;

// Import da Task
import javafx.concurrent.Task;


public class MainController {

    // --- Variáveis de Configuração da Rede (ligadas ao FXML) ---
    @FXML
    private TextField txtEntrada;
    @FXML
    private TextField txtSaida;
    @FXML
    private TextField txtOculta;
    @FXML
    private TextField txtErro;
    @FXML
    private TextField txtEpocas;
    @FXML
    private TextField txtTaxaAprendizado;
    @FXML
    private ToggleGroup funcaoTransferenciaGroup;
    @FXML
    private RadioButton radioLinear;
    @FXML
    private RadioButton radioLogistica;
    @FXML
    private RadioButton radioHiperbolica;

    // --- Variáveis de Carregamento de Arquivo (ligadas ao FXML) ---
    @FXML
    private CheckBox checkArquivoUnico;
    @FXML
    private Button btnCarregarPrincipal;
    @FXML
    private Label lblArquivoPrincipal;
    @FXML
    private Button btnCarregarTeste;
    @FXML
    private Label lblArquivoTeste;
    @FXML
    private TableView<Instancia> tableViewDados;
    @FXML
    private TableColumn<Instancia, Double> colX1;
    @FXML
    private TableColumn<Instancia, Double> colX2;
    @FXML
    private TableColumn<Instancia, Double> colX3;
    @FXML
    private TableColumn<Instancia, Double> colX4;
    @FXML
    private TableColumn<Instancia, Double> colX5;
    @FXML
    private TableColumn<Instancia, Double> colX6;
    @FXML
    private TableColumn<Instancia, String> colClasse;

    // --- Botão de Ação ---
    @FXML
    private Button btnIniciar;

    // --- Variáveis de Lógica ---
    // Agora guardam os arquivos carregados
    private ConjuntoDados dadosCarregadoPrincipal;
    private ConjuntoDados dadosCarregadoTeste;

    // Variável para guardar o CONTROLLER da janela de treinamento
    private TreinamentoController treinamentoController;
    private Stage janelaTreinamento;


    @FXML
    public void initialize() {
        // Configura as colunas da tabela
        colX1.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getEntradas().get(0)).asObject());
        colX2.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getEntradas().get(1)).asObject());
        colX3.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getEntradas().get(2)).asObject());
        colX4.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getEntradas().get(3)).asObject());
        colX5.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getEntradas().get(4)).asObject());
        colX6.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getEntradas().get(5)).asObject());
        colClasse.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClasse()));
    }

    // --- Métodos de Clique dos Botões ---

    @FXML
    protected void onCheckArquivoUnicoClick() {
        boolean selecionado = checkArquivoUnico.isSelected();

        // Desabilita o botão de teste se selecionado
        btnCarregarTeste.setDisable(selecionado);

        if (selecionado) {
            btnCarregarPrincipal.setText("Carregar Arquivo Único");
            lblArquivoPrincipal.setText("Nenhum arquivo carregado.");
            lblArquivoTeste.setText("Usando modo de divisão 70/30");
        } else {
            btnCarregarPrincipal.setText("Carregar Arquivo de Treino");
            lblArquivoPrincipal.setText("Nenhum arquivo de treino carregado.");
            lblArquivoTeste.setText("Nenhum arquivo de teste carregado.");
        }

        // Reseta os dados carregados ao mudar de modo
        dadosCarregadoPrincipal = null;
        dadosCarregadoTeste = null;
        tableViewDados.getItems().clear();
        verificarSePodeIniciar();
    }

    @FXML
    protected void onCarregarPrincipalClick() {
        File arquivo = selecionarArquivoCsv();
        if (arquivo != null) {
            try {
                dadosCarregadoPrincipal = LeitorCSV.carregarDados(arquivo);
                lblArquivoPrincipal.setText(arquivo.getName());

                // Popula campos e tabela
                txtEntrada.setText(String.valueOf(dadosCarregadoPrincipal.getNumeroDeAtributos()));
                txtSaida.setText(String.valueOf(dadosCarregadoPrincipal.getNumeroDeClasses()));
                tableViewDados.getItems().setAll(dadosCarregadoPrincipal.getInstancias());
                System.out.println("Arquivo Principal carregado.");

            } catch (Exception e) {
                exibirAlertaErro("Erro ao carregar arquivo", "Não foi possível ler o arquivo: " + e.getMessage());
            }
        }
        verificarSePodeIniciar();
    }

    @FXML
    protected void onCarregarTesteClick() {
        File arquivo = selecionarArquivoCsv();
        if (arquivo != null) {
            try {
                dadosCarregadoTeste = LeitorCSV.carregarDados(arquivo);
                lblArquivoTeste.setText(arquivo.getName());
                System.out.println("Arquivo de Teste carregado.");
            } catch (Exception e) {
                exibirAlertaErro("Erro ao carregar arquivo", "Não foi possível ler o arquivo de teste: " + e.getMessage());
            }
        }
        verificarSePodeIniciar();
    }

    /**
     * MÉTODO PRINCIPAL - Chamado ao clicar em "Iniciar Treinamento"
     */
    @FXML
    protected void onIniciarTreinamentoClick() {
        // --- 1. Validação dos Parâmetros da UI ---
        int epocas, numOculta;
        double erroDesejado, taxaAprendizado;

        try {
            // (Validação dos campos de texto, igual a antes)
            epocas = Integer.parseInt(txtEpocas.getText());
            erroDesejado = Double.parseDouble(txtErro.getText());
            taxaAprendizado = Double.parseDouble(txtTaxaAprendizado.getText());
            numOculta = Integer.parseInt(txtOculta.getText());

            if (taxaAprendizado <= 0 || taxaAprendizado > 1) {
                exibirAlertaErro("Parâmetro Inválido", "A Taxa de Aprendizado (N) deve ser > 0 e <= 1.");
                return;
            }
            if (numOculta <= 0 || epocas <= 0) {
                exibirAlertaErro("Parâmetro Inválido", "Épocas e Camada Oculta devem ser maiores que 0.");
                return;
            }
        } catch (NumberFormatException e) {
            exibirAlertaErro("Parâmetro Inválido", "Verifique os valores de Épocas, Erro, Taxa e Camada Oculta. Devem ser números.");
            return;
        }

        // Pega a função de ativação selecionada
        FuncaoAtivacao funcao = FuncaoAtivacao.getFuncaoSelecionada(radioLinear, radioLogistica, radioHiperbolica);

        // --- 2. PREPARAR OS CONJUNTOS DE DADOS (NOVO) ---
        final ConjuntoDados dadosTreinoFinal;
        final ConjuntoDados dadosTesteFinal;

        if (checkArquivoUnico.isSelected()) {
            // Modo Arquivo Único: Dividir o 'dadosCarregadoPrincipal'
            // Usamos 70% para treino
            Pair<ConjuntoDados, ConjuntoDados> datasets = splitDataSet(dadosCarregadoPrincipal, 0.7);
            dadosTreinoFinal = datasets.getKey();
            dadosTesteFinal = datasets.getValue();

            System.out.println("Dividindo arquivo único:");
            System.out.println("Instâncias de Treino: " + dadosTreinoFinal.getInstancias().size());
            System.out.println("Instâncias de Teste: " + dadosTesteFinal.getInstancias().size());
        } else {
            // Modo Dois Arquivos: Apenas usa os dados carregados
            dadosTreinoFinal = dadosCarregadoPrincipal;
            dadosTesteFinal = dadosCarregadoTeste;
        }

        // --- 3. Criação da TAREFA (Task) de Treinamento ---
        Task<ResultadoTreinamento> tarefaTreinamento = new Task<>() {
            @Override
            protected ResultadoTreinamento call() throws Exception {
                // --- INÍCIO DA EXECUÇÃO NA THREAD DE BACKGROUND ---

                double taxaAprendizadoAtual = taxaAprendizado;

                // 1. Instanciar a Rede Neural
                RedeNeural rede = new RedeNeural(
                        dadosTreinoFinal.getNumeroDeAtributos(), // Usa o 'Final'
                        numOculta,
                        dadosTreinoFinal.getNumeroDeClasses(), // Usa o 'Final'
                        funcao,
                        taxaAprendizadoAtual,
                        dadosTreinoFinal // Passa o 'Final' (ele contém o Min/Max correto)
                );

                // 2. Loop de Treinamento (Épocas)
                int epocaAtual = 0;
                double erroEpoca = 1.0;
                List<Instancia> instanciasTreino = new ArrayList<>(dadosTreinoFinal.getInstancias()); // Usa o 'Final'
                List<Double> historicoErros = new ArrayList<>();

                LinkedList<Double> ultimosErros = new LinkedList<>();
                final int TAMANHO_JANELA_PLATO = 10;
                final double LIMIAR_PLATO = 0.00001;

                while (epocaAtual < epocas && erroEpoca > erroDesejado) {

                    Collections.shuffle(instanciasTreino);
                    double somaErrosQuadradicos = 0.0;
                    for (Instancia inst : instanciasTreino) {
                        List<Double> entradasNormalizadas = rede.normalizar(inst.getEntradas());
                        List<Double> vetorAlvo = rede.getVetorAlvo(inst.getClasse());
                        List<Double> saidasRede = rede.feedforward(entradasNormalizadas);
                        rede.backpropagation(entradasNormalizadas, vetorAlvo);
                        for (int i = 0; i < vetorAlvo.size(); i++) {
                            double erro = vetorAlvo.get(i) - saidasRede.get(i);
                            somaErrosQuadradicos += 0.5 * (erro * erro);
                        }
                    }

                    epocaAtual++;
                    erroEpoca = somaErrosQuadradicos / instanciasTreino.size();
                    historicoErros.add(erroEpoca);

                    // 3. ATUALIZAR A UI DE TREINAMENTO
                    treinamentoController.atualizarStatus(epocaAtual, erroEpoca);

                    // 4. LÓGICA DE DETECÇÃO DE PLATÔ
                    ultimosErros.add(erroEpoca);
                    if (ultimosErros.size() > TAMANHO_JANELA_PLATO) ultimosErros.removeFirst();

                    if (ultimosErros.size() == TAMANHO_JANELA_PLATO) {
                        double desvioPadrao = calcularDesvioPadrao(ultimosErros);

                        if (desvioPadrao >= 0 && desvioPadrao <= LIMIAR_PLATO) {
                            System.out.println(">>> PLATÔ DETECTADO! Desvio Padrão: " + desvioPadrao);

                            // **** A LINHA CORRIGIDA ESTÁ AQUI ****
                            TreinamentoController.AcaoPlato acao = treinamentoController.aguardarAcaoPlato().get();

                            if (acao == TreinamentoController.AcaoPlato.PARAR) break;
                            else if (acao == TreinamentoController.AcaoPlato.REDUZIR_TAXA) {
                                taxaAprendizadoAtual *= 0.90;
                                rede.setTaxaAprendizado(taxaAprendizadoAtual);
                            }
                            ultimosErros.clear();
                        }
                    }
                }

                // 5. Retornar os resultados
                return new ResultadoTreinamento(rede, erroEpoca, epocaAtual, historicoErros);
            }
        };

        // --- 4. Configurar o que fazer quando a TAREFA terminar ---
        tarefaTreinamento.setOnSucceeded(event -> {
            fecharJanelaTreinamento();
            ResultadoTreinamento resultado = tarefaTreinamento.getValue();
            System.out.println("Treinamento Concluído!");
            // Passa o 'dadosTesteFinal' para a janela de resultados
            abrirJanelaResultados(resultado, dadosTesteFinal);
        });

        tarefaTreinamento.setOnFailed(event -> {
            fecharJanelaTreinamento();
            Throwable e = tarefaTreinamento.getException();
            exibirAlertaErro("Erro no Treinamento", "Ocorreu uma falha: " + e.getMessage());
            e.printStackTrace();
        });

        // --- 5. Iniciar a Tarefa ---
        abrirJanelaTreinamento();
        new Thread(tarefaTreinamento).start();
    }

    // --- Métodos Auxiliares ---

    /**
     * NOVO MÉTODO: Divide um Conjunto de Dados em Treino e Teste.
     */
    private Pair<ConjuntoDados, ConjuntoDados> splitDataSet(ConjuntoDados dadosCompletos, double porcentagemTreino) {
        // Pega os valores globais Mín, Máx e Classes
        List<Double> minimos = dadosCompletos.getMinimos();
        List<Double> maximos = dadosCompletos.getMaximos();
        Set<String> classes = dadosCompletos.getClassesUnicas();

        // Embaralha todas as instâncias
        List<Instancia> todasInstancias = new ArrayList<>(dadosCompletos.getInstancias());
        Collections.shuffle(todasInstancias);

        // Calcula o ponto de divisão
        int pontoDivisao = (int) (todasInstancias.size() * porcentagemTreino);

        // Cria as sub-listas
        List<Instancia> listaTreino = new ArrayList<>(todasInstancias.subList(0, pontoDivisao));
        List<Instancia> listaTeste = new ArrayList<>(todasInstancias.subList(pontoDivisao, todasInstancias.size()));

        // Cria os novos ConjuntoDados usando o construtor que NÃO recalcula min/max
        ConjuntoDados dadosTreino = new ConjuntoDados(listaTreino, minimos, maximos, classes);
        ConjuntoDados dadosTeste = new ConjuntoDados(listaTeste, minimos, maximos, classes);

        // Retorna o "Par" de datasets
        return new Pair<>(dadosTreino, dadosTeste);
    }


    private void abrirJanelaTreinamento() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("treinamento-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            this.treinamentoController = fxmlLoader.getController();
            janelaTreinamento = new Stage();
            janelaTreinamento.setTitle("Treinando...");
            janelaTreinamento.setScene(scene);
            janelaTreinamento.initModality(Modality.APPLICATION_MODAL);
            janelaTreinamento.initOwner(getJanelaPrincipal());
            janelaTreinamento.setResizable(false);
            janelaTreinamento.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fecharJanelaTreinamento() {
        if (janelaTreinamento != null) {
            janelaTreinamento.close();
        }
    }

    private void abrirJanelaResultados(ResultadoTreinamento resultado, ConjuntoDados dadosTeste) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("resultados-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            ResultadosController controller = fxmlLoader.getController();
            controller.iniciar(
                    resultado.getRedeTreinada(),
                    dadosTeste,
                    resultado.getErroFinal(),
                    resultado.getEpocasTotal(),
                    resultado.getHistoricoErros()
            );

            Stage stage = new Stage();
            stage.setTitle("Resultados do Treinamento");
            stage.setScene(scene);
            stage.initOwner(getJanelaPrincipal());
            stage.show();

        } catch (IOException e) {
            exibirAlertaErro("Erro", "Não foi possível abrir a tela de resultados.");
            e.printStackTrace();
        }
    }

    private File selecionarArquivoCsv() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Arquivo CSV");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Arquivos CSV (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        return fileChooser.showOpenDialog(getJanelaPrincipal());
    }

    private void verificarSePodeIniciar() {
        // Lógica de verificação atualizada
        boolean pronto = false;
        if (checkArquivoUnico.isSelected()) {
            // Modo arquivo único: só precisa do principal
            pronto = (dadosCarregadoPrincipal != null);
        } else {
            // Modo dois arquivos: precisa dos dois
            pronto = (dadosCarregadoPrincipal != null && dadosCarregadoTeste != null);
        }
        btnIniciar.setDisable(!pronto);
    }

    private Window getJanelaPrincipal() {
        return btnIniciar.getScene().getWindow();
    }

    private void exibirAlertaErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private double calcularDesvioPadrao(List<Double> lista) {
        if (lista == null || lista.size() < 2) return 0.0;
        double soma = 0.0;
        for (double val : lista) soma += val;
        double media = soma / lista.size();
        double somaVariancia = 0.0;
        for (double val : lista) somaVariancia += Math.pow(val - media, 2);
        double variancia = somaVariancia / (lista.size() - 1);
        return Math.sqrt(variancia);
    }
}