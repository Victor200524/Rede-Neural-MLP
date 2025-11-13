package victor.trabalhoredeneural.redeneuralmlp.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ConjuntoDados {

    private final List<Instancia> instancias;
    private final List<Double> minimos;
    private final List<Double> maximos;
    private final Set<String> classesUnicas;

    /**
     * Construtor principal: Calcula min/max e classes a partir da lista.
     */
    public ConjuntoDados(List<Instancia> instancias) {
        this.instancias = new ArrayList<>(instancias);
        this.minimos = new ArrayList<>();
        this.maximos = new ArrayList<>();
        this.classesUnicas = new TreeSet<>(); // TreeSet para manter em ordem alfabética

        calcularMinMaxEClasses();
    }

    /**
     * NOVO CONSTRUTOR: Apenas recebe os valores, não calcula.
     * Usado para criar os conjuntos de treino/teste divididos.
     */
    public ConjuntoDados(List<Instancia> instancias, List<Double> minimos, List<Double> maximos, Set<String> classesUnicas) {
        this.instancias = instancias;
        this.minimos = minimos;
        this.maximos = maximos;
        this.classesUnicas = classesUnicas;
    }

    /**
     * Método privado para inicializar os valores min/max e as classes.
     */
    private void calcularMinMaxEClasses() {
        if (instancias.isEmpty()) {
            return;
        }

        int numAtributos = instancias.get(0).getNumeroDeEntradas();
        for (int i = 0; i < numAtributos; i++) {
            minimos.add(Double.MAX_VALUE);
            maximos.add(Double.MIN_VALUE);
        }

        for (Instancia inst : instancias) {
            classesUnicas.add(inst.getClasse());
            for (int i = 0; i < numAtributos; i++) {
                double valor = inst.getEntradas().get(i);
                if (valor < minimos.get(i)) minimos.set(i, valor);
                if (valor > maximos.get(i)) maximos.set(i, valor);
            }
        }
    }

    // --- Getters ---

    public List<Instancia> getInstancias() {
        return instancias;
    }

    public List<Double> getMinimos() {
        return minimos;
    }

    public List<Double> getMaximos() {
        return maximos;
    }

    // NOVO GETTER (necessário para o split)
    public Set<String> getClassesUnicas() {
        return classesUnicas;
    }

    public List<String> getListaClassesUnicas() {
        return new ArrayList<>(classesUnicas);
    }

    public int getNumeroDeClasses() {
        return classesUnicas.size();
    }

    public int getNumeroDeAtributos() {
        if (instancias.isEmpty()) return 0;
        return instancias.get(0).getNumeroDeEntradas();
    }
}