package pt.ulusofona.lp2.thenightofthelivingdeisi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class TestGameManager {
    private GameManager gameManager;
    private File testFile;

    @BeforeEach
    public void setUp() {
        gameManager = new GameManager();
        testFile = createTestFile();
    }

    public File createTestFile() {
        try {
            File tempFile = File.createTempFile("test-game", ".txt");
            try (PrintWriter writer = new PrintWriter(tempFile)) {
                writer.println("10 10");
                writer.println("20");
                writer.println("2");
                writer.println("1 : 20 : 1 : Humano1 : 1 : 1");
                writer.println("2 : 10 : 1 : Zumbi1 : 3 : 3");
                writer.println("1");
                writer.println("1 : 1 : 2 : 2");
                writer.println("1");
                writer.println("5 : 5");
            }
            return tempFile;
        } catch (Exception e) {
            fail("Falha ao criar arquivo de teste: " + e.getMessage());
            return null;
        }
    }

    @Test
     public void testBasicMovement() {
        try {
            gameManager.loadGame(testFile);
            assertTrue(gameManager.move(1, 1, 2, 1),
                    "Movimento válido deve ser permitido");
            assertTrue(gameManager.move(3, 3, 3, 2),
                    "Movimento válido do zumbi deve ser permitido");
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    @Test
    public void testInvalidMovement() {
        try {
            gameManager.loadGame(testFile);
            assertFalse(gameManager.move(1, 1, -1, -1),
                    "Movimento fora do tabuleiro deve ser bloqueado");
            assertFalse(gameManager.move(1, 1, 5, 5),
                    "Movimento muito distante deve ser bloqueado");
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    @Test
    public void testEquipmentPickup() {
        try {
            gameManager.loadGame(testFile);
            assertTrue(gameManager.move(1, 1, 2, 2),
                    "Humano deve poder mover até equipamento");
            String info = gameManager.getCreatureInfoAsString(1);
            assertNotNull(info, "Informação da criatura não deve ser nula");
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    @Test
   public void testBasicGameState() {
        try {
            gameManager.loadGame(testFile);
            int[] size = gameManager.getWorldSize();
            assertEquals(10, size[0], "Altura do tabuleiro deve ser 10");
            assertEquals(10, size[1], "Largura do tabuleiro deve ser 10");
            assertEquals(20, gameManager.getInitialTeamId(),
                    "Equipe inicial deve ser humanos (20)");
            assertFalse(gameManager.gameIsOver(), "Jogo não deve começar terminado");
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
   }

    @Test
   public void testSquareInfo() {
        try {
            gameManager.loadGame(testFile);
            String humanSquare = gameManager.getSquareInfo(1, 1);
            assertTrue(humanSquare.startsWith("H:"), "Deve indicar presença de humano");
            String zombieSquare = gameManager.getSquareInfo(3, 3);
            assertTrue(zombieSquare.startsWith("Z:"), "Deve indicar presença de zumbi");
            assertEquals("", gameManager.getSquareInfo(0, 0),
                    "Posição vazia deve retornar string vazia");
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
   }

    @Test
   public void testCreatureTypes() {
        Creature adulto = new Adulto(1, "Adulto", 0, 0, 20, true);
        Creature crianca = new Crianca(2, "Crianca", 1, 1, 20, true);
        Creature idoso = new Idoso(3, "Idoso", 2, 2, 20, true);
        Creature cao = new Cao(4, "Cao", 3, 3, 20, true);
        Creature vampiro = new Vampiro(5, "Vampiro", 4, 4, 10);

        assertEquals("Adulto", adulto.getTipoCriatura(), "Tipo de adulto incorreto");
        assertEquals("Criança", crianca.getTipoCriatura(), "Tipo de criança incorreto");
        assertEquals("Idoso", idoso.getTipoCriatura(), "Tipo de idoso incorreto");
        assertEquals("Cão", cao.getTipoCriatura(), "Tipo de cão incorreto");
        assertEquals("Vampiro", vampiro.getTipoCriatura(), "Tipo de vampiro incorreto");

        assertTrue(adulto.podeMover(0, 0, 1, 1, true),
                "Adulto deve poder mover na diagonal");
        assertTrue(adulto.podeMover(0, 0, 2, 0, true),
                "Adulto deve poder mover 2 casas em linha reta");
        assertFalse(adulto.podeMover(0, 0, 3, 3, true),
                "Adulto não deve poder mover 3 casas");

        assertTrue(crianca.podeMover(0, 0, 0, 1, true),
                "Criança deve poder mover 1 casa vertical");
        assertFalse(crianca.podeMover(0, 0, 1, 1, true),
                "Criança não deve poder mover na diagonal");
   }

    @Test
   public void testEquipmentInteractions() {
        Creature adulto = new Adulto(1, "Humano", 0, 0, 20, true);
        Equipamento escudo = new EscudoDeMadeira(1, 0, 0, 0);

        adulto.pegarEquipamento(escudo);
        assertEquals(1, adulto.getContadorEquipamentos(),
                "Contador de equipamentos deve ser 1");
        assertNotNull(adulto.getEquipamentoAtual(),
                "Equipamento atual não deve ser nulo");

        adulto.soltarEquipamento();
        assertNull(adulto.getEquipamentoAtual(),
                "Equipamento atual deve ser nulo após soltar");

        Equipamento espada = new EspadaSamurai(2, 1, 0, 0);
        adulto.pegarEquipamento(espada);
        assertEquals(2, adulto.getContadorEquipamentos(),
                "Contador de equipamentos deve ser 2");
   }

    @Test
   public void testVampiroComportamentoNoturno() {
        Vampiro vampiro = new Vampiro(1, "Drac", 0, 0, 10);

        assertFalse(vampiro.podeMover(0, 0, 1, 0, true),
                "Vampiro não deve mover de dia");
        assertTrue(vampiro.podeMover(0, 0, 1, 0, false),
                "Vampiro deve poder mover à noite");
        assertTrue(vampiro.podeMover(0, 0, 1, 1, false),
                "Vampiro deve poder mover na diagonal à noite");
   }

    @Test
   public void testIdosoComportamentoEspecial() {
        Idoso idoso = new Idoso(1, "Anciao", 0, 0, 20, true);

        assertTrue(idoso.podeMover(0, 0, 1, 1, true),
                "Idoso deve poder mover na diagonal durante o dia");
        assertFalse(idoso.podeMover(0, 0, 1, 1, false),
                "Idoso humano não deve mover durante a noite");
        assertFalse(idoso.podeMover(0, 0, 1, 0, true),
                "Idoso não deve mover horizontalmente");
        assertFalse(idoso.podeMover(0, 0, 2, 2, true),
                "Idoso não deve mover mais de uma casa");
   }

    @Test
   public void testGameSaveLoad() {
        try {
            File saveFile = new File("save-test.txt");
            gameManager.loadGame(testFile);
            gameManager.saveGame(saveFile);
            assertTrue(saveFile.exists(), "Arquivo de save deve ser criado");

            GameManager newManager = new GameManager();
            newManager.loadGame(saveFile);

            assertEquals(gameManager.getWorldSize()[0], newManager.getWorldSize()[0],
                    "Dimensões devem ser preservadas");
            assertEquals(gameManager.getInitialTeamId(), newManager.getInitialTeamId(),
                    "Equipe inicial deve ser preservada");

            saveFile.delete();
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
   }

    @Test
   public void testSafeHavenOperations() {
        SafeHaven safeHaven = new SafeHaven(5, 5);
        Creature humano = new Adulto(1, "Humano", 5, 5, 20, true);

        safeHaven.entrar(humano);
        List<Creature> criaturas = safeHaven.getCriaturasDentro();

        assertEquals(1, criaturas.size(), "Deve ter uma criatura no safe haven");
        assertEquals(humano.getId(), criaturas.get(0).getId(), "IDs devem corresponder");
        assertEquals(5, safeHaven.getX(), "Coordenada X deve ser preservada");
        assertEquals(5, safeHaven.getY(), "Coordenada Y deve ser preservada");
   }

    @Test
   public void testEquipamentoEfeitos() {
        Creature humano = new Adulto(1, "Humano", 0, 0, 20, true);
        Creature zumbi = new Adulto(2, "Zumbi", 1, 1, 10, false);

        Lixivia lixivia = new Lixivia(1, 3, 0, 0);
        assertTrue(lixivia.executarAcao(humano, zumbi),
                "Lixívia deve poder ser usada");
        assertEquals("0.7 litros", lixivia.getInfo(),
                "Volume deve ser reduzido após uso");

        assertTrue(lixivia.executarAcao(humano, zumbi),
                "Lixívia deve poder ser usada novamente");
        assertTrue(lixivia.executarAcao(humano, zumbi),
                "Lixívia deve poder ser usada uma terceira vez");
   }

    @Test
   public void testPistolaComportamento() {
        PistolaWaltherPPK pistola = new PistolaWaltherPPK(1, 2, 0, 0);

        assertTrue(pistola.temBalas(), "Pistola deve começar com balas");
        assertEquals("3 balas", pistola.getInfo(),
                "Pistola deve começar com 3 balas");

        pistola.gastarBala();
        assertTrue(pistola.temBalas(), "Pistola deve ainda ter balas");
        assertEquals("2 balas", pistola.getInfo(),
                "Pistola deve ter 2 balas após gastar uma");

        pistola.gastarBala();
        pistola.gastarBala();
        assertFalse(pistola.temBalas(), "Pistola não deve ter mais balas");
        assertEquals("0 balas", pistola.getInfo(),
                "Pistola deve mostrar 0 balas quando vazia");
   }

    @Test
    public void testGameOverConditions() {
        try {
            File testFile = new File("gameover-test.txt");
            try (PrintWriter writer = new PrintWriter(testFile)) {
                writer.println("10 10");
                writer.println("20");
                writer.println("2");
                writer.println("1 : 20 : 1 : Humano1 : 1 : 1");
                writer.println("2 : 10 : 1 : Zumbi1 : 2 : 2");
                writer.println("1");
                writer.println("1 : 1 : 3 : 3");
                writer.println("0");
            }

            gameManager.loadGame(testFile);
            assertTrue(gameManager.move(1, 1, 3, 3),
                    "Humano deve poder pegar espada");
            assertTrue(gameManager.move(2, 2, 2, 3),
                    "Zumbi deve poder mover");
            assertTrue(gameManager.move(3, 3, 2, 3),
                    "Humano deve poder atacar zumbi");

            ArrayList<String> survivors = gameManager.getSurvivors();
            assertTrue(survivors.contains("OS VIVOS"),
                    "Deve mostrar sobreviventes humanos");

            // Verifica se alguma string da lista contém o ID do humano
            boolean containsId = survivors.stream()
                    .anyMatch(s -> s.contains("1")); // Procura pelo ID 1 em qualquer parte das strings
            assertTrue(containsId, "Deve conter o ID do humano sobrevivente em alguma parte da string");

            testFile.delete();
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    public File createBasicTestFile() {
        try {
            File tempFile = File.createTempFile("test-game", ".txt");
            try (PrintWriter writer = new PrintWriter(tempFile)) {
                writer.println("10 10");
                writer.println("20");
                writer.println("2");
                writer.println("1 : 20 : 1 : Humano1 : 1 : 1");
                writer.println("2 : 10 : 1 : Zumbi1 : 3 : 3");
                writer.println("1");
                writer.println("1 : 0 : 2 : 2");
                writer.println("1");
                writer.println("5 : 5");
            }
            return tempFile;
        } catch (IOException e) {
            fail("Falha ao criar arquivo de teste: " + e.getMessage());
            return null;
        }
    }

    @Test
   public void testDayNightCycle() {
        try {
            File testFile = new File("daynight-test.txt");
            try (PrintWriter writer = new PrintWriter(testFile)) {
                writer.println("10 10");
                writer.println("20");
                writer.println("2");
                writer.println("1 : 20 : 1 : Humano1 : 1 : 1");
                writer.println("2 : 10 : 1 : Zumbi1 : 3 : 3");
                writer.println("0");
                writer.println("0");
            }
            gameManager.loadGame(testFile);

            boolean initialDay = gameManager.isDay();

            // Fazer movimentos para mudar o período
            gameManager.move(1, 1, 1, 2);
            gameManager.move(3, 3, 3, 2);

            assertNotEquals(initialDay, gameManager.isDay(),
                    "Período deve mudar após 2 turnos");

            testFile.delete();
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
   }

    @Test
   public void testCaoComportamentoCompleto() {
        Cao cao = new Cao(1, "Rex", 0, 0, 20, true);

        assertEquals("Cão", cao.getTipoCriatura(),
                "Tipo da criatura deve ser Cão");
        assertEquals("Humano", cao.getTipo(),
                "Tipo deve ser Humano");
        assertTrue(cao.isHuman(),
                "Cão deve ser considerado humano");

        Equipamento equip = new EscudoDeMadeira(1, 0, 0, 0);
        assertFalse(cao.podePegarEquipamento(equip),
                "Cão não deve poder pegar equipamentos");
        assertFalse(cao.podeMoverParaComEquipamento(equip),
                "Cão não deve poder mover com equipamentos");

        cao.destruirEquipamento();
        assertEquals(0, cao.getContadorEquipamentos(),
                "Contador de equipamentos deve permanecer zero");
   }

    @Test
   public   void testEquipamentoTransformacao() {
        Creature humano = new Adulto(1, "Humano", 0, 0, 20, true);

        assertFalse(humano.isTransformed(), "Não deve começar transformado");
        assertTrue(humano.isHuman(), "Deve começar humano");
        assertFalse(humano.isZombie(), "Não deve começar zumbi");

        humano.transformar();
        humano.setEquipa(10);

        assertTrue(humano.isTransformed(), "Deve estar transformado");
        assertFalse(humano.isHuman(), "Não deve mais ser humano");
        assertTrue(humano.isZombie(), "Deve ser zumbi");
   }

    @Test
    public void testEquipamentosPropagacaoCoordenadas() {
        Creature humano = new Adulto(1, "Humano", 0, 0, 20, true);
        Equipamento equip = new EscudoDeMadeira(1, 0, 0, 0);

        humano.pegarEquipamento(equip);
        humano.setX(5);
        humano.setY(3);

        assertEquals(5, humano.getX(), "X da criatura deve ser atualizado");
        assertEquals(3, humano.getY(), "Y da criatura deve ser atualizado");
        assertEquals(5, equip.getX(), "X do equipamento deve seguir criatura");
        assertEquals(3, equip.getY(), "Y do equipamento deve seguir criatura");
   }

    @Test
    public void testSafeHavenInteracoesComplexas() {
        SafeHaven safeHaven = new SafeHaven(5, 5);
        Creature humano1 = new Adulto(1, "Humano1", 5, 5, 20, true);
        Creature humano2 = new Crianca(2, "Humano2", 5, 5, 20, true);

        safeHaven.entrar(humano1);
        safeHaven.entrar(humano2);

        List<Creature> criaturas = safeHaven.getCriaturasDentro();
        assertEquals(2, criaturas.size(),
                "Safe Haven deve conter duas criaturas");

        safeHaven.entrar(humano1);
        assertEquals(2, safeHaven.getCriaturasDentro().size(),
                "Não deve permitir duplicatas");

        SafeHaven.add(safeHaven);
        assertTrue(SafeHaven.getSafeHavens().contains(safeHaven),
                "Safe Haven deve estar registrado globalmente");
    }

    @Test
   public void testLixiviaComportamentoDetalhado() {
        Creature humano = new Adulto(1, "Humano", 0, 0, 20, true);
        Creature zumbi = new Adulto(2, "Zumbi", 1, 1, 10, false);
        Lixivia lixivia = new Lixivia(1, 3, 0, 0);

        assertTrue(lixivia.isDefensivo(),
                "Lixívia deve ser considerada defensiva");
        assertTrue(lixivia.executarAcao(humano, zumbi),
                "Deve poder executar ação");
        assertEquals("0.7 litros", lixivia.getInfo(),
                "Volume deve diminuir corretamente");

        for (int i = 0; i < 2; i++) {
            lixivia.executarAcao(humano, zumbi);
        }
        assertEquals("0.1 litros", lixivia.getInfo(),
                "Volume deve diminuir após múltiplos usos");
   }

    @Test
    public void testMovimentosEspeciais() {
        Adulto adulto = new Adulto(1, "Humano", 1, 1, 20, true);

        assertTrue(adulto.podeMover(1, 1, 2, 2, true),
                "Adulto deve poder mover na diagonal");
        assertTrue(adulto.podeMover(1, 1, 3, 1, true),
                "Adulto deve poder mover duas casas");

        Equipamento espada = new EspadaSamurai(1, 1, 1, 1);
        adulto.pegarEquipamento(espada);
        assertTrue(adulto.podeMoverParaComEquipamento(espada),
                "Adulto deve poder mover com equipamento");

        assertFalse(adulto.podeMover(1, 1, 4, 1, true),
                "Adulto não deve poder mover três casas");
        assertFalse(adulto.podeMover(1, 1, 3, 2, true),
                "Adulto não deve mover em L");
    }

    @Test
   public void testCriancaComportamentoCompleto() {
        Crianca crianca = new Crianca(1, "Mini", 0, 0, 20, true);

        // Movimentos válidos
        assertTrue(crianca.podeMover(0, 0, 0, 1, true),
                "Criança deve poder mover verticalmente");
        assertTrue(crianca.podeMover(0, 0, 1, 0, true),
                "Criança deve poder mover horizontalmente");

        // Movimentos inválidos
        assertFalse(crianca.podeMover(0, 0, 1, 1, true),
                "Criança não deve poder mover na diagonal");
        assertFalse(crianca.podeMover(0, 0, 0, 2, true),
                "Criança não deve poder mover duas casas");

        // Teste equipamentos
        Equipamento escudo = new EscudoDeMadeira(1, 0, 0, 0);
        Equipamento espada = new EspadaSamurai(2, 1, 0, 0);

        assertTrue(crianca.podePegarEquipamento(escudo),
                "Criança deve poder pegar equipamento defensivo");
        assertFalse(crianca.podePegarEquipamento(espada),
                "Criança não deve poder pegar equipamento ofensivo");

        assertThrows(IllegalStateException.class,
                () -> crianca.pegarEquipamento(espada),
                "Deve lançar exceção ao tentar pegar equipamento ofensivo");
   }

    @Test
   public void testGameStateAfterLoad() {
        try {
            gameManager.loadGame(testFile);

            // Verifica estado inicial do jogo
            assertTrue(gameManager.isDay(), "Jogo deve começar durante o dia");
            assertEquals(20, gameManager.getCurrentTeamId(), "Equipe inicial deve ser humanos (20)");

            // Verifica se as estruturas foram inicializadas corretamente
            assertNotNull(gameManager.getWorldSize(), "Dimensões do mundo devem estar definidas");
            assertFalse(gameManager.gameIsOver(), "Jogo não deve começar terminado");
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
   }

    @Test
    public void testInvalidFileLoad() {
        File invalidFile = new File("invalid.txt");
        try (PrintWriter writer = new PrintWriter(invalidFile)) {
            writer.println("10"); // Dimensões incompletas
            writer.println("30"); // Equipe inválida
        } catch (IOException e) {
            fail("Falha ao criar arquivo de teste");
        }

        assertThrows(InvalidFileException.class, () -> {
            gameManager.loadGame(invalidFile);
        }, "Deve lançar exceção para arquivo inválido");

        invalidFile.delete();
    }

    @Test
   public void testEquipmentPickupRules() {
        try {
            File testFile = new File("equipment-test.txt");
            try (PrintWriter writer = new PrintWriter(testFile)) {
                writer.println("10 10");
                writer.println("20");
                writer.println("3");
                writer.println("1 : 20 : 0 : Crianca : 1 : 1"); // Criança
                writer.println("2 : 20 : 1 : Adulto : 2 : 2");  // Adulto
                writer.println("3 : 10 : 4 : Vampiro : 3 : 3"); // Vampiro
                writer.println("2");
                writer.println("1 : 1 : 4 : 4"); // Espada
                writer.println("2 : 0 : 5 : 5"); // Escudo
                writer.println("0");
            }

            gameManager.loadGame(testFile);

            // Criança não pode pegar espada
            assertFalse(gameManager.move(1, 1, 4, 4),
                    "Criança não deve poder pegar espada");

            // Adulto pode pegar espada
            assertTrue(gameManager.move(2, 2, 4, 4),
                    "Adulto deve poder pegar espada");

            // Vampiro não pode pegar equipamentos
            assertFalse(gameManager.move(3, 3, 5, 5),
                    "Vampiro não deve poder pegar equipamentos");

            testFile.delete();
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
   }

    @Test
   public void testCreatureMovementLimits() {
        try {
            gameManager.loadGame(testFile);

            // Teste de movimentos inválidos
            assertFalse(gameManager.move(1, 1, 4, 4),
                    "Não deve permitir movimento além do limite da criatura");
            assertFalse(gameManager.move(1, 1, 1, 1),
                    "Não deve permitir movimento para mesma posição");
            assertFalse(gameManager.move(1, 1, -1, 1),
                    "Não deve permitir movimento para coordenada negativa");
            assertFalse(gameManager.move(1, 1, 10, 10),
                    "Não deve permitir movimento para fora do tabuleiro");

        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
   }

    @Test
    public void testSafeHavenWithZombies() {
        try {
            File testFile = new File("safehaven-zombie-test.txt");
            try (PrintWriter writer = new PrintWriter(testFile)) {
                writer.println("10 10");
                writer.println("20");
                writer.println("2");
                writer.println("1 : 10 : 1 : Zumbi : 4 : 4");   // Zumbi em (4,4)
                writer.println("2 : 20 : 1 : Humano : 4 : 3");  // Humano em (4,3)
                writer.println("1");                       // Precisa ter equipamento
                writer.println("1 : 0 : 4 : 2");          // Escudo em (4,2)
                writer.println("1");                       // Número de Safe Havens
                writer.println("5 : 4");                   // Safe Haven em (5,4)
            }

            gameManager.loadGame(testFile);

            // Humano pega equipamento
            assertTrue(gameManager.move(4, 3, 4, 2),
                    "Humano deve poder pegar equipamento");

            // Turno do zumbi
            assertTrue(gameManager.move(4, 4, 3, 4),
                    "Zumbi deve poder se mover");

            // Humano move para o Safe Haven em dois passos
            assertTrue(gameManager.move(4, 2, 5, 3),
                    "Humano deve poder se mover em direção ao Safe Haven");

            // Turno do zumbi
            assertTrue(gameManager.move(3, 4, 4, 4),
                    "Zumbi deve poder se mover");

            // Humano entra no Safe Haven
            assertTrue(gameManager.move(5, 3, 5, 4),
                    "Humano deve poder entrar no Safe Haven");

            testFile.delete();
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    @Test
    public void testEquipmentInfoMethods() {
        try {
            File testFile = new File("equipment-info-test.txt");
            try (PrintWriter writer = new PrintWriter(testFile)) {
                writer.println("10 10");
                writer.println("20");
                writer.println("1");
                writer.println("1 : 20 : 1 : Humano : 1 : 1");
                writer.println("3");                          // 3 equipamentos diferentes
                writer.println("1 : 0 : 2 : 2");  // Escudo
                writer.println("2 : 1 : 3 : 3");  // Espada
                writer.println("3 : 2 : 4 : 4");  // Pistola
                writer.println("0");
            }

            gameManager.loadGame(testFile);

            // Teste getEquipmentInfo
            String[] infoEscudo = gameManager.getEquipmentInfo(1);
            assertNotNull(infoEscudo, "Info do escudo não deve ser nula");
            assertEquals("1", infoEscudo[0], "ID do escudo deve ser 1");
            assertEquals("0", infoEscudo[1], "Tipo do escudo deve ser 0");

            // Teste getEquipmentInfoAsString
            String infoEscudoString = gameManager.getEquipmentInfoAsString(1);
            assertNotNull(infoEscudoString, "String info do escudo não deve ser nula");
            assertTrue(infoEscudoString.contains("1"), "String deve conter ID do escudo");

            // Teste hasEquipment antes de pegar
            assertFalse(gameManager.hasEquipment(1, 0),
                    "Humano não deve ter equipamento inicialmente");

            // Humano pega escudo
            assertTrue(gameManager.move(1, 1, 2, 2),
                    "Humano deve poder pegar escudo");

            // Teste hasEquipment depois de pegar
            assertTrue(gameManager.hasEquipment(1, 0),
                    "Humano deve ter escudo após pegá-lo");

            testFile.delete();
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    @Test
    public void testSafeHavenIds() {
        try {
            File testFile = new File("safehaven-ids-test.txt");
            try (PrintWriter writer = new PrintWriter(testFile)) {
                writer.println("10 10");
                writer.println("20");  // Começa com humanos
                writer.println("3");   // 3 criaturas
                writer.println("1 : 20 : 1 : Humano1 : 2 : 2");  // Humano mais perto do Safe Haven
                writer.println("2 : 20 : 1 : Humano2 : 1 : 1");
                writer.println("3 : 10 : 1 : Zumbi : 3 : 3");
                writer.println("1");   // 1 equipamento
                writer.println("1 : 0 : 2 : 3");  // Escudo em (2,3)
                writer.println("1");   // 1 Safe Haven
                writer.println("2 : 4"); // Safe Haven em (2,4)
            }

            gameManager.loadGame(testFile);

            // Inicialmente não deve haver ninguém no Safe Haven
            List<Integer> ids = gameManager.getIdsInSafeHaven();
            assertTrue(ids.isEmpty(), "Safe Haven deve começar vazio");

            // Primeiro humano pega o equipamento
            assertTrue(gameManager.move(2, 2, 2, 3),
                    "Humano1 deve poder pegar o escudo");

            // Zumbi se move
            assertTrue(gameManager.move(3, 3, 3, 2),
                    "Zumbi deve poder se mover");

            // Primeiro humano entra no Safe Haven
            assertTrue(gameManager.move(2, 3, 2, 4),
                    "Humano1 deve poder entrar no Safe Haven");

            // Verifica se o primeiro humano está no Safe Haven
            ids = gameManager.getIdsInSafeHaven();
            assertTrue(ids.contains(1), "ID 1 deve estar no Safe Haven");
            assertEquals(1, ids.size(), "Deve haver apenas 1 ID no Safe Haven");

            testFile.delete();
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    @Test
    public void testEquipmentInfoNonExistent() {
        try {
            File testFile = new File("equipment-nonexistent-test.txt");
            try (PrintWriter writer = new PrintWriter(testFile)) {
                writer.println("10 10");
                writer.println("20");
                writer.println("1");
                writer.println("1 : 20 : 1 : Humano : 1 : 1");
                writer.println("1");
                writer.println("1 : 0 : 2 : 2");  // Apenas um escudo
                writer.println("0");
            }

            gameManager.loadGame(testFile);

            // Testa ID inexistente
            assertNull(gameManager.getEquipmentInfo(999),
                    "Info de equipamento inexistente deve ser null");
            assertNull(gameManager.getEquipmentInfoAsString(999),
                    "String info de equipamento inexistente deve ser null");

            // Testa hasEquipment com ID inexistente
            assertFalse(gameManager.hasEquipment(999, 0),
                    "hasEquipment deve retornar false para ID inexistente");

            testFile.delete();
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    @Test
    public void testMultipleEquipmentTypes() {
        try {
            File testFile = new File("equipment-types-test.txt");
            try (PrintWriter writer = new PrintWriter(testFile)) {
                writer.println("10 10");
                writer.println("20");
                writer.println("1");
                writer.println("1 : 20 : 1 : Humano : 1 : 1");
                writer.println("4");  // Todos os tipos de equipamento
                writer.println("1 : 0 : 2 : 2");  // Escudo
                writer.println("2 : 1 : 3 : 3");  // Espada
                writer.println("3 : 2 : 4 : 4");  // Pistola
                writer.println("4 : 3 : 5 : 5");  // Lixívia
                writer.println("0");
            }

            gameManager.loadGame(testFile);

            // Verifica info de cada tipo de equipamento
            for (int i = 1; i <= 4; i++) {
                String[] info = gameManager.getEquipmentInfo(i);
                assertNotNull(info, "Info do equipamento " + i + " não deve ser nula");
                assertEquals(String.valueOf(i), info[0], "ID deve corresponder");
            }

            // Verifica hasEquipment antes e depois de pegar cada equipamento
            for (int type = 0; type <= 3; type++) {
                assertFalse(gameManager.hasEquipment(1, type),
                        "Não deve ter equipamento tipo " + type + " inicialmente");
            }

            testFile.delete();
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    @Test
    public void testCaoMethods() {
        try {
            File testFile = new File("cao-test.txt");
            try (PrintWriter writer = new PrintWriter(testFile)) {
                writer.println("10 10");
                writer.println("20");
                writer.println("2");
                writer.println("1 : 20 : 3 : Rex : 1 : 1");  // Cão em (1,1)
                writer.println("2 : 10 : 4 : Vampiro : 3 : 3");  // Vampiro em (3,3)
                writer.println("2");  // 2 equipamentos
                writer.println("1 : 0 : 2 : 2"); // Escudo
                writer.println("2 : 1 : 4 : 4"); // Espada
                writer.println("0");
            }

            gameManager.loadGame(testFile);

            // Teste movimento do cão
            assertTrue(gameManager.move(1, 1, 1, 3),
                    "Cão deve poder mover 2 casas em linha reta");

            assertFalse(gameManager.move(1, 3, 2, 4),
                    "Cão não deve poder mover na diagonal");

            // Tenta pegar equipamento (não deve conseguir)
            assertFalse(gameManager.move(1, 3, 2, 2),
                    "Cão não deve poder pegar equipamento");

            // Verifica informações do cão
            String[] infoCao = gameManager.getCreatureInfo(1);
            assertEquals("Cão", infoCao[1], "Tipo deve ser Cão");
            assertTrue(infoCao[2].equals("Humano"), "Cão deve ser do tipo Humano");

            testFile.delete();
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    @Test
    public void testEspadaSamuraiUsage() {
        try {
            File testFile = new File("espada-test.txt");
            try (PrintWriter writer = new PrintWriter(testFile)) {
                writer.println("10 10");
                writer.println("20");
                writer.println("2");
                writer.println("1 : 20 : 1 : Humano : 1 : 1");  // Humano
                writer.println("2 : 10 : 4 : Vampiro : 3 : 3");  // Vampiro
                writer.println("1");
                writer.println("1 : 1 : 1 : 2"); // Espada adjacente ao humano
                writer.println("0");
            }

            gameManager.loadGame(testFile);

            // Pega a espada
            assertTrue(gameManager.move(1, 1, 1, 2),
                    "Humano deve poder pegar a espada");

            // Verifica se pegou a espada
            assertTrue(gameManager.hasEquipment(1, 1),
                    "Humano deve ter equipamento tipo 1 (espada)");

            testFile.delete();
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    @Test
    public void testCriancaCompleto() {
        try {
            File testFile = new File("crianca-test.txt");
            try (PrintWriter writer = new PrintWriter(testFile)) {
                writer.println("10 10");
                writer.println("20");           // Começa com turno dos humanos
                writer.println("1");            // Uma criatura
                writer.println("1 : 20 : 0 : Crianca : 2 : 2");  // Criança em (2,2)
                writer.println("1");            // Um equipamento
                writer.println("1 : 0 : 2 : 3"); // Escudo defensivo em (2,3)
                writer.println("0");
            }

            gameManager.loadGame(testFile);

            // Verifica se a criança está na posição correta
            assertNotNull(gameManager.getCreatureInfo(1), "Deve encontrar a criança");

            // Testa movimento ortogonal
            assertTrue(gameManager.move(2, 2, 2, 3),
                    "Criança deve poder mover uma casa na vertical para pegar escudo");

            // Verifica se pegou o escudo
            assertTrue(gameManager.hasEquipment(1, 0),
                    "Criança deve ter o escudo (tipo 0)");

            testFile.delete();
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }



    @Test
    public void testGameManagerStates() {
        try {
            File testFile = new File("gamemanager-test.txt");
            try (PrintWriter writer = new PrintWriter(testFile)) {
                writer.println("10 10");
                writer.println("20");
                writer.println("2");
                writer.println("1 : 20 : 1 : Humano : 1 : 1");
                writer.println("2 : 10 : 4 : Vampiro : 2 : 2");
                writer.println("0");
                writer.println("0");
            }

            gameManager.loadGame(testFile);

            // Testa estados do jogo
            assertTrue(gameManager.isDay(), "Jogo deve começar de dia");
            assertEquals(20, gameManager.getCurrentTeamId(),
                    "Deve começar com equipe humana");

            // Faz um movimento e verifica mudança de turno
            assertTrue(gameManager.move(1, 1, 1, 2));
            assertEquals(10, gameManager.getCurrentTeamId(),
                    "Deve mudar para equipe zumbi");

            // Testa informações do tabuleiro
            String squareInfo = gameManager.getSquareInfo(1, 2);
            assertTrue(squareInfo.startsWith("H:"),
                    "Deve mostrar humano na posição");

            testFile.delete();
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }


    @Test
    public void testIdosoMovementComplete() {
        try {
            File testFile = new File("idoso-test.txt");
            try (PrintWriter writer = new PrintWriter(testFile)) {
                writer.println("10 10");
                writer.println("20");
                writer.println("1");
                writer.println("1 : 20 : 2 : Idoso : 2 : 2");  // Idoso no centro
                writer.println("1");
                writer.println("1 : 0 : 3 : 3"); // Escudo
                writer.println("0");
            }

            gameManager.loadGame(testFile);

            // Testa movimento diagonal (deve ser permitido)
            assertTrue(gameManager.move(2, 2, 3, 3),
                    "Idoso deve poder mover na diagonal");

            // Testa pegar equipamento
            assertTrue(gameManager.hasEquipment(1, 0),
                    "Idoso deve poder pegar equipamento defensivo");

            // Testa movimento inválido (horizontal)
            assertFalse(gameManager.move(3, 3, 4, 3),
                    "Idoso não deve poder mover horizontalmente");

            testFile.delete();
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    @Test
    public void testIdosoMovimentoDiagonal() {
        try {
            File testFile = new File("idoso-test.txt");
            try (PrintWriter writer = new PrintWriter(testFile)) {
                writer.println("10 10");
                writer.println("20");
                writer.println("1");
                writer.println("1 : 20 : 2 : IdosoTeste : 2 : 2");  // Idoso no centro
                writer.println("0");  // sem equipamentos
                writer.println("0");  // sem safe havens
            }

            gameManager.loadGame(testFile);

            // Movimento diagonal permitido para Idoso
            assertTrue(gameManager.move(2, 2, 3, 3),
                    "Idoso deve poder mover na diagonal");

            testFile.delete();
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    @Test
    public void testPistolaWaltherPPKBalas() {
        try {
            File testFile = new File("pistola-test.txt");
            try (PrintWriter writer = new PrintWriter(testFile)) {
                writer.println("10 10");
                writer.println("20");
                writer.println("2");
                writer.println("1 : 20 : 1 : Humano : 1 : 1");  // Adulto
                writer.println("2 : 10 : 4 : Vampiro : 5 : 1");  // Vampiro na mesma linha
                writer.println("1");
                writer.println("1 : 2 : 1 : 2");  // Pistola adjacente
                writer.println("0");
            }

            gameManager.loadGame(testFile);

            // Pegar a pistola
            assertTrue(gameManager.move(1, 1, 1, 2),
                    "Humano deve poder pegar a pistola");

            // Verificar se tem a pistola
            assertTrue(gameManager.hasEquipment(1, 2),
                    "Humano deve ter a pistola equipada");

            testFile.delete();
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    @Test
    public void testEspadaSamuraiAtaque() {
        try {
            File testFile = new File("espada-test.txt");
            try (PrintWriter writer = new PrintWriter(testFile)) {
                writer.println("10 10");
                writer.println("20");
                writer.println("2");
                writer.println("1 : 20 : 1 : Humano : 2 : 2");  // Adulto
                writer.println("2 : 10 : 4 : Vampiro : 2 : 3");  // Vampiro adjacente
                writer.println("1");
                writer.println("1 : 1 : 2 : 1");  // Espada
                writer.println("0");
            }

            gameManager.loadGame(testFile);

            // Pegar a espada
            assertTrue(gameManager.move(2, 2, 2, 1),
                    "Humano deve poder pegar a espada");

            // Verificar se pegou a espada
            assertTrue(gameManager.hasEquipment(1, 1),
                    "Humano deve ter a espada equipada");

            testFile.delete();
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    @Test
    public void testCriarLixivia() {
        try {
            File testFile = new File("lixivia-test.txt");
            try (PrintWriter writer = new PrintWriter(testFile)) {
                writer.println("10 10");
                writer.println("20");
                writer.println("1");
                writer.println("1 : 20 : 1 : Humano : 1 : 1");  // Adulto
                writer.println("1");
                writer.println("1 : 3 : 2 : 2");  // Lixívia em (2,2)
                writer.println("0");
            }

            gameManager.loadGame(testFile);

            // Pegar a lixívia
            assertTrue(gameManager.move(1, 1, 2, 2),
                    "Humano deve poder pegar a lixívia");

            // Verificar se tem a lixívia
            assertTrue(gameManager.hasEquipment(1, 3),
                    "Humano deve ter a lixívia equipada");

            testFile.delete();
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    @Test
    public void testEscudoDeMadeiraDefesa() {
        try {
            File testFile = new File("escudo-test.txt");
            try (PrintWriter writer = new PrintWriter(testFile)) {
                writer.println("10 10");
                writer.println("20");
                writer.println("1");
                writer.println("1 : 20 : 1 : Humano : 1 : 1");  // Adulto
                writer.println("1");
                writer.println("1 : 0 : 2 : 2");  // Escudo em (2,2)
                writer.println("0");
            }

            gameManager.loadGame(testFile);

            // Pegar o escudo
            assertTrue(gameManager.move(1, 1, 2, 2),
                    "Humano deve poder pegar o escudo");

            // Verificar se tem o escudo
            assertTrue(gameManager.hasEquipment(1, 0),
                    "Humano deve ter o escudo equipado");

            testFile.delete();
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    @Test
    public void testMovimentosEspeciaisIdoso() {
        try {
            File testFile = new File("idoso-especial-test.txt");
            try (PrintWriter writer = new PrintWriter(testFile)) {
                writer.println("10 10");
                writer.println("20");
                writer.println("1");
                writer.println("1 : 20 : 2 : IdosoTeste : 2 : 2");
                writer.println("1");
                writer.println("1 : 0 : 3 : 3"); // Escudo na diagonal
                writer.println("0");
            }

            gameManager.loadGame(testFile);

            // Movimento na horizontal não permitido
            assertFalse(gameManager.move(2, 2, 3, 2),
                    "Idoso não deve poder mover horizontalmente");

            // Movimento na vertical não permitido
            assertFalse(gameManager.move(2, 2, 2, 3),
                    "Idoso não deve poder mover verticalmente");

            // Movimento diagonal de duas casas não permitido
            assertFalse(gameManager.move(2, 2, 4, 4),
                    "Idoso não deve poder mover duas casas na diagonal");

            testFile.delete();
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }


    @Test
    public void testCaoEspecial() {
        try {
            File testFile = new File("cao-especial.txt");
            try (PrintWriter writer = new PrintWriter(testFile)) {
                writer.println("10 10");
                writer.println("20");
                writer.println("1");
                writer.println("1 : 20 : 3 : Cão : 2 : 2"); // Cão no centro
                writer.println("1");
                writer.println("1 : 0 : 3 : 2"); // Equipamento
                writer.println("0");
            }

            gameManager.loadGame(testFile);

            // Tentar pegar equipamento
            assertFalse(gameManager.move(2, 2, 3, 2),
                    "Cão não deve poder pegar equipamento");

            // Movimento diagonal não permitido
            assertFalse(gameManager.move(2, 2, 3, 3),
                    "Cão não deve poder mover na diagonal");

            // Movimento duas casas permitido
            assertTrue(gameManager.move(2, 2, 2, 4),
                    "Cão deve poder mover duas casas em linha reta");

            testFile.delete();
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    @Test
    public void testLixiviaCompleto() {
        try {
            File testFile = new File("lixivia-completo.txt");
            try (PrintWriter writer = new PrintWriter(testFile)) {
                writer.println("10 10");
                writer.println("20");
                writer.println("2");
                writer.println("1 : 20 : 1 : Humano : 1 : 1"); // Humano
                writer.println("2 : 10 : 4 : Vampiro : 3 : 1"); // Vampiro na mesma linha
                writer.println("1");
                writer.println("1 : 3 : 1 : 2"); // Lixívia
                writer.println("0");
            }

            gameManager.loadGame(testFile);

            // Pegar a lixívia
            assertTrue(gameManager.move(1, 1, 1, 2),
                    "Deve poder pegar a lixívia");

            // Verificar se tem a lixívia
            assertTrue(gameManager.hasEquipment(1, 3),
                    "Deve ter a lixívia equipada");

            testFile.delete();
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    @Test
    public void testCriancaComEquipamentos() {
        try {
            File testFile = new File("crianca-equipamentos.txt");
            try (PrintWriter writer = new PrintWriter(testFile)) {
                writer.println("10 10");
                writer.println("20");
                writer.println("1");
                writer.println("1 : 20 : 0 : Criança : 2 : 2");
                writer.println("2");
                writer.println("1 : 0 : 3 : 2"); // Escudo (defensivo)
                writer.println("2 : 1 : 2 : 3"); // Espada (ofensivo)
                writer.println("0");
            }

            gameManager.loadGame(testFile);

            // Tentar pegar espada (não deve conseguir)
            assertFalse(gameManager.move(2, 2, 2, 3),
                    "Criança não deve poder pegar equipamento ofensivo");

            // Pode pegar escudo
            assertTrue(gameManager.move(2, 2, 3, 2),
                    "Criança deve poder pegar equipamento defensivo");

            testFile.delete();
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    @Test
    public void testCriancaLimitacoes() {
        try {
            File testFile = new File("crianca-test.txt");
            try (PrintWriter writer = new PrintWriter(testFile)) {
                writer.println("10 10");
                writer.println("20");
                writer.println("1");
                writer.println("1 : 20 : 0 : Crianca : 2 : 2");  // Criança no centro
                writer.println("2");  // dois equipamentos
                writer.println("1 : 0 : 2 : 3"); // escudo (defensivo)
                writer.println("2 : 1 : 2 : 1"); // espada (ofensivo)
                writer.println("0");
            }

            gameManager.loadGame(testFile);

            // Testa movimento na diagonal (não deve ser permitido)
            assertFalse(gameManager.move(2, 2, 3, 3),
                    "Criança não deve poder mover na diagonal");

            // Testa mover duas casas (não deve ser permitido)
            assertFalse(gameManager.move(2, 2, 2, 4),
                    "Criança não deve poder mover duas casas");

            // Testa movimento vertical válido
            assertTrue(gameManager.move(2, 2, 2, 3),
                    "Criança deve poder mover uma casa na vertical");

            testFile.delete();
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }


    @Test
    public void testIdosoLimitacoes() {
        try {
            File testFile = new File("idoso-test.txt");
            try (PrintWriter writer = new PrintWriter(testFile)) {
                writer.println("10 10");
                writer.println("20");
                writer.println("1");
                writer.println("1 : 20 : 2 : Idoso : 2 : 2");  // Idoso no centro
                writer.println("0");  // sem equipamentos
                writer.println("0");  // sem safe havens
            }

            gameManager.loadGame(testFile);

            // Não deve permitir movimento horizontal
            assertFalse(gameManager.move(2, 2, 3, 2),
                    "Idoso não deve poder mover horizontalmente");

            // Não deve permitir movimento vertical
            assertFalse(gameManager.move(2, 2, 2, 3),
                    "Idoso não deve poder mover verticalmente");

            // Deve permitir movimento diagonal
            assertTrue(gameManager.move(2, 2, 3, 3),
                    "Idoso deve poder mover na diagonal");

            testFile.delete();
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    @Test
    public void testEscudoEPosicao() {
        try {
            File testFile = new File("escudo-posicao-test.txt");
            try (PrintWriter writer = new PrintWriter(testFile)) {
                writer.println("10 10");
                writer.println("20");
                writer.println("1");
                writer.println("1 : 20 : 1 : Adulto : 2 : 2");  // Adulto
                writer.println("1");
                writer.println("1 : 0 : 2 : 3");  // Escudo adjacente
                writer.println("0");
            }

            gameManager.loadGame(testFile);

            // Verifica posição inicial do escudo
            String infoInicial = gameManager.getEquipmentInfo(1)[0];
            assertNotNull(infoInicial, "Deve retornar info do escudo");

            // Pega o escudo
            assertTrue(gameManager.move(2, 2, 2, 3),
                    "Deve poder pegar o escudo");

            // Verifica se escudo está com o jogador
            assertTrue(gameManager.hasEquipment(1, 0),
                    "Jogador deve ter o escudo");

            testFile.delete();
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    @Test
    public void testCaoMovimentos() {
        try {
            File testFile = new File("cao-test.txt");
            try (PrintWriter writer = new PrintWriter(testFile)) {
                writer.println("10 10");
                writer.println("20");
                writer.println("1");
                writer.println("1 : 20 : 3 : Cao : 2 : 2");  // Cão no centro
                writer.println("0");
                writer.println("0");
            }

            gameManager.loadGame(testFile);

            // Não deve permitir movimento diagonal
            assertFalse(gameManager.move(2, 2, 3, 3),
                    "Cão não deve poder mover na diagonal");

            // Deve permitir movimento vertical de duas casas
            assertTrue(gameManager.move(2, 2, 2, 4),
                    "Cão deve poder mover duas casas na vertical");

            testFile.delete();
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }
    @Test
    public void testGameOverAfterSixInvalidMoves() {
        try {
            File testFile = new File("invalid-moves-test.txt");
            try (PrintWriter writer = new PrintWriter(testFile)) {
                writer.println("10 10");
                writer.println("20");
                writer.println("1");
                writer.println("1 : 20 : 1 : Humano : 1 : 1");
                writer.println("0");
                writer.println("0");
            }

            gameManager.loadGame(testFile);

            // Faz 6 movimentos inválidos para humanos
            for (int i = 0; i < 6; i++) {
                assertFalse(gameManager.move(-1, -1, -1, -1),
                        "Movimento inválido deve retornar false");
            }

            // Verifica se o jogo terminou
            assertTrue(gameManager.gameIsOver(),
                    "Jogo deve terminar após 6 movimentos inválidos");

            testFile.delete();
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }
    @Test
    public void testProcriacaoFalhaZumbi() {
        try {
            File file = createTempFile("""
            5 5
            20
            2
            1 : 20 : 1 : Adulto1 : 2 : 2
            2 : 10 : 1 : Zumbi1 : 2 : 3
            0
            0""");

            gameManager.loadGame(file);
            assertFalse(gameManager.move(2, 2, 2, 3),
                    "Adulto não deve poder procriar com zumbi");
            file.delete();
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    @Test
    public void testProcriacaoFalhaIdoso() {
        try {
            File file = createTempFile("""
            5 5
            20
            2
            1 : 20 : 1 : Adulto1 : 2 : 2
            2 : 20 : 2 : Idoso1 : 2 : 3
            0
            0""");

            gameManager.loadGame(file);
            assertFalse(gameManager.move(2, 2, 2, 3),
                    "Adulto não deve poder procriar com idoso");
            file.delete();
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    @Test
    public void testProcriacaoFalhaCrianca() {
        try {
            File file = createTempFile("""
            5 5
            20
            2
            1 : 20 : 1 : Adulto1 : 2 : 2
            2 : 20 : 0 : Crianca1 : 2 : 3
            0
            0""");

            gameManager.loadGame(file);
            assertFalse(gameManager.move(2, 2, 2, 3),
                    "Adulto não deve poder procriar com criança");
            file.delete();
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    @Test
    public void testProcriacaoComEquipamento() {
        try {
            // Coloca o equipamento em (2,1) - uma posição adjacente ao primeiro adulto
            File file = createTempFile("""
            5 5
            20
            2
            1 : 20 : 1 : Adulto1 : 2 : 2
            2 : 20 : 1 : Adulto2 : 2 : 3
            1
            1 : 0 : 2 : 1
            0""");

            gameManager.loadGame(file);

            // Move para posição do equipamento
            assertTrue(gameManager.move(2, 2, 2, 1),
                    "Adulto deve poder mover para pegar equipamento");

            // Verifica se pegou o equipamento
            assertTrue(gameManager.hasEquipment(1, 0),
                    "Adulto deve ter pegado o equipamento");

            // Avança turno dos zumbis
            assertFalse(gameManager.move(0, 0, 0, 0));

            // Tenta procriar
            assertFalse(gameManager.move(2, 1, 2, 3),
                    "Não deve poder procriar com equipamento");

            file.delete();
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    @Test
    public void testProcriacaoSemEspaco() {
        try {
            // Cria tabuleiro onde todas as posições adjacentes estão ocupadas
            File file = createTempFile("""
            5 5
            20
            6
            1 : 20 : 1 : Adulto1 : 2 : 2
            2 : 20 : 1 : Adulto2 : 2 : 3
            3 : 20 : 1 : Block1 : 1 : 2
            4 : 20 : 1 : Block2 : 3 : 2
            5 : 20 : 1 : Block3 : 2 : 1
            6 : 20 : 1 : Block4 : 2 : 4
            0
            0""");

            gameManager.loadGame(file);
            assertFalse(gameManager.move(2, 2, 2, 3),
                    "Não deve poder procriar sem espaço livre adjacente");
            file.delete();
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    @Test
    public void testProcriacaoBemSucedida() {
        try {
            File file = createTempFile("""
            5 5
            20
            2
            1 : 20 : 1 : Maria : 2 : 2
            2 : 20 : 1 : Joao : 2 : 3
            0
            0""");

            gameManager.loadGame(file);
            assertTrue(gameManager.move(2, 2, 2, 3),
                    "Procriação entre adultos deve ser permitida");

            boolean encontrouCrianca = false;
            int[][] posicoesAdjacentes = {{1,2}, {3,2}, {2,1}, {2,4}};

            for (int[] pos : posicoesAdjacentes) {
                String info = gameManager.getSquareInfo(pos[0], pos[1]);
                if (info != null && info.startsWith("H:")) {
                    int id = Integer.parseInt(info.split(":")[1]);
                    String[] crianca = gameManager.getCreatureInfo(id);

                    if (crianca != null && crianca[1].equals("Criança")) {
                        encontrouCrianca = true;
                        assertEquals("Maria & Joao", crianca[3],
                                "Nome da criança deve ser concatenação dos nomes dos pais");
                        assertEquals("12", crianca[0],
                                "ID da criança deve ser concatenação dos IDs dos pais");
                        break;
                    }
                }
            }
            assertTrue(encontrouCrianca, "Uma criança deve ser criada em posição adjacente");
            file.delete();
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    @Test
    public void testPosicoesCrianca() {
        try {
            // Testa cada posição possível para a criança sendo bloqueada
            int[][] bloqueios = {
                    {1,2},  // Bloqueia oeste
                    {3,2},  // Bloqueia leste
                    {2,1},  // Bloqueia norte
                    {2,4}   // Bloqueia sul
            };

            for (int[] bloqueio : bloqueios) {
                File file = createTempFile(String.format("""
                5 5
                20
                3
                1 : 20 : 1 : Adulto1 : 2 : 2
                2 : 20 : 1 : Adulto2 : 2 : 3
                3 : 20 : 1 : Block : %d : %d
                0
                0""", bloqueio[0], bloqueio[1]));

                gameManager.loadGame(file);
                assertTrue(gameManager.move(2, 2, 2, 3),
                        "Procriação deve ser possível com apenas uma posição bloqueada");
                file.delete();
            }
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    private File createTempFile(String content) throws IOException {
        File tempFile = File.createTempFile("test", ".txt");
        try (PrintWriter writer = new PrintWriter(tempFile)) {
            writer.print(content);
        }
        return tempFile;
    }
}


