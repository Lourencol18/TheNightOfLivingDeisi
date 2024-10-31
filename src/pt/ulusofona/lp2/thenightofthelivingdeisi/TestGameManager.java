
package pt.ulusofona.lp2.thenightofthelivingdeisi;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class TestGameManager {

    @Test
    public void testCreatureCreation() {
        Creature creature = new Creature(1, 1, "Humano1", 0, 0);
        assertEquals(1, creature.getId());
        assertEquals(1, creature.getTipo());
        assertEquals("Humano1", creature.getNome());
        assertEquals(0, creature.getX());
        assertEquals(0, creature.getY());
        assertNull(creature.getEquipamento());
    }

    @Test
    public void testEquipamentoCreation() {
        Equipamento equipamento = new Equipamento(1, 0, 5, 5);
        assertEquals(1, equipamento.getId());
        assertEquals(0, equipamento.getTipo());
        assertEquals(5, equipamento.getX());
        assertEquals(5, equipamento.getY());
        assertEquals("1 Escudo de madeira @ (5, 5)", equipamento.toString());
    }

    @Test
    public void testCreaturePickUpEquipment() {
        Creature creature = new Creature(2, 1, "Humano2", 1, 1);
        Equipamento equipamento = new Equipamento(2, 1, 1, 1);
        creature.apanhaequipamento(equipamento);
        assertEquals(equipamento, creature.getEquipamento());
        assertEquals(1, creature.getContadorEquipamentos());
    }

    @Test
    public void testCreatureDestroyEquipment() {
        Creature creature = new Creature(3, 0, "Zombie1", 2, 2);
        creature.destruirEquipamento();
        assertNull(creature.getEquipamento());
        assertEquals(1, creature.getContadorEquipamentos());
    }

    @Test
    public void testGameManagerLoadGame() {
        GameManager gameManager = new GameManager();
        File validFile = new File("path/to/validGameFile.txt"); // Replace with a valid path for testing
        File invalidFile = new File("path/to/invalidGameFile.txt"); // Replace with an invalid path for testing

        assertTrue(gameManager.loadGame(validFile));
        assertFalse(gameManager.loadGame(invalidFile));
    }

    @Test
    public void testGameManagerMove() {
        GameManager gameManager = new GameManager();
        gameManager.tabuleiro = new Tabuleiro(10, 10); // Setting up a 10x10 board
        Creature creature = new Creature(4, 1, "Humano3", 0, 0);
        gameManager.personagens.add(creature);

        assertTrue(gameManager.move(0, 0, 1, 0)); // Valid move
        assertEquals(1, creature.getX());
        assertEquals(0, creature.getY());

        assertFalse(gameManager.move(1, 0, 10, 10)); // Move outside the board boundaries
    }
}
