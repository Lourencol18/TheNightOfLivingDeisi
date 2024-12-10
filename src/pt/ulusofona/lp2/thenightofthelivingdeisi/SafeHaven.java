package pt.ulusofona.lp2.thenightofthelivingdeisi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SafeHaven {
    private int x;
    private int y;
    private List<Creature> criaturasDentro;  // Lista de criaturas que entraram no Safe Haven
    private static Set<SafeHaven> safeHavens = new HashSet<>();  // Conjunto de Safe Havens

    // Construtor
    public SafeHaven(int x, int y) {
        this.x = x;
        this.y = y;
        this.criaturasDentro = new ArrayList<>();
    }

    // Método para adicionar uma criatura ao Safe Haven
    public void entrar(Creature creature) {
        if (!criaturasDentro.contains(creature)) {
            criaturasDentro.add(creature); // Adiciona a criatura ao Safe Haven
            System.out.println("Criatura adicionada ao Safe Haven: " + creature.getNome());
        }
    }

    // Método para retornar as criaturas dentro do Safe Haven
    public List<Creature> getCriaturasDentro() {
        return new ArrayList<>(criaturasDentro); // Retorna uma cópia da lista
    }

    // Retorna as coordenadas do Safe Haven
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // Adiciona um novo Safe Haven ao conjunto
    public static void add(SafeHaven safeHaven) {
        safeHavens.add(safeHaven);  // Adiciona o Safe Haven ao conjunto
    }

    // Remove um Safe Haven do conjunto
    public static void remove(SafeHaven safeHaven) {
        safeHavens.remove(safeHaven);  // Remove o Safe Haven do conjunto
    }

    // Verifica se um Safe Haven existe na posição
    public static boolean contains(int x, int y) {
        for (SafeHaven safeHaven : safeHavens) {
            if (safeHaven.getX() == x && safeHaven.getY() == y) {
                return true;  // Verifica se existe um Safe Haven na posição
            }
        }
        return false;
    }

    // Método para exibir as criaturas dentro do Safe Haven
    public String getCriaturasNoSafeHaven() {
        if (criaturasDentro.isEmpty()) {
            return "Nenhuma criatura no Safe Haven.";
        }

        StringBuilder criaturas = new StringBuilder("Criaturas no Safe Haven:\n");
        for (Creature creature : criaturasDentro) {
            criaturas.append(creature.getNome())
                    .append(" (")
                    .append(creature.getTipoCriatura())
                    .append(")\n");
        }
        return criaturas.toString();
    }

    // Retorna todos os Safe Havens registrados
    public static Set<SafeHaven> getSafeHavens() {
        return safeHavens;  // Retorna o conjunto de Safe Havens
    }

    @Override
    public String toString() {
        return "Safe Haven @ (" + x + ", " + y + "), Criaturas: " + criaturasDentro.size();
    }
}
