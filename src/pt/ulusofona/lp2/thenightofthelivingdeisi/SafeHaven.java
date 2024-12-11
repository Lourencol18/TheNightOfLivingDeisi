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

    // Retorna todos os Safe Havens registrados
    public static Set<SafeHaven> getSafeHavens() {
        return safeHavens;  // Retorna o conjunto de Safe Havens
    }


}
