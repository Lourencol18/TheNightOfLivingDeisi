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
        File file = new File("test-files/testeVivosPrimeiro.txt");

        // Carrega o jogo e verifica se foi bem-sucedido
        boolean loaded = gameManager.loadGame(file);
        assertEquals(true, loaded, "O jogo deve ser carregado com sucesso.");

        // Verifica se o ID da equipe inicial é 1 (vivos)
        assertEquals(1, gameManager.getInitialTeamId(), "O ID inicial deve ser 1 para os vivos.");
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
    @Test
    public void testHasEquipment() {
        GameManager gameManager = new GameManager();

        // Criar um humano com um equipamento específico
        Creature humanWithEquipment = new Creature(1, 0, "Humano A", 0, 0);
        Equipamento sword = new Equipamento(10, 1, 0, 0); // Tipo 1 representa uma espada
        humanWithEquipment.setEquipamento(sword);

        // Criar um humano sem equipamento
        Creature humanWithoutEquipment = new Creature(2, 0, "Humano B", 1, 1);

        // Criar um zumbi (tipo 1)
        Creature zombie = new Creature(3, 1, "Zombie C", 2, 2);

        // Adiciona as criaturas à lista de personagens do GameManager
        gameManager.personagens.add(humanWithEquipment);
        gameManager.personagens.add(humanWithoutEquipment);
        gameManager.personagens.add(zombie);

        // Teste: humano com equipamento de tipo 1 deve retornar true
        assertTrue(gameManager.hasEquipment(1, 1), "Humano com espada deve retornar true");

        // Teste: humano sem equipamento deve retornar false
        assertFalse(gameManager.hasEquipment(2, 1), "Humano sem equipamento deve retornar false");

        // Teste: zumbi não pode ter equipamento, deve retornar false
        assertFalse(gameManager.hasEquipment(3, 1), "Zombie não pode ter equipamento, deve retornar false");
    }
}


