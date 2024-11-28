package pt.ulusofona.lp2.thenightofthelivingdeisi;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class GameManager {
   private Tabuleiro tabuleiro;
   private int equipaInicial;
   private int equipaAtual;
   private ArrayList<Equipamento> equipamentos = new ArrayList<>();
   private ArrayList<Creature> personagens = new ArrayList<>();
   private int turnoAtual = 0;
   private static boolean dia = true;
   private boolean terminado = false;
   private int turnoSemEventos = 0;

    public boolean loadGame(File file) throws InvalidFileException, FileNotFoundException {
        // Inicializa variáveis e limpa listas
        tabuleiro = null;
        equipaInicial = -1;
        equipaAtual = -1;
        personagens.clear();
        equipamentos.clear();

        try (Scanner scanner = new Scanner(file)) {
            // Lê as dimensões do tabuleiro (primeiro linhas, depois colunas)
            if (!scanner.hasNextLine()) {
                throw new InvalidFileException("Arquivo inválido: dimensões do tabuleiro ausentes.");
            }
            String[] tamanho = scanner.nextLine().trim().split(" ");
            if (tamanho.length != 2) {
                throw new InvalidFileException("Arquivo inválido: dimensões do tabuleiro mal formatadas.");
            }
            int height, width;
            try {
                height = Integer.parseInt(tamanho[0]); // Lê o número de linhas primeiro (altura)
                width = Integer.parseInt(tamanho[1]); // Depois o número de colunas (largura)
            } catch (NumberFormatException e) {
                throw new InvalidFileException("Dimensões do tabuleiro não são números válidos.");
            }
            tabuleiro = new Tabuleiro(width, height);

            // Lê a equipe inicial
            if (!scanner.hasNext()) {
                throw new InvalidFileException("Arquivo inválido: equipe inicial ausente.");
            }
            try {
                equipaInicial = Integer.parseInt(scanner.next());
            } catch (NumberFormatException e) {
                throw new InvalidFileException("Equipe inicial não é um número válido.");
            }
            if (equipaInicial != 10 && equipaInicial != 20) {
                throw new InvalidFileException("Equipe inicial deve ser 10 ou 20.");
            }
            equipaAtual = equipaInicial;

            // Lê o número de criaturas
            if (!scanner.hasNext()) {
                throw new InvalidFileException("Arquivo inválido: número de criaturas ausente.");
            }
            int numCreatures;
            try {
                numCreatures = Integer.parseInt(scanner.next());
            } catch (NumberFormatException e) {
                throw new InvalidFileException("Número de criaturas não é um número válido.");
            }
            if (numCreatures < 0) {
                throw new InvalidFileException("Número de criaturas não pode ser negativo.");
            }

            // Lê cada criatura
            personagens.clear();
            for (int i = 0; i < numCreatures; i++) {
                String linhaCriatura = scanner.nextLine().trim();
                if (linhaCriatura.isEmpty()) {
                    i--;
                    continue;
                }

                String[] criaturaData = linhaCriatura.split(" : ");
                if (criaturaData.length != 6) {
                    throw new InvalidFileException("Dados da criatura mal formatados.");
                }

                try {
                    int id = Integer.parseInt(criaturaData[0]);
                    int equipa = Integer.parseInt(criaturaData[1]); // 10 = Zumbi, 20 = Humano
                    int tipoCriatura = Integer.parseInt(criaturaData[2]); // Tipo da criatura (0 a 4)
                    String nome = criaturaData[3];
                    int x = Integer.parseInt(criaturaData[4]);
                    int y = Integer.parseInt(criaturaData[5]);

                    // Verifica se as coordenadas estão dentro dos limites
                    if (!tabuleiro.dentroDosLimites(x, y)) {
                        throw new InvalidFileException("Coordenadas da criatura fora dos limites.");
                    }

                    // Verifica Safe Haven
                    if (tabuleiro.isSafeHaven(x, y)) {
                        throw new InvalidFileException("A criatura não pode ser posicionada em um Safe Haven.");
                    }

                    // Inicializa a variável criatura
                    Creature criatura;

                    // Verifica a equipe
                    if (equipa == 20) { // Humanos
                        switch (tipoCriatura) {
                            case 0: // Criança
                                criatura = new CriançaHumano(id, nome, x, y, equipa);
                                break;
                            case 1: // Adulto
                                criatura = new AdultoHumano(id, nome, x, y, equipa);
                                break;
                            case 2: // Idoso
                                criatura = new IdosoHumano(id, nome, x, y, equipa);
                                break;
                            case 3: // Cão
                                criatura = new Cao(id, nome, x, y, equipa); // Humanos podem ter cães
                                break;
                            default:
                                throw new InvalidFileException("Tipo de criatura inválido para humanos: " + tipoCriatura);
                        }
                    } else if (equipa == 10) { // Zumbis
                        switch (tipoCriatura) {
                            case 0: // Criança
                                criatura = new CriançaZombie(id, nome, x, y, equipa);
                                break;
                            case 1: // Adulto
                                criatura = new AdultoZombie(id, nome, x, y, equipa);
                                break;
                            case 2: // Idoso
                                criatura = new IdosoZombie(id, nome, x, y, equipa);
                                break;
                            case 4: // Vampiro
                                criatura = new Vampiro(id, nome, x, y, equipa); // Vampiro só é zumbi
                                break;
                            default:
                                throw new InvalidFileException("Tipo de criatura inválido para zumbis: " + tipoCriatura);
                        }
                    } else {
                        throw new InvalidFileException("Equipe inválida: " + equipa);
                    }

                    // Adiciona a criatura à lista de personagens
                    personagens.add(criatura);

                } catch (NumberFormatException e) {
                    throw new InvalidFileException("Dados da criatura contêm valores inválidos.");
                }



            }

            // Lê o número de equipamentos
            if (!scanner.hasNext()) {
                throw new InvalidFileException("Arquivo inválido: número de equipamentos ausente.");
            }
            int numEquipments;
            try {
                numEquipments = Integer.parseInt(scanner.next());
            } catch (NumberFormatException e) {
                throw new InvalidFileException("Número de equipamentos não é um número válido.");
            }
            if (numEquipments < 0) {
                throw new InvalidFileException("Número de equipamentos não pode ser negativo.");
            }

            equipamentos.clear();

            // Lê cada equipamento
            for (int i = 0; i < numEquipments; i++) {
                String linhaEquipamento = scanner.nextLine().trim();
                if (linhaEquipamento.isEmpty()) {
                    i--;
                    continue;
                }

                String[] equipamentoData = linhaEquipamento.split(" : ");
                if (equipamentoData.length != 4) {
                    throw new InvalidFileException("Dados do equipamento mal formatados.");
                }

                try {
                    int id = Integer.parseInt(equipamentoData[0]);
                    int tipo = Integer.parseInt(equipamentoData[1]);
                    int x = Integer.parseInt(equipamentoData[2]);
                    int y = Integer.parseInt(equipamentoData[3]);

                    if (!tabuleiro.dentroDosLimites(x, y)) {
                        throw new InvalidFileException("Coordenadas do equipamento fora dos limites.");
                    }

                    // Cria instância do equipamento com base no tipo
                    Equipamento equipamento;
                    switch (tipo) {
                        case 0: // Escudo de Madeira
                            equipamento = new EscudoDeMadeira(id, x, y);
                            break;
                        case 1: // Espada Samurai
                            equipamento = new EspadaSamurai(id, x, y);
                            break;
                        case 2: // Pistola
                            equipamento = new PistolaWaltherPPK(id, x, y);
                            break;
                        case 3: // Lixívia
                            equipamento = new Lixivia(id, x, y);
                            break;
                        default:
                            throw new InvalidFileException("Tipo de equipamento inválido: " + tipo);
                    }

                    // Adiciona o equipamento à lista
                    equipamentos.add(equipamento);
                } catch (NumberFormatException e) {
                    throw new InvalidFileException("Dados do equipamento contêm valores inválidos.");
                }
            }


            return true; // Carregamento bem-sucedido
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Arquivo não encontrado: " + file.getAbsolutePath());
        }
    }






    public void alternarTurno() {
         turnoAtual = turnoAtual == 20 ? 10 : 20;
    }


    public int[] getWorldSize() {
        return new int[]{tabuleiro.getHeight(), tabuleiro.getWidth()};
    }


    public int getInitialTeamId() {
        return equipaInicial;
    }

    public int getCurrentTeamId() {
        return turnoAtual;
    }



    public  boolean isDay() {
        return dia;
    }

    public String getSquareInfo(int x, int y) {
        // Verifica se há uma criatura na posição
        for (Creature creature : personagens) {
            if (creature.getX() == x && creature.getY() == y) {
                return creature.getTipoCriatura() + ":" + creature.getId();
            }
        }

        // Verifica se há um equipamento na posição
        for (Equipamento equipment : equipamentos) {
            if (equipment.getX() == x && equipment.getY() == y) {
                return "E:" + equipment.getId();
            }
        }



        return ""; // Caso não haja nada na posição
    }


    public String[] getCreatureInfo(int id) {
        for (Creature creature : personagens) {
            if (creature.getId() == id) {
                return new String[]{
                        String.valueOf(creature.getId()),    // ID
                        creature.getTipo(),                  // Tipo
                        creature.getTipoCriatura(),         //equipa
                        creature.getNome(),                  // Nome
                        String.valueOf(creature.getX()),     // Posição X
                        String.valueOf(creature.getY()),     // Posição Y
                        null
                };
            }
        }
        return null; // Se a criatura com o ID fornecido não for encontrada
    }


    public String getCreatureInfoAsString(int id) {
        for (Creature criatura :personagens) {
            if (criatura.getId() == id) {
                return criatura.getInfoAsString();
            }
        }
        return "Criatura não encontrada.";
    }




    public String[] getEquipmentInfo(int id) {
        for (Equipamento equipment : equipamentos) {
            if (equipment.getId() == id) {
                // Retorne o tipo como número (0 ou 1) em vez de texto
                return new String[]{
                        String.valueOf(equipment.getId()),    // ID
                        equipment.getNome(),                    // Tipo como número (0 ou 1)
                        String.valueOf(equipment.getX()),     // Posição X
                        String.valueOf(equipment.getY()),     // Posição Y
                        null                                 // PNG ou caminho do ícone, se aplicável
                };
            }
        }
        return null; // Se o equipamento com o ID fornecido não for encontrado
    }





    public String getEquipmentInfoAsString(int id) {
        for (Equipamento equipamento : equipamentos) {
            if (equipamento.getId() == id) {
                StringBuilder info = new StringBuilder();
                info.append("-").append(equipamento.getId()).append(" | ")
                        .append(equipamento.getNome()).append(" @ (")
                        .append(equipamento.getX()).append(", ")
                        .append(equipamento.getY()).append(")");

                // Adiciona as informações específicas do equipamento
                String additionalInfo = equipamento.getInfo();
                if (!additionalInfo.isEmpty()) {
                    info.append(" | ").append(additionalInfo);
                }

                return info.toString();
            }
        }

        // Caso o equipamento não seja encontrado
        return null;
    }




    public boolean hasEquipment(int creatureId, int equipmentTypeId) {
        for (Creature criatura : personagens) {
            if (criatura.getId() == creatureId) {
                Equipamento equipamentoAtual = criatura.getEquipamentoAtual();

                // Verifica se a criatura tem um equipamento atual e se ela pode tê-lo
                return equipamentoAtual != null
                        && equipamentoAtual.getId() == equipmentTypeId
                        && criatura.podeTerEquipamento(equipmentTypeId);
            }
        }
        return false; // Criatura não encontrada ou regras não permitidas
    }


    public boolean move(int xO, int yO, int xD, int yD) {
        // Verifica se as coordenadas estão dentro do tabuleiro
        if (xD < 0 || xD >= tabuleiro.getWidth() || yD < 0 || yD >= tabuleiro.getHeight()) {
            return false;
        }

        // Encontra a criatura na posição de origem
        Creature creatureToMove = null;
        for (Creature creature : personagens) {
            if (creature.getX() == xO && creature.getY() == yO) {
                creatureToMove = creature;
                break;
            }
        }

        // Se não houver criatura na origem, retorna falso
        if (creatureToMove == null) {
            return false;
        }

        // Determina a equipe correta com base no turno e na equipe inicial
        boolean turnoParaHumanos = (turnoAtual % 2 == 0 && equipaInicial == 1) || (turnoAtual % 2 == 1 && equipaInicial == 0);
        boolean equipeCorreta = (turnoParaHumanos && creatureToMove.isHuman()) || (!turnoParaHumanos && creatureToMove.isZombie());

        // Verifica se é a vez da equipe correta
        if (!equipeCorreta) {
            return false;
        }

        // Verifica se há outra criatura no destino
        for (Creature creature : personagens) {
            if (creature.getX() == xD && creature.getY() == yD) {
                return false;
            }
        }

        // Verifica se o movimento é válido (apenas em linha reta)
        int distanciaX = Math.abs(xD - xO);
        int distanciaY = Math.abs(yD - yO);

        // Permite apenas movimento em linha reta (horizontal ou vertical)
        if (!((distanciaX == 1 && distanciaY == 0) || (distanciaX == 0 && distanciaY == 1))) {
            return false;
        }

        // Verifica equipamentos na posição de destino
        Equipamento equipamentoParaInteragir = null;
        for (Equipamento equipment : equipamentos) {
            if (equipment.getX() == xD && equipment.getY() == yD) {
                equipamentoParaInteragir = equipment;
                break;
            }
        }

        // Executa o movimento
        creatureToMove.x = xD;
        creatureToMove.y = yD;

        // Se houver equipamento na posição de destino
        if (equipamentoParaInteragir != null) {
            if (creatureToMove.isHuman()) { // Humanos pegam equipamentos
                creatureToMove.pegarEquipamento(equipamentoParaInteragir); // Adiciona o equipamento ao humano
                equipamentos.remove(equipamentoParaInteragir); // Remove o equipamento do tabuleiro
            } else if (creatureToMove.isZombie()) { // Zumbis destroem equipamentos
                creatureToMove.destruirEquipamento(); // Incrementa o contador de destruições
                equipamentos.remove(equipamentoParaInteragir); // Remove o equipamento do tabuleiro
            }
        }

        // Incrementa o turno para alternar a equipe
        turnoAtual++;

        // A cada 2 turnos, alterna entre dia e noite
        if (turnoAtual % 2 == 0) {
            dia = !dia;
        }

        return true;
    }

    public boolean gameIsOver() {
        // 1. Verifica se passaram 8 turnos sem transformações ou mortes
        if (turnoSemEventos >= 8) {
            return true;
        }

        // 2. Verifica se restam apenas elementos de uma equipe no tabuleiro
        boolean existemHumanos = personagens.stream().anyMatch(Creature::isHuman);
        boolean existemZumbis = personagens.stream().anyMatch(Creature::isZombie);

        // O jogo termina se apenas humanos ou apenas zumbis existirem
        return !existemHumanos || !existemZumbis;
    }

    public void atualizarTurnosSemEventos(boolean houveEvento) {
        if (houveEvento) {
            turnoSemEventos = 0; // Reseta o contador se houve evento
        } else {
            turnoSemEventos++; // Incrementa o contador caso contrário
        }
    }

    public ArrayList<String> getSurvivors() {
        ArrayList<String> resultados = new ArrayList<>();

        // Número de turnos terminados
        resultados.add("Nr. de turnos terminados: " + turnoAtual);
        resultados.add("");

        // Separador para os vivos
        resultados.add("OS VIVOS");
        for (Creature creature : personagens) {
            if (creature.isHuman()) { // Tipo 1 representa humano
                resultados.add(creature.getId() + " " + creature.getNome());
            }
        }
        resultados.add(""); // Linha em branco entre os vivos e os outros

        // Separador para os outros (zumbis)
        resultados.add("OS OUTROS");
        for (Creature creature : personagens) {
            if (creature.isZombie()) { // Tipo 0 representa zumbi
                resultados.add(creature.getId() + " (antigamente conhecido como " + creature.getNome() + ")");
            }
        }
        resultados.add("-----"); // Separador final

        return resultados;
    }

    public void saveGame(File file) throws IOException {
        // Implemente a lógica de salvar o estado do jogo no arquivo
        try (FileWriter writer = new FileWriter(file)) {
            // Escreva os dados necessários no arquivo
            writer.write("Dimensões: " + tabuleiro.getHeight() + "x" + tabuleiro.getWidth() + "\n");
            writer.write("Equipe inicial: " + equipaInicial + "\n");
            writer.write("Turno atual: " + turnoAtual + "\n");

            // Adicione outros dados relevantes (criaturas, equipamentos, etc.)
        }
    }


    public List<Integer> getIdsInSafeHaven(){
        return new ArrayList<>();
    }


    public JPanel getCreditsPanel() {

        JPanel creditsPanel = new JPanel();
        creditsPanel.setLayout(new BorderLayout());


        String creditsText = "<html><center><h1>Créditos</h1>"
                + "<p>Desenvolvido por: Ruben Graça e Lourenço Luís</p>"
                + "<p>Apoio moral: Meu gato, que dormiu o projeto todo.</p>"
                + "<p>Café fornecido por: Minha cafeteira incansável.</p>"
                + "<p>Testadores: Meu teclado e meu monitor, que aguentaram firme.</p>"
                + "<p><i>Agradecimentos especiais à procrastinação.</i></p>"
                + "</center></html>";


        JLabel creditsLabel = new JLabel(creditsText, SwingConstants.CENTER);


        creditsPanel.add(creditsLabel, BorderLayout.CENTER);

        return creditsPanel;
    }


    public HashMap<String, String> customizeBoard() {
        return new HashMap<>();
    }


}
