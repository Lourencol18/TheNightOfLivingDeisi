package pt.ulusofona.lp2.thenightofthelivingdeisi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SafeHaven {
    private int x;
    private int y;
    private List<Creature> criaturasDentro;  // Lista de criaturas que entraram no Safe Haven
    private static Set<SafeHaven> safeHavens = new HashSet<>();  // Conjunto de SafeHavens

    // Construtor
    public SafeHaven(int x, int y) {
        this.x = x;
        this.y = y;
        this.criaturasDentro = new ArrayList<>();
    }

    public boolean entrar(Creature criatura) {
        if (!criatura.isHuman()) {
            return false; // Apenas humanos podem entrar
        }

        if (!criaturasDentro.contains(criatura)) {
            criaturasDentro.add(criatura);
            System.out.println("Criatura adicionada ao Safe Haven: " + criatura.getNome() + " (ID: " + criatura.getId() + ")");
        }
        return true;
    }



    public List<Creature> getCriaturasDentro() {
        return new ArrayList<>(criaturasDentro); // Retorna uma cópia da lista para evitar modificações externas
    }


    // Retorna as coordenadas do Safe Haven
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // Adiciona um novo Safe Haven
    public static void add(SafeHaven safeHaven) {
        safeHavens.add(safeHaven);  // Adiciona o Safe Haven ao conjunto
    }

    // Remove um Safe Haven
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
    public void mostrarCriaturasNoSafeHaven(SafeHaven safeHaven) {
        List<Creature> criaturasDentro = safeHaven.getCriaturasDentro();
        if (criaturasDentro.isEmpty()) {
            System.out.println("Nenhuma criatura encontrada no Safe Haven.");
        } else {
            for (Creature criatura : criaturasDentro) {
                System.out.println("ID: " + criatura.getId() + ", Nome: " + criatura.getNome());
            }
        }
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
