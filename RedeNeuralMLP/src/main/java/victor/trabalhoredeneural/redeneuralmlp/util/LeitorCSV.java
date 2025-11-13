package victor.trabalhoredeneural.redeneuralmlp.util;

import victor.trabalhoredeneural.redeneuralmlp.model.ConjuntoDados;
import victor.trabalhoredeneural.redeneuralmlp.model.Instancia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe utilitária responsável por ler os arquivos CSV e
 * transformá-los em um objeto ConjuntoDados.
 */
public class LeitorCSV {

    /**
     * Lê um arquivo CSV e o converte em um ConjuntoDados.
     *
     * @param arquivo O arquivo (File) a ser lido.
     * @return Um objeto ConjuntoDados contendo todas as instâncias e estatísticas.
     * @throws IOException Se houver um erro de leitura do arquivo.
     * @throws NumberFormatException Se um valor numérico no CSV for inválido.
     */
    public static ConjuntoDados carregarDados(File arquivo) throws IOException, NumberFormatException {

        List<Instancia> instancias = new ArrayList<>();

        // Usamos try-with-resources para garantir que o BufferedReader seja fechado
        try (BufferedReader leitor = new BufferedReader(new FileReader(arquivo))) {

            // 1. Ler e descartar a linha do cabeçalho (X1, X2, ..., classe)
            leitor.readLine();

            String linha;
            // 2. Ler o restante das linhas de dados
            while ((linha = leitor.readLine()) != null) {
                if (linha.trim().isEmpty()) {
                    continue; // Pula linhas em branco
                }

                String[] valores = linha.split(",");

                // O documento de trabalho especifica 6 entradas e 1 classe
                if (valores.length != 7) {
                    System.err.println("Aviso: Pulando linha mal formada: " + linha);
                    continue;
                }

                try {
                    List<Double> entradas = new ArrayList<>();
                    // 3. Lê as 6 colunas de entrada
                    for (int i = 0; i < 6; i++) {
                        entradas.add(Double.parseDouble(valores[i].trim()));
                    }

                    // 4. Lê a última coluna (classe)
                    String classe = valores[6].trim();

                    // 5. Cria a Instancia e adiciona à lista
                    instancias.add(new Instancia(entradas, classe));

                } catch (NumberFormatException e) {
                    System.err.println("Aviso: Pulando linha com valor numérico inválido: " + linha);
                }
            }
        }

        // 6. Cria e retorna o ConjuntoDados, que calculará min/max automaticamente
        return new ConjuntoDados(instancias);
    }
}