package pt.ulusofona.lp2.thenightofthelivingdeisi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SafeHaven {
    private int x;
    private int y;
    private List<Creature> criaturasDentro;// Lista de criaturas que entraram no Safe Haven
    private static Set<String> SafeHaven = new HashSet<>();

    // Construtor
    public SafeHaven(int x, int y) {
        this.x = x;
        this.y = y;
        this.criaturasDentro = new ArrayList<>();
    }



    // Permite que uma criatura entre no Safe Haven
    public boolean entrar(Creature criatura) {
        if (!criatura.isHuman()) {
            return false; // Apenas humanos podem entrar
        }

        // Adiciona a criatura ao Safe Haven
        criaturasDentro.add(criatura);
        return true;
    }

    // Retorna as criaturas dentro do Safe Haven
    public List<Creature> getCriaturasDentro() {
        return criaturasDentro;
    }

    // Retorna as coordenadas do Safe Haven
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public static void add(int x, int y) {
        SafeHaven.add(x + "," + y); // Adiciona o Safe Haven como uma string "x,y"
    }

    // Remove um Safe Haven
    public static void remove(int x, int y) {
        SafeHaven.remove(x + "," + y); // Remove a string "x,y" do conjunto
    }

    // Verifica se um Safe Haven existe na posição
    public static boolean contains(int x, int y) {
        return SafeHaven.contains(x + "," + y); // Verifica se a string "x,y" está no conjunto
    }

    // Retorna todos os Safe Havens registrados (opcional)
    public static Set<String> getSafeHavens() {
        return SafeHaven; // Retorna o conjunto dos Safe Havens
    }

    @Override
    public String toString() {
        return "Safe Haven @ (" + x + ", " + y + "), Criaturas: " + criaturasDentro.size();
    }


}