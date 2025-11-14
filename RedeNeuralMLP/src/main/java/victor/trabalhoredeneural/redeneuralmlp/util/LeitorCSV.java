package victor.trabalhoredeneural.redeneuralmlp.util;

import victor.trabalhoredeneural.redeneuralmlp.model.ConjuntoDados;
import victor.trabalhoredeneural.redeneuralmlp.model.Instancia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//Classe utilitaria responsavel por ler os arquivos CSV e transforma em um objeto ConjuntoDados
public class LeitorCSV {

    //Lê um arquivo CSV e o converte em um ConjuntoDados
    public static ConjuntoDados carregarDados(File arquivo) throws IOException, NumberFormatException {

        List<Instancia> instancias = new ArrayList<>();

        // Usamos try-with-resources para garantir que o BufferedReader seja fechado
        try (BufferedReader leitor = new BufferedReader(new FileReader(arquivo))) {

            // Le e descarta a linha do cabeçalho (X1, X2, ..., classe)
            leitor.readLine();

            String linha;
            // Le o restante das linhas de dados
            while ((linha = leitor.readLine()) != null) {

                // Se a linha não estiver em branco, processa
                if (!linha.trim().isEmpty()) {

                    String[] valores = linha.split(",");

                    // O documento de trabalho especifica 6 entradas e 1 classe
                    // Se o número de colunas for o correto, processa
                    if (valores.length == 7) {

                        try {
                            List<Double> entradas = new ArrayList<>();
                            // Lê as 6 colunas de entrada
                            for (int i = 0; i < 6; i++) {
                                entradas.add(Double.parseDouble(valores[i].trim()));
                            }

                            // Lê a última coluna (classe)
                            String classe = valores[6].trim();

                            // Cria a Instancia e adiciona à lista
                            instancias.add(new Instancia(entradas, classe));

                        } catch (NumberFormatException e) {
                            System.err.println("Aviso: Pulando linha com valor numérico inválido: " + linha);
                        }

                    }
                    else // Se o número de colunas for incorreto
                        System.err.println("Aviso: Pulando linha mal formada: " + linha);

                }
            }
        }

        // Cria e retorna o ConjuntoDados, que calculará min/max automaticamente
        return new ConjuntoDados(instancias);
    }
}