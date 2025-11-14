package victor.trabalhoredeneural.redeneuralmlp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa uma única instância (ou linha) dos dados.
 * Armazena os 6 valores de entrada (X1 a X6) e o nome da classe (ex: "CA", "CB")
 */
public class Instancia {

    private final List<Double> entradas;
    private final String classe;

    public Instancia(List<Double> entradas, String classe) {
        // Cria uma nova lista para garantir imutabilidade da lista original
        this.entradas = new ArrayList<>(entradas);
        this.classe = classe;
    }

    public List<Double> getEntradas() {
        return entradas;
    }

    public String getClasse() {
        return classe;
    }

    public int getNumeroDeEntradas() {
        return this.entradas.size();
    }

    @Override
    public String toString() {
        return "Instancia{" +
                "entradas=" + entradas +
                ", classe='" + classe + '\'' +
                '}';
    }
}