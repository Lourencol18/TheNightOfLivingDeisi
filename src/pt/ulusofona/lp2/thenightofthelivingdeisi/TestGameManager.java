package pt.ulusofona.lp2.thenightofthelivingdeisi;

import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

public class TestGameManager {

    @Test
    public void testLoadGameWithValidFile() {
        GameManager gameManager = new GameManager();
        File validFile = new File("test-files/teste1.txt"); // Caminho relativo para o arquivo de teste

        boolean result = gameManager.loadGame(validFile);
        assertTrue(result, "O método loadGame deve retornar true para um arquivo de jogo válido.");
    }

    @Test
    public void testLoadGameWithInvalidFile() {
        GameManager gameManager = new GameManager();
        File invalidFile = new File("test-files/invalidGameFile.txt"); // Caminho relativo para o arquivo de teste

        boolean result = gameManager.loadGame(invalidFile);
        assertFalse(result, "O método loadGame deve retornar false para um arquivo de jogo inválido.");
    }

    @Test
    public void testMoveValid() {
        GameManager gameManager = new GameManager();
        gameManager.tabuleiro = new Tabuleiro(10, 10); // Cria um tabuleiro 10x10
        Creature creature = new Creature(1, 1, "Humano", 0, 0);
        gameManager.personagens.add(creature);

        boolean moveResult = gameManager.move(0, 0, 1, 0); // Movimento válido
        assertTrue(moveResult, "O movimento deve ser válido.");
        assertEquals(1, creature.getX(), "A posição X da criatura deve ser atualizada para 1.");
        assertEquals(0, creature.getY(), "A posição Y da criatura deve permanecer 0.");
    }

    @Test
    public void testMoveOutOfBounds() {
        GameManager gameManager = new GameManager();
        gameManager.tabuleiro = new Tabuleiro(5, 5); // Cria um tabuleiro 5x5
        Creature creature = new Creature(2, 1, "Humano", 4, 4);
        gameManager.personagens.add(creature);

        boolean moveResult = gameManager.move(4, 4, 5, 5); // Movimento fora dos limites
        assertFalse(moveResult, "O movimento deve ser inválido quando está fora dos limites do tabuleiro.");
        assertEquals(4, creature.getX(), "A posição X da criatura não deve mudar.");
        assertEquals(4, creature.getY(), "A posição Y da criatura não deve mudar.");
    }
}
