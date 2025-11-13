package victor.trabalhoredeneural.redeneuralmlp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa uma única instância (ou linha) dos dados.
 * Armazena os 6 valores de entrada (X1 a X6) e o nome da classe (ex: "CA", "CB").
 */
public class Instancia {

    private final List<Double> entradas;
    private final String classe;

    /**
     * Construtor da Instância.
     * @param entradas A lista de valores de entrada (já como Double).
     * @param classe O nome da classe (String).
     */
    public Instancia(List<Double> entradas, String classe) {
        // Cria uma nova lista para garantir imutabilidade da lista original
        this.entradas = new ArrayList<>(entradas);
        this.classe = classe;
    }

    // --- Getters ---

    public List<Double> getEntradas() {
        return entradas;
    }

    public String getClasse() {
        return classe;
    }

    /**
     * Retorna o número de atributos de entrada.
     * @return O tamanho da lista de entradas (deve ser 6).
     */
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