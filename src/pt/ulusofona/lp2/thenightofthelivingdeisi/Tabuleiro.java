package pt.ulusofona.lp2.thenightofthelivingdeisi;

public class Tabuleiro {
     int width;
    int height;
     Object[][] grid; // Array bidimensional para armazenar criaturas e equipamentos

    public Tabuleiro(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new Object[width][height]; // Inicializa o array bidimensional
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

    // Adiciona uma criatura ou equipamento ao tabuleiro
    public boolean adicionar(Object objeto, int x, int y) {
        if (!dentroDosLimites(x, y)) {
            return false; // Fora dos limites
        }

        if (grid[x][y] == null) {
            grid[x][y] = objeto; // Adiciona o objeto
            return true;
        }
        return false; // Posição já ocupada
    }

    // Remove uma criatura ou equipamento de uma posição
    public boolean remover(int x, int y) {
        if (!dentroDosLimites(x, y)) {
            return false; // Fora dos limites
        }

        if (grid[x][y] != null) {
            grid[x][y] = null; // Remove o objeto
            return true;
        }
        return false; // Posição já está vazia
    }

    // Obtém o conteúdo de uma posição
    public Object obter(int x, int y) {
        if (!dentroDosLimites(x, y)) {
            return null; // Fora dos limites
        }
        return grid[x][y];
    }

    // Imprime o tabuleiro (apenas para debugging)
    public void imprimirTabuleiro() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (grid[x][y] == null) {
                    System.out.print(". "); // Célula vazia
                } else if (grid[x][y] instanceof Creature) {
                    System.out.print("C "); // Representa criatura
                } else if (grid[x][y] instanceof Equipamento) {
                    System.out.print("E "); // Representa equipamento
                }
            }
            System.out.println();
        }
    }


}
