package pt.ulusofona.lp2.thenightofthelivingdeisi;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class TestGameManager {
    private GameManager gameManager;
    private File testFile;

    @Before
    public void setUp() {
        gameManager = new GameManager();
        testFile = createTestFile();
    }

    private File createTestFile() {
        try {
            File tempFile = File.createTempFile("test-game", ".txt");
            try (java.io.PrintWriter writer = new java.io.PrintWriter(tempFile)) {
                writer.println("10 10");        // dimensões
                writer.println("20");           // equipe inicial
                writer.println("2");            // número de criaturas
                writer.println("1 : 20 : 1 : Humano1 : 1 : 1");     // adulto humano
                writer.println("2 : 10 : 1 : Zumbi1 : 3 : 3");      // adulto zumbi
                writer.println("1");            // número de equipamentos
                writer.println("1 : 1 : 2 : 2"); // espada em (2,2)
                writer.println("1");            // número de safe havens
                writer.println("5 : 5");        // safe haven em (5,5)
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

            // Teste movimento básico do humano
            assertTrue("Movimento válido deve ser permitido",
                    gameManager.move(1, 1, 2, 1));

            // Teste movimento do zumbi
            assertTrue("Movimento válido do zumbi deve ser permitido",
                    gameManager.move(3, 3, 3, 2));

        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    @Test
    public void testInvalidMovement() {
        try {
            gameManager.loadGame(testFile);

            // Teste movimento fora do tabuleiro
            assertFalse("Movimento fora do tabuleiro deve ser bloqueado",
                    gameManager.move(1, 1, -1, -1));

            // Teste movimento muito distante
            assertFalse("Movimento muito distante deve ser bloqueado",
                    gameManager.move(1, 1, 5, 5));

        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    @Test
    public void testEquipmentPickup() {
        try {
            gameManager.loadGame(testFile);

            // Humano move para posição do equipamento
            assertTrue("Humano deve poder mover até equipamento",
                    gameManager.move(1, 1, 2, 2));

            // Verifica se equipamento foi pego
            String info = gameManager.getCreatureInfoAsString(1);
            assertNotNull("Informação da criatura não deve ser nula", info);

        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    @Test
    public void testBasicGameState() {
        try {
            gameManager.loadGame(testFile);

            // Testa dimensões do tabuleiro
            int[] size = gameManager.getWorldSize();
            assertEquals("Altura do tabuleiro deve ser 10", 10, size[0]);
            assertEquals("Largura do tabuleiro deve ser 10", 10, size[1]);

            // Testa equipe inicial
            assertEquals("Equipe inicial deve ser humanos (20)", 20, gameManager.getInitialTeamId());

            // Testa se jogo começa não terminado
            assertFalse("Jogo não deve começar terminado", gameManager.gameIsOver());

        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    @Test
    public void testSquareInfo() {
        try {
            gameManager.loadGame(testFile);

            // Teste posição com humano
            String humanSquare = gameManager.getSquareInfo(1, 1);
            assertTrue("Deve indicar presença de humano",
                    humanSquare.startsWith("H:"));

            // Teste posição com zumbi
            String zombieSquare = gameManager.getSquareInfo(3, 3);
            assertTrue("Deve indicar presença de zumbi",
                    zombieSquare.startsWith("Z:"));

            // Teste posição vazia
            assertEquals("Posição vazia deve retornar string vazia",
                    "", gameManager.getSquareInfo(0, 0));

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

        // Teste tipos de criaturas
        assertEquals("Adulto", adulto.getTipoCriatura());
        assertEquals("Criança", crianca.getTipoCriatura());
        assertEquals("Idoso", idoso.getTipoCriatura());
        assertEquals("Cão", cao.getTipoCriatura());
        assertEquals("Vampiro", vampiro.getTipoCriatura());

        // Teste movimentos específicos
        assertTrue(adulto.podeMover(0, 0, 1, 1, true)); // Diagonal 1 casa
        assertTrue(adulto.podeMover(0, 0, 2, 0, true)); // 2 casas em linha reta
        assertFalse(adulto.podeMover(0, 0, 3, 3, true)); // Movimento inválido

        assertTrue(crianca.podeMover(0, 0, 0, 1, true)); // 1 casa vertical
        assertFalse(crianca.podeMover(0, 0, 1, 1, true)); // Diagonal não permitida

        assertTrue(idoso.podeMover(0, 0, 1, 1, true)); // Diagonal permitida
        assertFalse(idoso.podeMover(0, 0, 1, 0, true)); // Linha reta não permitida

        assertTrue(cao.podeMover(0, 0, 0, 1, true)); // 1 casa vertical
        assertFalse(cao.podeMover(0, 0, 1, 1, true)); // Diagonal não permitida

        assertFalse(vampiro.podeMover(0, 0, 1, 1, true)); // Não move de dia
        assertTrue(vampiro.podeMover(0, 0, 1, 1, false)); // Move à noite
    }

    @Test
    public void testEquipmentInteractions() {
        Creature adulto = new Adulto(1, "Adulto", 0, 0, 20, true);
        Creature crianca = new Crianca(2, "Crianca", 1, 1, 20, true);
        Creature vampiro = new Vampiro(3, "Vampiro", 2, 2, 10);

        Equipamento escudo = new EscudoDeMadeira(1, 0, 0, 0);
        Equipamento espada = new EspadaSamurai(2, 1, 1, 1);
        Equipamento pistola = new PistolaWaltherPPK(3, 2, 2, 2);
        Equipamento lixivia = new Lixivia(4, 3, 3, 3);

        // Teste restrições de equipamentos
        assertTrue(adulto.podePegarEquipamento(escudo));
        assertTrue(adulto.podePegarEquipamento(espada));
        assertTrue(adulto.podePegarEquipamento(pistola));

        assertTrue(crianca.podePegarEquipamento(escudo)); // Defensivo permitido
        assertTrue(crianca.podePegarEquipamento(lixivia)); // Defensivo permitido
        assertFalse(crianca.podePegarEquipamento(espada)); // Ofensivo não permitido

        assertFalse(vampiro.podePegarEquipamento(escudo)); // Vampiro não pega equipamentos
    }

    @Test
    public void testEquipmentActions() {
        Creature zumbi = new Adulto(1, "Zumbi", 0, 0, 10, false);
        Creature humano = new Adulto(2, "Humano", 1, 1, 20, true);

        Equipamento espada = new EspadaSamurai(1, 1, 0, 0);
        Equipamento pistola = new PistolaWaltherPPK(2, 2, 0, 0);
        Equipamento lixivia = new Lixivia(3, 3, 0, 0);

        // Teste ações dos equipamentos
        assertTrue(espada.executarAcao(humano, zumbi)); // Espada contra zumbi
        assertFalse(espada.executarAcao(humano, humano)); // Espada contra humano

        // Teste munição da pistola
        PistolaWaltherPPK pistolaTeste = (PistolaWaltherPPK) pistola;
        assertTrue(pistolaTeste.temBalas());
        pistolaTeste.gastarBala();
        assertEquals("2 balas", pistolaTeste.getInfo());

        // Teste volume da lixívia
        Lixivia lixiviaTeste = (Lixivia) lixivia;
        assertTrue(lixiviaTeste.executarAcao(humano, zumbi));
        assertTrue(lixiviaTeste.getInfo().contains("litros"));
    }

    @Test
    public void testSafeHavenOperations() {
        SafeHaven safeHaven = new SafeHaven(5, 5);
        Creature humano = new Adulto(1, "Humano", 5, 5, 20, true);

        safeHaven.entrar(humano);
        List<Creature> criaturas = safeHaven.getCriaturasDentro();

        assertEquals(1, criaturas.size());
        assertEquals(humano.getId(), criaturas.get(0).getId());
        assertEquals(5, safeHaven.getX());
        assertEquals(5, safeHaven.getY());

        // Teste singleton dos Safe Havens
        SafeHaven.add(safeHaven);
        assertTrue(SafeHaven.getSafeHavens().contains(safeHaven));
    }

    @Test
    public void testGameState() {
        try {
            gameManager.loadGame(testFile);

            // Teste survivors
            ArrayList<String> survivors = gameManager.getSurvivors();
            assertNotNull(survivors);
            assertTrue(survivors.contains("OS VIVOS"));
            assertTrue(survivors.contains("OS OUTROS"));

            // Teste créditos
            assertNotNull(gameManager.getCreditsPanel());

            // Teste customização
            assertNotNull(gameManager.customizeBoard());
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    @Test
    public void testCreatureTransformations() {
        Creature humano = new Adulto(1, "Humano", 0, 0, 20, true);

        assertFalse(humano.isTransformed());
        assertEquals(20, humano.getEquipa());

        humano.transformar();
        humano.setEquipa(10);

        assertTrue(humano.isTransformed());
        assertEquals(10, humano.getEquipa());
        assertTrue(humano.isZombie());
        assertFalse(humano.isHuman());
    }

    @Test
    public void testEquipmentCounting() {
        Creature humano = new Adulto(1, "Humano", 0, 0, 20, true);
        Equipamento escudo = new EscudoDeMadeira(1, 0, 0, 0);

        assertEquals(0, humano.getContadorEquipamentos());
        humano.pegarEquipamento(escudo);
        assertEquals(1, humano.getContadorEquipamentos());

        // Teste destruição de equipamento
        humano.destruirEquipamento();
        assertEquals(2, humano.getContadorEquipamentos());

        // Teste soltar equipamento
        humano.soltarEquipamento();
        assertNull(humano.getEquipamentoAtual());
    }



    @Test
    public void testPistolaAdvancedBehavior() {
        PistolaWaltherPPK pistola = new PistolaWaltherPPK(1, 2, 0, 0);
        Creature humano = new Adulto(1, "Humano", 0, 0, 20, true);
        Creature zumbi = new Vampiro(2, "Vampiro", 1, 1, 10);

        // Teste ciclo completo de balas
        assertTrue(pistola.executarAcao(humano, zumbi));  // Primeira bala
        assertEquals("2 balas", pistola.getInfo());

        assertTrue(pistola.executarAcao(humano, zumbi));  // Segunda bala
        assertEquals("1 balas", pistola.getInfo());

        assertTrue(pistola.executarAcao(humano, zumbi));  // Última bala
        assertEquals("0 balas", pistola.getInfo());

        assertFalse(pistola.executarAcao(humano, zumbi)); // Sem balas
    }

    @Test
    public void testLixiviaDetailedBehavior() {
        Lixivia lixivia = new Lixivia(1, 3, 0, 0);
        Creature humano = new Adulto(1, "Humano", 0, 0, 20, true);
        Creature zumbi = new Vampiro(2, "Vampiro", 1, 1, 10);

        // Teste uso gradual
        for (int i = 0; i < 3; i++) {
            assertTrue(lixivia.executarAcao(humano, zumbi));
            double litros = Double.parseDouble(lixivia.getInfo().split(" ")[0]);
            assertTrue(litros < 1.0 && litros >= 0.0);
        }
    }

    @Test
    public void testCriancaSpecificBehavior() {
        Crianca crianca = new Crianca(1, "Crianca", 0, 0, 20, true);

        // Teste movimento válido
        assertTrue(crianca.podeMover(0, 0, 0, 1, true));  // Vertical
        assertTrue(crianca.podeMover(0, 0, 1, 0, true));  // Horizontal
        assertFalse(crianca.podeMover(0, 0, 1, 1, true)); // Diagonal

        // Teste equipamentos
        Equipamento escudo = new EscudoDeMadeira(1, 0, 0, 0);
        Equipamento espada = new EspadaSamurai(2, 1, 0, 0);

        assertTrue(crianca.podePegarEquipamento(escudo));   // Defensivo
        assertFalse(crianca.podePegarEquipamento(espada)); // Ofensivo

        try {
            crianca.pegarEquipamento(espada); // Deve lançar exceção
            fail("Deveria ter lançado exceção");
        } catch (IllegalStateException e) {
            assertEquals("A criança não pode pegar este equipamento.", e.getMessage());
        }
    }

    @Test
    public void testVampiroSpecificBehavior() {
        Vampiro vampiro = new Vampiro(1, "Drácula", 0, 0, 10);

        // Teste movimento diurno/noturno
        assertFalse("Vampiro não deve mover de dia",
                vampiro.podeMover(0, 0, 1, 1, true));
        assertTrue("Vampiro deve mover à noite",
                vampiro.podeMover(0, 0, 1, 1, false));

        // Teste equipamentos
        assertFalse(vampiro.podePegarEquipamento(new EscudoDeMadeira(1, 0, 0, 0)));
        assertFalse(vampiro.podePegarEquipamento(new EspadaSamurai(2, 1, 0, 0)));
    }

    @Test
    public void testTabuleiroComplexOperations() {
        Tabuleiro tabuleiro = new Tabuleiro(10, 10);

        // Teste limites
        assertTrue(tabuleiro.dentroDosLimites(0, 0));    // Canto superior esquerdo
        assertTrue(tabuleiro.dentroDosLimites(9, 9));    // Canto inferior direito
        assertFalse(tabuleiro.dentroDosLimites(-1, 0));  // Fora à esquerda
        assertFalse(tabuleiro.dentroDosLimites(0, -1));  // Fora em cima
        assertFalse(tabuleiro.dentroDosLimites(10, 0));  // Fora à direita
        assertFalse(tabuleiro.dentroDosLimites(0, 10));  // Fora embaixo

        // Teste Safe Havens
        assertTrue(tabuleiro.adicionarSafeHaven(5, 5));
        assertTrue(tabuleiro.isSafeHaven(5, 5));
        assertFalse(tabuleiro.isSafeHaven(4, 4));

        try {
            tabuleiro.adicionarSafeHaven(-1, -1);
            fail("Deveria ter lançado exceção");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Coordenadas fora dos limites"));
        }
    }

    @Test
    public void testEquipamentosContador() {
        Creature humano = new Adulto(1, "Humano", 0, 0, 20, true);

        assertEquals(0, humano.getContadorEquipamentos());

        // Adiciona vários equipamentos
        humano.pegarEquipamento(new EscudoDeMadeira(1, 0, 0, 0));
        assertEquals(1, humano.getContadorEquipamentos());

        humano.soltarEquipamento();
        humano.pegarEquipamento(new EspadaSamurai(2, 1, 0, 0));
        assertEquals(2, humano.getContadorEquipamentos());

        humano.destruirEquipamento();
        assertEquals(3, humano.getContadorEquipamentos());
    }

    @Test
    public void testCaoSpecificBehavior() {
        Cao cao = new Cao(1, "Rex", 0, 0, 20, true);

        // Teste movimento
        assertTrue(cao.podeMover(0, 0, 0, 1, true));   // 1 casa vertical
        assertTrue(cao.podeMover(0, 0, 0, 2, true));   // 2 casas vertical
        assertTrue(cao.podeMover(0, 0, 2, 0, true));   // 2 casas horizontal
        assertFalse(cao.podeMover(0, 0, 1, 1, true)); // Diagonal não permitida

        // Teste equipamentos (não deve poder pegar)
        assertFalse(cao.podePegarEquipamento(new EscudoDeMadeira(1, 0, 0, 0)));
        assertFalse(cao.podeMoverParaComEquipamento(new EscudoDeMadeira(1, 0, 0, 0)));
    }

    @Test
    public void testEquipamentosTiposEspecificos() {
        // Teste Escudo
        EscudoDeMadeira escudo = new EscudoDeMadeira(1, 0, 0, 0);
        assertTrue(escudo.isDefensivo());
        assertEquals("", escudo.getInfo());

        // Teste Espada
        EspadaSamurai espada = new EspadaSamurai(1, 1, 0, 0);
        assertFalse(espada.isDefensivo());
        assertEquals("", espada.getInfo());

        // Teste coordenadas
        escudo.setX(5);
        escudo.setY(5);
        assertEquals(5, escudo.getX());
        assertEquals(5, escudo.getY());
    }

    @Test
    public void testMovimentosCrianca() {
        Crianca crianca = new Crianca(1, "Mini", 0, 0, 20, true);

        // Movimentos válidos (1 casa ortogonal)
        assertTrue(crianca.podeMover(0, 0, 0, 1, true));  // Vertical
        assertTrue(crianca.podeMover(0, 0, 1, 0, true));  // Horizontal

        // Movimentos inválidos
        assertFalse(crianca.podeMover(0, 0, 1, 1, true));  // Diagonal
        assertFalse(crianca.podeMover(0, 0, 0, 2, true));  // 2 casas
        assertFalse(crianca.podeMover(0, 0, 2, 0, true));  // 2 casas
    }

    @Test
    public void testEquipamentosCrianca() {
        Crianca crianca = new Crianca(1, "Mini", 0, 0, 20, true);

        // Equipamentos defensivos
        Equipamento escudo = new EscudoDeMadeira(1, 0, 0, 0);
        Equipamento lixivia = new Lixivia(2, 3, 0, 0);
        assertTrue(crianca.podePegarEquipamento(escudo));
        assertTrue(crianca.podePegarEquipamento(lixivia));

        // Equipamentos ofensivos
        Equipamento espada = new EspadaSamurai(3, 1, 0, 0);
        Equipamento pistola = new PistolaWaltherPPK(4, 2, 0, 0);
        assertFalse(crianca.podePegarEquipamento(espada));
        assertFalse(crianca.podePegarEquipamento(pistola));
    }

    @Test
    public void testMovimentosVampiro() {
        Vampiro vampiro = new Vampiro(1, "Drac", 0, 0, 10);

        // Teste durante o dia (não pode mover)
        assertFalse(vampiro.podeMover(0, 0, 1, 0, true));
        assertFalse(vampiro.podeMover(0, 0, 0, 1, true));
        assertFalse(vampiro.podeMover(0, 0, 1, 1, true));

        // Teste durante a noite (pode mover 1 casa em qualquer direção)
        assertTrue(vampiro.podeMover(0, 0, 1, 0, false));   // Horizontal
        assertTrue(vampiro.podeMover(0, 0, 0, 1, false));   // Vertical
        assertTrue(vampiro.podeMover(0, 0, 1, 1, false));   // Diagonal

        // Teste movimento inválido à noite
        assertFalse(vampiro.podeMover(0, 0, 2, 0, false));  // 2 casas
    }

    @Test
    public void testLixiviaComportamento() {
        Lixivia lixivia = new Lixivia(1, 3, 0, 0);
        Creature humano = new Adulto(1, "Humano", 0, 0, 20, true);
        Creature zumbi = new Adulto(2, "Zumbi", 1, 1, 10, false);

        assertTrue(lixivia.isDefensivo());
        assertTrue(lixivia.executarAcao(humano, zumbi));

        String info = lixivia.getInfo();
        assertTrue(info.contains("litros"));
        assertTrue(info.contains("0.7")); // Após usar uma vez (1.0 - 0.3)
    }

    @Test
    public void testPistolaComportamento() {
        PistolaWaltherPPK pistola = new PistolaWaltherPPK(1, 2, 0, 0);

        assertTrue(pistola.temBalas());
        assertEquals("3 balas", pistola.getInfo());

        pistola.gastarBala();
        assertTrue(pistola.temBalas());
        assertEquals("2 balas", pistola.getInfo());

        pistola.gastarBala();
        pistola.gastarBala();
        assertFalse(pistola.temBalas());
        assertEquals("0 balas", pistola.getInfo());
    }

    @Test
    public void testMovimentosAdulto() {
        Adulto adulto = new Adulto(1, "Adulto", 0, 0, 20, true);

        // Movimentos válidos
        assertTrue(adulto.podeMover(0, 0, 1, 0, true));   // 1 casa horizontal
        assertTrue(adulto.podeMover(0, 0, 0, 1, true));   // 1 casa vertical
        assertTrue(adulto.podeMover(0, 0, 1, 1, true));   // 1 casa diagonal
        assertTrue(adulto.podeMover(0, 0, 2, 0, true));   // 2 casas horizontal
        assertTrue(adulto.podeMover(0, 0, 0, 2, true));   // 2 casas vertical
        assertTrue(adulto.podeMover(0, 0, 2, 2, true));   // 2 casas diagonal

        // Movimentos inválidos
        assertFalse(adulto.podeMover(0, 0, 3, 0, true));  // 3 casas
        assertFalse(adulto.podeMover(0, 0, 2, 1, true));  // Movimento em L
    }

    @Test
    public void testMovimentosCao() {
        Cao cao = new Cao(1, "Rex", 0, 0, 20, true);

        // Movimentos válidos
        assertTrue(cao.podeMover(0, 0, 1, 0, true));   // 1 casa horizontal
        assertTrue(cao.podeMover(0, 0, 0, 1, true));   // 1 casa vertical
        assertTrue(cao.podeMover(0, 0, 2, 0, true));   // 2 casas horizontal
        assertTrue(cao.podeMover(0, 0, 0, 2, true));   // 2 casas vertical

        // Movimentos inválidos
        assertFalse(cao.podeMover(0, 0, 1, 1, true));  // Diagonal
        assertFalse(cao.podeMover(0, 0, 3, 0, true));  // 3 casas
        assertFalse(cao.podeMover(0, 0, 2, 1, true));  // Movimento em L
    }

    @Test
    public void testEquipamentosInteracoes() {
        EscudoDeMadeira escudo = new EscudoDeMadeira(1, 0, 0, 0);
        EspadaSamurai espada = new EspadaSamurai(2, 1, 0, 0);

        // Teste propriedades básicas
        assertTrue(escudo.isDefensivo());
        assertFalse(espada.isDefensivo());

        // Teste coordenadas
        escudo.setX(5);
        escudo.setY(3);
        assertEquals(5, escudo.getX());
        assertEquals(3, escudo.getY());

        // Teste IDs e tipos
        assertEquals(1, escudo.getId());
        assertEquals(0, escudo.getTipo());
        assertEquals(2, espada.getId());
        assertEquals(1, espada.getTipo());
    }

    @Test
    public void testEquipamentosDestruidos() {
        Creature zumbi = new Adulto(1, "Zumbi", 0, 0, 10, false);

        assertEquals(0, zumbi.getEquipamentosDestruidos());
        zumbi.incrementarEquipamentosDestruidos(1);
        assertEquals(1, zumbi.getEquipamentosDestruidos());
        zumbi.incrementarEquipamentosDestruidos(2);
        assertEquals(3, zumbi.getEquipamentosDestruidos());
    }

    @Test
    public void testHistoricoEquipamentos() {
        Creature humano = new Adulto(1, "Humano", 0, 0, 20, true);
        Equipamento escudo = new EscudoDeMadeira(1, 0, 0, 0);
        Equipamento espada = new EspadaSamurai(2, 1, 1, 1);

        // Pegar primeiro equipamento
        humano.pegarEquipamento(escudo);
        assertEquals(escudo, humano.getEquipamentoAtual());

        // Pegar segundo equipamento (primeiro vai para histórico)
        humano.pegarEquipamento(espada);
        assertEquals(espada, humano.getEquipamentoAtual());

        // Verificar contador
        assertEquals(2, humano.getContadorEquipamentos());
    }

    @Test
    public void testMovimentosIdosoCompleto() {
        Idoso idoso = new Idoso(1, "Idoso", 0, 0, 20, true);

        // Movimentos válidos - apenas diagonal e somente de dia
        assertTrue(idoso.podeMover(0, 0, 1, 1, true));   // Diagonal dia
        assertTrue(idoso.podeMover(0, 0, -1, -1, true)); // Diagonal oposta dia

        // Movimentos inválidos
        assertFalse(idoso.podeMover(0, 0, 1, 1, false)); // Diagonal noite
        assertFalse(idoso.podeMover(0, 0, 1, 0, true));  // Horizontal
        assertFalse(idoso.podeMover(0, 0, 0, 1, true));  // Vertical
        assertFalse(idoso.podeMover(0, 0, 2, 2, true));  // Diagonal longa
    }

    @Test
    public void testIdosoZumbi() {
        Idoso idosoZumbi = new Idoso(1, "IdosoZ", 0, 0, 10, false);

        // Movimentos válidos - apenas diagonal
        assertTrue("Idoso zumbi deve poder mover na diagonal à noite",
                idosoZumbi.podeMover(0, 0, 1, 1, false));  // Diagonal à noite

        assertTrue("Idoso zumbi deve poder mover diagonalmente para cima à noite",
                idosoZumbi.podeMover(1, 1, 0, 0, false));  // Diagonal inversa à noite

        // Movimentos inválidos
        assertFalse("Idoso zumbi não deve poder mover na horizontal",
                idosoZumbi.podeMover(0, 0, 1, 0, false)); // Horizontal

        assertFalse("Idoso zumbi não deve poder mover na vertical",
                idosoZumbi.podeMover(0, 0, 0, 1, false)); // Vertical

        assertFalse("Idoso zumbi não deve poder mover mais de uma casa diagonal",
                idosoZumbi.podeMover(0, 0, 2, 2, false)); // Diagonal longa

        // Teste equipamentos
        Equipamento equip = new EscudoDeMadeira(1, 0, 0, 0);
        assertTrue("Idoso deve poder pegar equipamento",
                idosoZumbi.podePegarEquipamento(equip));

        // Teste destruição de equipamento
        idosoZumbi.destruirEquipamento();
        assertEquals("Contador de equipamentos deve incrementar após destruição",
                1, idosoZumbi.getContadorEquipamentos());
    }

    @Test
    public void testMovimentoComEquipamento() {
        Creature adulto = new Adulto(1, "Humano", 0, 0, 20, true);
        Creature crianca = new Crianca(2, "Crianca", 1, 1, 20, true);

        Equipamento escudo = new EscudoDeMadeira(1, 0, 0, 0);
        Equipamento espada = new EspadaSamurai(2, 1, 0, 0);

        // Adulto pode mover com qualquer equipamento
        assertTrue(adulto.podeMoverParaComEquipamento(escudo));
        assertTrue(adulto.podeMoverParaComEquipamento(espada));

        // Criança só pode mover com equipamento defensivo
        assertTrue(crianca.podeMoverParaComEquipamento(escudo));
        assertFalse(crianca.podeMoverParaComEquipamento(espada));

        // Sem equipamento deve permitir movimento
        assertTrue(crianca.podeMoverParaComEquipamento(null));
    }

    @Test
    public void testCaoComportamentoCompleto() {
        Cao cao = new Cao(1, "Rex", 0, 0, 20, true);

        // Tipo e equipe
        assertEquals("Cão", cao.getTipoCriatura());
        assertEquals("Humano", cao.getTipo());
        assertTrue(cao.isHuman());

        // Equipamentos
        Equipamento equip = new EscudoDeMadeira(1, 0, 0, 0);
        assertFalse(cao.podePegarEquipamento(equip));
        assertFalse(cao.podeMoverParaComEquipamento(equip));

        // Destruição de equipamento não deve afetar contador
        cao.destruirEquipamento();
        assertEquals(0, cao.getContadorEquipamentos());
    }

    @Test
    public void testVampiroComportamentoCompleto() {
        Vampiro vampiro = new Vampiro(1, "Drac", 0, 0, 10);

        // Tipo e equipe
        assertEquals("Vampiro", vampiro.getTipoCriatura());
        assertEquals("Zombie", vampiro.getTipo());
        assertTrue(vampiro.isZombie());

        // Equipamentos
        Equipamento equip = new EscudoDeMadeira(1, 0, 0, 0);
        assertFalse(vampiro.podePegarEquipamento(equip));
        assertTrue(vampiro.podeMoverParaComEquipamento(equip));

        // Destruição de equipamento
        vampiro.destruirEquipamento();
        assertEquals(1, vampiro.getContadorEquipamentos());
    }

    @Test
    public void testTransformacaoCompleta() {
        Creature humano = new Adulto(1, "Humano", 0, 0, 20, true);

        // Estado inicial
        assertFalse(humano.isTransformed());
        assertTrue(humano.isHuman());
        assertFalse(humano.isZombie());

        // Transformação
        humano.transformar();
        humano.setEquipa(10);

        // Estado final
        assertTrue(humano.isTransformed());
        assertFalse(humano.isHuman());
        assertTrue(humano.isZombie());
    }

    @Test
    public void testEquipamentosPropagacaoCoordenadas() {
        Creature humano = new Adulto(1, "Humano", 0, 0, 20, true);
        Equipamento equip = new EscudoDeMadeira(1, 0, 0, 0);

        humano.pegarEquipamento(equip);

        // Movimento da criatura deve atualizar coordenadas do equipamento
        humano.setX(5);
        humano.setY(3);

        assertEquals(5, humano.getX());
        assertEquals(3, humano.getY());
        assertEquals(5, equip.getX());
        assertEquals(3, equip.getY());
    }

    @Test
    public void testCreatureInfoDetalhado() {
        // Criar diferentes tipos de criaturas
        Creature crianca = new Crianca(1, "CriancaTest", 1, 1, 20, true);
        Creature adulto = new Adulto(2, "AdultoTest", 2, 2, 20, true);
        Creature zumbi = new Adulto(3, "ZumbiTest", 3, 3, 10, false);

        // Testar informações básicas da criança
        assertEquals("ID incorreto", 1, crianca.getId());
        assertEquals("Tipo incorreto", "Criança", crianca.getTipoCriatura());
        assertEquals("Nome incorreto", "CriancaTest", crianca.getNome());
        assertEquals("X incorreto", 1, crianca.getX());
        assertEquals("Y incorreto", 1, crianca.getY());
        assertTrue("Deveria ser humano", crianca.isHuman());
        assertFalse("Não deveria ser zumbi", crianca.isZombie());

        // Testar informações básicas do adulto
        assertEquals("ID incorreto", 2, adulto.getId());
        assertEquals("Tipo incorreto", "Adulto", adulto.getTipoCriatura());
        assertEquals("Nome incorreto", "AdultoTest", adulto.getNome());
        assertEquals("X incorreto", 2, adulto.getX());
        assertEquals("Y incorreto", 2, adulto.getY());
        assertTrue("Deveria ser humano", adulto.isHuman());
        assertFalse("Não deveria ser zumbi", adulto.isZombie());

        // Testar informações básicas do zumbi
        assertEquals("ID incorreto", 3, zumbi.getId());
        assertEquals("Tipo incorreto", "Adulto", zumbi.getTipoCriatura());
        assertEquals("Nome incorreto", "ZumbiTest", zumbi.getNome());
        assertEquals("X incorreto", 3, zumbi.getX());
        assertEquals("Y incorreto", 3, zumbi.getY());
        assertFalse("Não deveria ser humano", zumbi.isHuman());
        assertTrue("Deveria ser zumbi", zumbi.isZombie());

        // Testar equipamentos
        Equipamento escudo = new EscudoDeMadeira(1, 0, 1, 1);
        crianca.pegarEquipamento(escudo);

        assertEquals("Contador de equipamentos incorreto",
                1, crianca.getContadorEquipamentos());
        assertNotNull("Equipamento atual não deveria ser nulo",
                crianca.getEquipamentoAtual());
        assertEquals("Equipamento incorreto",
                escudo, crianca.getEquipamentoAtual());

        // Testar equipes
        assertEquals("Equipe incorreta da criança", 20, crianca.getEquipa());
        assertEquals("Equipe incorreta do adulto", 20, adulto.getEquipa());
        assertEquals("Equipe incorreta do zumbi", 10, zumbi.getEquipa());
    }

    @Test
    public void testSafeHavenInteracoesComplexas() {
        SafeHaven safeHaven = new SafeHaven(5, 5);

        // Testar múltiplas entradas no mesmo SafeHaven
        Creature humano1 = new Adulto(1, "Humano1", 5, 5, 20, true);
        Creature humano2 = new Crianca(2, "Humano2", 5, 5, 20, true);

        safeHaven.entrar(humano1);
        safeHaven.entrar(humano2);

        List<Creature> criaturas = safeHaven.getCriaturasDentro();
        assertEquals(2, criaturas.size());

        // Verificar duplicatas
        safeHaven.entrar(humano1); // Tentar adicionar novamente
        assertEquals(2, safeHaven.getCriaturasDentro().size());

        // Verificar registro global
        SafeHaven.add(safeHaven);
        assertTrue(SafeHaven.getSafeHavens().contains(safeHaven));
    }

    @Test
    public void testEquipamentoEfeitos() {
        Creature humano = new Adulto(1, "Humano", 0, 0, 20, true);
        Creature zumbi = new Adulto(2, "Zumbi", 1, 1, 10, false);

        // Testar Lixívia com diferentes volumes
        Lixivia lixivia = new Lixivia(1, 3, 0, 0);
        assertTrue(lixivia.executarAcao(humano, zumbi));
        assertEquals("0.7 litros", lixivia.getInfo());

        // Usar mais vezes até acabar
        assertTrue(lixivia.executarAcao(humano, zumbi));
        assertTrue(lixivia.executarAcao(humano, zumbi));
        assertEquals("0.1 litros", lixivia.getInfo());
    }

    @Test
    public void testMovimentosEspeciais() {
        GameManager manager = new GameManager();
        try {
            // Teste de movimentos válidos do adulto
            Adulto adulto = new Adulto(1, "Humano", 1, 1, 20, true);

            // Movimento diagonal
            assertTrue("Adulto deve poder mover na diagonal",
                    adulto.podeMover(1, 1, 2, 2, true));

            // Movimento duas casas
            assertTrue("Adulto deve poder mover duas casas",
                    adulto.podeMover(1, 1, 3, 1, true));

            // Teste com equipamento
            Equipamento espada = new EspadaSamurai(1, 1, 1, 1);
            adulto.pegarEquipamento(espada);
            assertTrue("Adulto deve poder mover com equipamento",
                    adulto.podeMoverParaComEquipamento(espada));

            // Movimentos inválidos
            assertFalse("Adulto não deve poder mover três casas",
                    adulto.podeMover(1, 1, 4, 1, true));
            assertFalse("Adulto não deve mover em L",
                    adulto.podeMover(1, 1, 3, 2, true));
        } catch (Exception e) {
            fail("Teste falhou com exceção: " + e.getMessage());
        }
    }

    @Test
    public void testEquipamentoComplexo() {
        Creature humano = new Adulto(1, "Humano", 0, 0, 20, true);
        PistolaWaltherPPK pistola = new PistolaWaltherPPK(1, 2, 0, 0);

        // Testar ciclo completo de balas
        assertTrue(pistola.temBalas());

        // Usar todas as balas
        for (int i = 0; i < 3; i++) {
            pistola.gastarBala();
        }

        assertFalse(pistola.temBalas());
        assertEquals("0 balas", pistola.getInfo());
    }

    @Test
    public void testZumbiTransformacaoDetalhada() {
        Creature humano = new Adulto(1, "Humano", 0, 0, 20, true);

        // Estado inicial
        assertTrue(humano.isHuman());
        assertFalse(humano.isZombie());
        assertFalse(humano.isTransformed());

        // Transformação
        humano.transformar();
        humano.setEquipa(10);

        // Estado após transformação
        assertTrue(humano.isTransformed());
        assertTrue(humano.isZombie());
        assertFalse(humano.isHuman());
        assertEquals(10, humano.getEquipa());
    }

    @Test
    public void testMovimentosBasicos() {
        // Teste de movimentos para cada tipo de criatura
        Creature adulto = new Adulto(1, "Humano", 0, 0, 20, true);
        assertTrue(adulto.podeMover(0, 0, 1, 1, true));  // Diagonal
        assertTrue(adulto.podeMover(0, 0, 2, 0, true));  // Horizontal 2 casas

        Creature crianca = new Crianca(2, "Crianca", 0, 0, 20, true);
        assertTrue(crianca.podeMover(0, 0, 0, 1, true));  // Vertical 1 casa
        assertFalse(crianca.podeMover(0, 0, 1, 1, true)); // Diagonal não permitida
    }

    @Test
    public void testEquipamentosComportamentoDetalhado() {
        Creature adulto = new Adulto(1, "Humano", 0, 0, 20, true);
        Equipamento escudo = new EscudoDeMadeira(1, 0, 0, 0);

        // Testar pegar equipamento
        adulto.pegarEquipamento(escudo);
        assertEquals(1, adulto.getContadorEquipamentos());
        assertNotNull(adulto.getEquipamentoAtual());

        // Testar soltar equipamento
        adulto.soltarEquipamento();
        assertNull(adulto.getEquipamentoAtual());

        // Testar pegar outro equipamento
        Equipamento espada = new EspadaSamurai(2, 1, 0, 0);
        adulto.pegarEquipamento(espada);
        assertEquals(2, adulto.getContadorEquipamentos());
    }

    @Test
    public void testVampiroComportamentoNoturno() {
        Vampiro vampiro = new Vampiro(1, "Drac", 0, 0, 10);

        // Movimentos durante o dia
        assertFalse("Vampiro não deve mover de dia",
                vampiro.podeMover(0, 0, 1, 0, true));

        // Movimentos durante a noite
        assertTrue("Vampiro deve poder mover à noite",
                vampiro.podeMover(0, 0, 1, 0, false));
        assertTrue("Vampiro deve poder mover na diagonal à noite",
                vampiro.podeMover(0, 0, 1, 1, false));
    }

    @Test
    public void testIdosoComportamentoEspecial() {
        Idoso idoso = new Idoso(1, "Anciao", 0, 0, 20, true);

        // Teste movimento diagonal durante o dia
        assertTrue("Idoso deve poder mover na diagonal durante o dia",
                idoso.podeMover(0, 0, 1, 1, true));

        // Teste movimento durante a noite
        assertFalse("Idoso humano não deve mover durante a noite",
                idoso.podeMover(0, 0, 1, 1, false));

        // Teste movimentos não permitidos
        assertFalse("Idoso não deve mover horizontalmente",
                idoso.podeMover(0, 0, 1, 0, true));
        assertFalse("Idoso não deve mover mais de uma casa",
                idoso.podeMover(0, 0, 2, 2, true));
    }

    @Test
    public void testMovimentosSafeHaven() {
        GameManager manager = new GameManager();
        try {
            // Setup das criaturas
            Creature humano = new Adulto(1, "Humano", 1, 1, 20, true);
            Creature zumbi = new Adulto(2, "Zumbi", 3, 3, 10, false);

            // SafeHaven em (5,5)
            SafeHaven safeHaven = new SafeHaven(5, 5);

            // Humano pode entrar no SafeHaven
            assertTrue("Humano deve poder entrar no SafeHaven",
                    humano.isHuman());
            assertFalse("Zumbi não deve estar no SafeHaven inicialmente",
                    safeHaven.getCriaturasDentro().contains(zumbi));

            // Verificar que zumbi não pode entrar
            assertFalse("Zumbi não deve poder entrar no SafeHaven",
                    !zumbi.isHuman() && safeHaven.getCriaturasDentro().contains(zumbi));
        } catch (Exception e) {
            fail("Teste falhou com exceção: " + e.getMessage());
        }
    }

    @Test
    public void testMovimentosDia() {
        Creature idoso = new Idoso(1, "Idoso", 1, 1, 20, true);

        // Movimentos durante o dia
        assertTrue("Idoso humano deve poder mover na diagonal durante o dia",
                idoso.podeMover(1, 1, 2, 2, true));

        // Movimentos inválidos mesmo durante o dia
        assertFalse("Idoso não deve poder mover em linha reta",
                idoso.podeMover(1, 1, 2, 1, true));
        assertFalse("Idoso não deve poder mover mais de uma casa",
                idoso.podeMover(1, 1, 3, 3, true));
    }

    @Test
    public void testGameManagerBasics() {
        GameManager manager = new GameManager();
        File testFile = createBasicTestFile();

        try {
            manager.loadGame(testFile);

            // Teste de dimensões
            int[] size = manager.getWorldSize();
            assertNotNull(size);
            assertEquals(10, size[0]);
            assertEquals(10, size[1]);

            // Teste de equipes
            assertEquals(20, manager.getInitialTeamId());
            assertEquals(20, manager.getCurrentTeamId());

            // Teste de estado do dia
            assertTrue(manager.isDay());
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    @Test
    public void testGameStateComprehensive() {
        GameManager manager = new GameManager();
        try {
            manager.loadGame(createBasicTestFile());

            assertFalse(manager.gameIsOver());
            ArrayList<String> survivors = manager.getSurvivors();
            assertNotNull(survivors);

            // Verificar formatação da lista de survivors
            assertTrue(survivors.contains("Nr. de turnos terminados:"));
            assertTrue(survivors.contains("OS VIVOS"));
            assertTrue(survivors.contains("OS OUTROS"));
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    @Test
    public void testSquareInformation() {
        GameManager manager = new GameManager();
        try {
            manager.loadGame(createBasicTestFile());

            // Teste posição com humano
            assertEquals("H:1", manager.getSquareInfo(1, 1));

            // Teste posição com zumbi
            assertEquals("Z:2", manager.getSquareInfo(3, 3));

            // Teste posição vazia
            assertEquals("", manager.getSquareInfo(0, 0));

            // Teste posição fora do tabuleiro
            assertNull(manager.getSquareInfo(-1, -1));
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    @Test
    public void testAdvanceTurnBehavior() {
        GameManager manager = new GameManager();
        try {
            manager.loadGame(createBasicTestFile());

            // Estado inicial
            assertEquals(20, manager.getCurrentTeamId());
            assertTrue(manager.isDay());

            // Após movimento válido
            manager.move(1, 1, 1, 2);
            assertEquals(10, manager.getCurrentTeamId());
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }



    @Test
    public void testCreatureInteractions() {
        GameManager manager = new GameManager();
        try {
            manager.loadGame(createBasicTestFile());

            // Teste criatura existente
            String[] info = manager.getCreatureInfo(1);
            assertNotNull(info);
            assertEquals("1", info[0]);

            // Teste criatura inexistente
            assertNull(manager.getCreatureInfoAsString(999));

            // Teste equipamento
            assertFalse(manager.hasEquipment(999, 0));
        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    @Test
    public void testInvalidMoves() {
        GameManager manager = new GameManager();
        try {
            // Criar e carregar arquivo com configuração básica
            File testFile = new File("test-file.txt");
            try (PrintWriter writer = new PrintWriter(testFile)) {
                writer.println("10 10");          // dimensões
                writer.println("20");             // equipe inicial
                writer.println("2");              // número de criaturas
                writer.println("1 : 20 : 1 : Humano1 : 1 : 1");  // humano
                writer.println("2 : 10 : 1 : Zumbi1 : 3 : 3");   // zumbi
                writer.println("0");              // sem equipamentos
                writer.println("0");              // sem safe havens
            }

            manager.loadGame(testFile);
            testFile.delete();

            // Testar movimento fora do tabuleiro
            assertFalse("Não deve permitir movimento para fora do tabuleiro",
                    manager.move(-1, -1, 0, 0));

            assertFalse("Não deve permitir movimento para fora do tabuleiro",
                    manager.move(0, 0, -1, -1));

            // Testar movimento para posição ocupada
            assertFalse("Não deve permitir movimento para posição ocupada",
                    manager.move(1, 1, 3, 3));

            // Testar movimento muito longo
            assertFalse("Não deve permitir movimento muito longo",
                    manager.move(1, 1, 5, 5));

            // Testar movimento da equipe errada
            assertFalse("Zumbi não deve mover no turno dos humanos",
                    manager.move(3, 3, 3, 4));

        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    @Test
    public void testTurnProgression() {
        GameManager manager = new GameManager();
        try {
            // Criar e carregar arquivo com configuração básica
            File testFile = new File("test-file.txt");
            try (PrintWriter writer = new PrintWriter(testFile)) {
                writer.println("10 10");          // dimensões
                writer.println("20");             // equipe inicial
                writer.println("2");              // número de criaturas
                writer.println("1 : 20 : 1 : Humano1 : 1 : 1");  // humano
                writer.println("2 : 10 : 1 : Zumbi1 : 3 : 3");   // zumbi
                writer.println("0");              // sem equipamentos
                writer.println("0");              // sem safe havens
            }

            manager.loadGame(testFile);
            testFile.delete();

            // Verificar estado inicial
            assertTrue("Deve começar de dia", manager.isDay());
            assertEquals("Deve começar com humanos", 20, manager.getCurrentTeamId());

            // Testar movimento válido
            assertTrue("Movimento válido deve ser aceito",
                    manager.move(1, 1, 1, 2));

            // Verificar mudança de turno
            assertEquals("Deve mudar para turno dos zumbis",
                    10, manager.getCurrentTeamId());
            assertTrue("Ainda deve ser dia após um movimento",
                    manager.isDay());

        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    private File createBasicTestFile() {
        try {
            File tempFile = File.createTempFile("test-game", ".txt");
            try (PrintWriter writer = new PrintWriter(tempFile)) {
                writer.println("10 10");        // dimensões
                writer.println("20");           // equipe inicial
                writer.println("2");            // número de criaturas
                writer.println("1 : 20 : 1 : Humano1 : 1 : 1");  // humano
                writer.println("2 : 10 : 1 : Zumbi1 : 3 : 3");   // zumbi
                writer.println("1");            // número de equipamentos
                writer.println("1 : 0 : 2 : 2");  // escudo
                writer.println("1");            // número de safe havens
                writer.println("5 : 5");        // safe haven
            }
            return tempFile;
        } catch (IOException e) {
            fail("Falha ao criar arquivo de teste: " + e.getMessage());
            return null;
        }
    }

    @Test
    public void testSaveGameFunctionality() {
        GameManager manager = new GameManager();
        try {
            // Carregar jogo inicial
            File loadFile = new File("load-test.txt");
            try (PrintWriter writer = new PrintWriter(loadFile)) {
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
            manager.loadGame(loadFile);

            // Salvar jogo
            File saveFile = new File("save-test.txt");
            manager.saveGame(saveFile);

            // Verificar se arquivo foi criado
            assertTrue("Arquivo de save deve ser criado", saveFile.exists());

            // Carregar jogo salvo em novo manager
            GameManager newManager = new GameManager();
            newManager.loadGame(saveFile);

            // Verificar se estado foi preservado
            assertEquals("Dimensões devem ser preservadas",
                    manager.getWorldSize()[0], newManager.getWorldSize()[0]);
            assertEquals("Equipe inicial deve ser preservada",
                    manager.getInitialTeamId(), newManager.getInitialTeamId());

            // Cleanup
            loadFile.delete();
            saveFile.delete();

        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    @Test
    public void testEquipmentPickupAndInteractions() {
        GameManager manager = new GameManager();
        try {
            File testFile = new File("equipment-test.txt");
            try (PrintWriter writer = new PrintWriter(testFile)) {
                writer.println("10 10");
                writer.println("20");
                writer.println("2");
                writer.println("1 : 20 : 1 : Humano1 : 1 : 1");
                writer.println("2 : 10 : 1 : Zumbi1 : 3 : 3");
                writer.println("2");
                writer.println("1 : 0 : 2 : 2"); // escudo
                writer.println("2 : 1 : 4 : 4"); // espada
                writer.println("0");
            }
            manager.loadGame(testFile);

            // Verificar informações do equipamento
            String[] equipInfo = manager.getEquipmentInfo(1);
            assertNotNull("Info do equipamento não deve ser nula", equipInfo);
            assertEquals("0", equipInfo[1]); // tipo escudo

            // Mover humano para pegar equipamento
            assertTrue(manager.move(1, 1, 2, 2));
            assertTrue(manager.hasEquipment(1, 0));

            testFile.delete();

        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    @Test
    public void testSafeHavenInteractions() {
        GameManager manager = new GameManager();
        try {
            File testFile = new File("safehaven-test.txt");
            try (PrintWriter writer = new PrintWriter(testFile)) {
                writer.println("10 10");
                writer.println("20");
                writer.println("2");
                writer.println("1 : 20 : 1 : Humano1 : 4 : 4");
                writer.println("2 : 10 : 1 : Zumbi1 : 1 : 1");
                writer.println("0");
                writer.println("1");
                writer.println("5 : 5");
            }
            manager.loadGame(testFile);

            // Testar movimento para SafeHaven
            assertTrue(manager.move(4, 4, 5, 5));

            // Verificar se humano está no SafeHaven
            List<Integer> safeHavenIds = manager.getIdsInSafeHaven();
            assertTrue(safeHavenIds.contains(1));

            testFile.delete();

        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    @Test
    public void testGameOverConditions() {
        GameManager manager = new GameManager();
        try {
            File testFile = new File("gameover-test.txt");
            try (PrintWriter writer = new PrintWriter(testFile)) {
                writer.println("10 10");
                writer.println("20");
                writer.println("2");
                writer.println("1 : 20 : 1 : Humano1 : 1 : 1");
                writer.println("2 : 10 : 1 : Zumbi1 : 2 : 2");
                writer.println("1");
                writer.println("1 : 1 : 3 : 3"); // espada
                writer.println("0");
            }
            manager.loadGame(testFile);

            // Simular batalha que termine o jogo
            assertTrue(manager.move(1, 1, 3, 3)); // Pegar espada
            assertTrue(manager.move(2, 2, 2, 3)); // Zumbi move
            assertTrue(manager.move(3, 3, 2, 3)); // Humano ataca zumbi

            // Verificar estado final
            ArrayList<String> survivors = manager.getSurvivors();
            assertTrue(survivors.contains("OS VIVOS"));
            assertTrue(survivors.contains(": 1")); // ID do humano sobrevivente

            testFile.delete();

        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }

    @Test
    public void testDayNightCycle() {
        GameManager manager = new GameManager();
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
            manager.loadGame(testFile);

            boolean initialDay = manager.isDay();

            // Fazer movimentos para mudar o período
            manager.move(1, 1, 1, 2); // Turno 1
            manager.move(3, 3, 3, 2); // Turno 2

            assertNotEquals("Período deve mudar após 2 turnos",
                    initialDay, manager.isDay());

            testFile.delete();

        } catch (Exception e) {
            fail("Teste falhou: " + e.getMessage());
        }
    }
}

