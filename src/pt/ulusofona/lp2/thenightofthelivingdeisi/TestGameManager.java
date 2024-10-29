package pt.ulusofona.lp2.thenightofthelivingdeisi;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class TestGameManager {
    @Test
    public void testGetInitialTeamIdVivosPrimeiro() {
        // Cria uma instância do GameManager
        GameManager gameManager = new GameManager();

        // Caminho do arquivo de teste para o caso dos vivos jogarem primeiro (teamId = 1)
        File file = new File("test-files/6x6.txt");

        // Carrega o jogo e verifica se foi bem-sucedido
        boolean loaded = gameManager.loadGame(file);
        assertEquals(true, loaded, "O jogo deve ser carregado com sucesso.");

        // Verifica se o ID da equipe inicial é 1 (vivos)
        assertEquals(0, gameManager.getInitialTeamId(), "O ID inicial deve ser 1 para os vivos.");
    }

    @Test
    public void testGetInitialTeamIdZombiesPrimeiro() {
        // Cria uma instância do GameManager
        GameManager gameManager = new GameManager();

        // Caminho do arquivo de teste para o caso dos zumbis jogarem primeiro (teamId = 0)
        File file = new File("test-files/testeZombiesPrimeiro.txt");

        // Carrega o jogo e verifica se foi bem-sucedido
        boolean loaded = gameManager.loadGame(file);
        assertEquals(true, loaded, "O jogo deve ser carregado com sucesso.");

        // Verifica se o ID da equipe inicial é 0 (zumbis)
        assertEquals(0, gameManager.getInitialTeamId(), "O ID inicial deve ser 0 para os zumbis.");
    }

}


