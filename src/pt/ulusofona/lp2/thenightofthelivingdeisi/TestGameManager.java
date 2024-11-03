package pt.ulusofona.lp2.thenightofthelivingdeisi;

import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

public class TestGameManager {

    @Test
    public void testApanhaEquipamento() {
        // criar uma criatura humana  e um equipamento
        Creature humano = new Creature(1, 1, "Humano", 0, 0);
        Equipamento espada = new Equipamento(101, 1, 0, 0);

        // verificar se o humano pode ter o equipamento
        humano.apanhaequipamento(espada);
        assertEquals(1, humano.equipamentos.size(), "O humano deve ter 1 equipamento.");
        assertEquals(espada, humano.equipamentos.get(0), "O equipamento deve ser a espada samurai.");
    }

    @Test
    public void testLoadGameWithInvalidFile() {
        GameManager gameManager = new GameManager();
        File invalidFile = new File("test-files/invalidGameFile.txt"); //caminho relativo para o arquivo de teste

        boolean result = gameManager.loadGame(invalidFile);
        assertFalse(result, "O método loadGame deve retornar false para um arquivo de jogo inválido.");
    }

    @Test
    public void testDestruirEquipamento() {
        // criar uma criatura zombi
        Creature zombie = new Creature(2, 0, "Zombie", 1, 1);
        zombie.contadorEquipamentos = 0;  // Inicializar o contador

        // destruir equipamento
        zombie.destruirEquipamento();
        assertEquals(1, zombie.contadorEquipamentos, "O contador de destruições deve ser 1.");
        assertNull(zombie.equipamentos, "A lista de equipamentos deve ser nula após a destruição.");
    }

    @Test
    public void testMoveOutOfBounds() {
        GameManager gameManager = new GameManager();
        gameManager.tabuleiro = new Tabuleiro(5, 5); // cria um tabuleiro 5x5
        Creature creature = new Creature(2, 1, "Humano", 4, 4);
        gameManager.personagens.add(creature);

        boolean moveResult = gameManager.move(4, 4, 5, 5); // movimento fora dos limites
        assertFalse(moveResult, "O movimento deve ser inválido quando está fora dos limites do tabuleiro.");
        assertEquals(4, creature.getX(), "A posição X da criatura não deve mudar.");
        assertEquals(4, creature.getY(), "A posição Y da criatura não deve mudar.");
    }
}
