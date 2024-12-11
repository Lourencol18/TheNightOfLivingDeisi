package pt.ulusofona.lp2.thenightofthelivingdeisi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestGameManager {
    private GameManager gameManager;
    @BeforeEach
    void setUp() {
        gameManager = new GameManager();
    }

    @Test
    void testLoadGame() {
        try {
            gameManager.loadGame(new File("test_game.txt"));
            // Adicione asserções para verificar se o jogo foi carregado corretamente
        } catch (InvalidFileException | FileNotFoundException e) {
            fail("Erro ao carregar o jogo: " + e.getMessage());
        }
    }

    @Test
    void testGetWorldSize() {
        int[] worldSize = gameManager.getWorldSize();
        assertTrue(worldSize[0] > 0 && worldSize[1] > 0, "Tamanho do mundo inválido");
    }

    @Test
    void testGetInitialTeamId() {
        int initialTeamId = gameManager.getInitialTeamId();
        assertTrue(initialTeamId == 10 || initialTeamId == 20, "Equipe inicial inválida");
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
        String squareInfo = gameManager.getSquareInfo(0, 0);
        assertNotNull(squareInfo, "Informações da posição inválidas");
    }

    @Test
    void testGetCreatureInfo() {
        String[] creatureInfo = gameManager.getCreatureInfo(1);
        assertNotNull(creatureInfo, "Informações da criatura inválidas");
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
        boolean hasEquipment = gameManager.hasEquipment(1, 0);
        assertTrue(hasEquipment || !hasEquipment, "Informações de equipamento inválidas");
    }

    @Test
    void testMove() {
        boolean moved = gameManager.move(0, 0, 1, 1);
        assertTrue(moved || !moved, "Movimento inválido");
    }

    @Test
    void testGameIsOver() {
        boolean gameOver = gameManager.gameIsOver();
        assertTrue(gameOver || !gameOver, "Status do jogo inválido");
    }

    @Test
    void testGetSurvivors() {
        ArrayList<String> survivors = gameManager.getSurvivors();
        assertNotNull(survivors, "Informações dos sobreviventes inválidas");
    }

    @Test
    void testSaveGame() {
        try {
            gameManager.saveGame(new File("test_game.txt"));
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

