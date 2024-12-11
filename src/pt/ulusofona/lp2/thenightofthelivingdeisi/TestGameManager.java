package pt.ulusofona.lp2.thenightofthelivingdeisi;

import org.junit.Before;

import java.io.File;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestGameManager {
    GameManager gameManager;
    File initialFile;

    @Before
    public void setUp() throws Exception {
        gameManager = new GameManager();
        initialFile = new File("6x6.txt");
        gameManager.loadGame(initialFile); // Carrega o jogo usando o método loadGame
    }

    @Test
    public void testLoadGame() {
        // Verifica se o jogo foi carregado corretamente
        assertNotNull(gameManager);
        assertEquals(10, gameManager.getWorldSize()[0] * gameManager.getWorldSize()[1]); // Confirma o tamanho do tabuleiro
    }

    @Test
    public void testCreatureMovement() {
        // Movimenta um humano para uma posição válida
        assertTrue(gameManager.move(5, 6, 5, 5)); // Movimenta "James Bond"
        assertEquals("James Bond", gameManager.getCreatureInfoAsString(9));

        // Movimenta um zumbi para uma posição inválida (fora dos limites)
        assertFalse(gameManager.move(0, 1, -1, 1));

        // Movimenta um zumbi para um Safe Haven (deve ser inválido)
        assertFalse(gameManager.move(0, 1, 6, 0));
    }

    @Test
    public void testSafeHavenEntry() {
        // Verifica se humanos podem entrar no Safe Haven
        assertTrue(gameManager.move(6, 5, 6, 0)); // Move "John Wayne" para o Safe Haven

        List<Integer> idsInSafeHaven = gameManager.getIdsInSafeHaven();
        assertTrue(idsInSafeHaven.contains(9)); // "John Wayne" deve estar no Safe Haven
    }

    @Test
    public void testTransformations() {
        // Simula um ataque de zumbi a humano
        gameManager.move(5, 3, 5, 4); // "Walker" ataca "Melanie"
        String[] creatureInfo = gameManager.getCreatureInfo(7);
        assertNotNull(creatureInfo);
        assertEquals("Zombie", creatureInfo[2]); // "Melanie" foi transformada
    }

    @Test
    public void testEquipmentDestruction() {
        // Verifica a destruição de equipamentos por zumbis
        gameManager.move(1, 1, 1, 2); // "Babe" se move para um equipamento
        assertNull(gameManager.getSquareInfo(1, 2)); // Equipamento foi destruído
    }

    @Test
    public void testGameOverConditions() {
        // Simula condições de fim de jogo

        // Todos humanos entram no Safe Haven
        gameManager.move(6, 5, 6, 0); // "John Wayne"
        gameManager.move(5, 6, 6, 0); // "James Bond"

        // Verifica se o jogo termina com todos humanos salvos
        assertTrue(gameManager.gameIsOver());

        // Todos zumbis mortos
        gameManager.move(5, 4, 5, 3); // "Melanie" mata "Walker"
        assertFalse(gameManager.getSurvivors().contains("Walker")); // "Walker" foi removido
        assertTrue(gameManager.gameIsOver());
    }

    @Test
    public void testGetSurvivors() {
        // Movimenta humanos para Safe Haven e verifica os sobreviventes
        gameManager.move(6, 5, 6, 0); // "John Wayne"

        List<String> survivors = gameManager.getSurvivors();
        assertTrue(survivors.contains("9 John Wayne"));
        assertFalse(survivors.contains("James Bond")); // "James Bond" ainda está no jogo
    }

    @Test
    public void testInvalidMoves() {
        // Testa movimentos inválidos
        assertFalse(gameManager.move(5, 3, 7, 3)); // Movimento fora dos limites
        assertFalse(gameManager.move(5, 3, 5, 4)); // Zumbi tenta atacar sem sucesso
    }
}


