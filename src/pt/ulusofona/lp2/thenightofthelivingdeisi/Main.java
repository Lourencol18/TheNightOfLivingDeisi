package pt.ulusofona.lp2.thenightofthelivingdeisi;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        GameManager gameManager = new GameManager();

        try {
            File file = new File("6x6.txt");
            gameManager.loadGame(file);

            // Teste de uma posição específica
            System.out.println("Info para posição (3, 3): " + gameManager.getSquareInfo(3, 3)); // Ajuste as coordenadas conforme necessário
            System.out.println("Info para posição (0, 0): " + gameManager.getSquareInfo(0, 0));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

