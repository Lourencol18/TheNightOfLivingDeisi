package pt.ulusofona.lp2.thenightofthelivingdeisi;

import java.util.ArrayList;
import java.util.List;

public class Tabuleiro {
    private int width;
    private int height;
    private Object[][] grid; // Array bidimensional para armazenar criaturas e equipamentos
    private List<SafeHaven> safeHavens = new ArrayList<>(); // Lista de Safe Havens

    public Tabuleiro(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new Object[width][height]; // Inicializa o array bidimensional
        this.safeHavens = new ArrayList<>();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    // Verifica se uma posição está dentro dos limites do tabuleiro
    public boolean dentroDosLimites(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    // Adiciona uma criatura na posição do tabuleiro
    public void adicionarCriatura(Creature criatura) {
        if (!dentroDosLimites(criatura.getX(), criatura.getY())) {
            throw new IllegalArgumentException("Coordenadas fora dos limites do tabuleiro.");
        }
        grid[criatura.getX()][criatura.getY()] = criatura; // Adiciona a criatura no grid
    }

    // Remove uma criatura da posição do tabuleiro
    public void removerCriatura(Creature criatura) {
        if (dentroDosLimites(criatura.getX(), criatura.getY())) {
            grid[criatura.getX()][criatura.getY()] = null; // Remove a criatura do grid
        }
    }


    // Adiciona um Safe Haven ao tabuleiro
    public boolean adicionarSafeHaven(int x, int y) {
        if (!dentroDosLimites(x, y)) {
            throw new IllegalArgumentException("Coordenadas fora dos limites do tabuleiro.");
        }

        // Verifica se já existe um Safe Haven nessa posição
        for (SafeHaven safeHaven : safeHavens) {
            if (safeHaven.getX() == x && safeHaven.getY() == y) {
                return false; // Já existe um Safe Haven nessa posição
            }
        }

        // Adiciona um novo Safe Haven
        safeHavens.add(new SafeHaven(x, y));
        return true;
    }

    // Remove um Safe Haven do tabuleiro
    public boolean removerSafeHaven(int x, int y) {
        return safeHavens.removeIf(safeHaven -> safeHaven.getX() == x && safeHaven.getY() == y);
    }

    // Verifica se uma posição é um Safe Haven
    public boolean isSafeHaven(int x, int y) {
        for (SafeHaven safeHaven : safeHavens) {

            if (safeHaven.getX() == x && safeHaven.getY() == y) {

                return true;
            }
        }

        return false;
    }

    // Retorna a lista de Safe Havens
    public List<SafeHaven> getSafeHavens() {
        return safeHavens;
    }

    // Verifica se existe uma criatura na posição específica
    public Creature getCriaturaAtPosition(int x, int y) {
        if (dentroDosLimites(x, y)) {
            return (Creature) grid[x][y]; // Retorna a criatura na posição, se houver
        }
        return null; // Caso não haja criatura nessa posição
    }
}
