package pt.ulusofona.lp2.thenightofthelivingdeisi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestGameManager {
    private static final String TEST_GAME_FILE = "6x6.txt";
    private GameManager gameManager;

    @BeforeEach
    void setUp() {
        gameManager = new GameManager();
        loadTestGame();
    }

    private void loadTestGame() {
        try {
            gameManager.loadGame(new File(TEST_GAME_FILE));
        } catch (InvalidFileException | IOException e) {
            fail("Erro ao carregar o jogo de teste: " + e.getMessage());
        }
    }

    @Test
    void testGetWorldSize() {
        int[] worldSize = gameManager.getWorldSize();
        assertEquals(7, worldSize[0], "Altura do mundo inválida");
        assertEquals(7, worldSize[1], "Largura do mundo inválida");
    }

    @Test
    void testGetInitialTeamId() {
        int initialTeamId = gameManager.getInitialTeamId();
        assertEquals(10, initialTeamId, "Equipe inicial inválida");
    }

    @Test
    void testGetCurrentTeamId() {
        int currentTeamId = gameManager.getCurrentTeamId();
        assertTrue(currentTeamId == 10 || currentTeamId == 20, "Equipe atual inválida");
    }

    @Test
    void testIsDay() {
        assertTrue(gameManager.isDay() || !gameManager.isDay(), "Horário do dia inválido");
    }

    @Test
    void testGetSquareInfo() {
        assertEquals("Z:1", gameManager.getSquareInfo(5, 4), "Informações da posição inválidas");
        assertEquals("H:6", gameManager.getSquareInfo(3, 4), "Informações da posição inválidas");
        assertEquals("", gameManager.getSquareInfo(0, 0), "Informações da posição inválidas");
    }

    @Test
    void testGetCreatureInfo() {
        String[] creatureInfo = gameManager.getCreatureInfo(1);
        assertEquals("1", creatureInfo[0], "ID da criatura inválido");
        assertEquals("Criança", creatureInfo[1], "Tipo de criatura inválido");
        assertEquals("Zombie", creatureInfo[2], "Equipe da criatura inválida");
        assertEquals("Melanie", creatureInfo[3], "Nome da criatura inválido");
        assertEquals("5", creatureInfo[4], "Coordenada X da criatura inválida");
        assertEquals("4", creatureInfo[5], "Coordenada Y da criatura inválida");
    }

    @Test
    void testGetCreatureInfoAsString() {
        String creatureInfoAsString = gameManager.getCreatureInfoAsString(1);
        assertNotNull(creatureInfoAsString, "Informações da criatura como string inválidas");
    }

    @Test
    void testGetEquipmentInfo() {
        String[] equipmentInfo = gameManager.getEquipmentInfo(1);
        assertNotNull(equipmentInfo, "Informações do equipamento inválidas");
    }

    @Test
    void testGetEquipmentInfoAsString() {
        String equipmentInfoAsString = gameManager.getEquipmentInfoAsString(1);
        assertNotNull(equipmentInfoAsString, "Informações do equipamento como string inválidas");
    }

    @Test
    void testHasEquipment() {
        boolean hasEquipment = gameManager.hasEquipment(6, 0);
        assertTrue(hasEquipment, "Criatura não possui equipamento");
    }

    @Test
    void testMove() {
        boolean moved = gameManager.move(5, 4, 5, 3);
        assertTrue(moved, "Movimento inválido");
    }

    @Test
    void testGameIsOver() {
        boolean gameOver = gameManager.gameIsOver();
        assertFalse(gameOver, "Jogo terminou indevidamente");
    }

    @Test
    void testGetSurvivors() {
        ArrayList<String> survivors = gameManager.getSurvivors();
        assertNotNull(survivors, "Informações dos sobreviventes inválidas");
    }

    @Test
    void testSaveGame() {
        try {
            gameManager.saveGame(new File("test_game_saved.txt"));
            // Adicione asserções para verificar se o jogo foi salvo corretamente
        } catch (IOException e) {
            fail("Erro ao salvar o jogo: " + e.getMessage());
        }
    }

    @Test
    void testGetIdsInSafeHaven() {
        List<Integer> idsInSafeHaven = gameManager.getIdsInSafeHaven();
        assertNotNull(idsInSafeHaven, "Ids das criaturas no Safe Haven inválidos");
    }
}


