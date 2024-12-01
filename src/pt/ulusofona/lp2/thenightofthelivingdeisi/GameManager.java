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
    private int numSafeHavens = 0;

    public void loadGame(File file) throws InvalidFileException, FileNotFoundException {
        tabuleiro = null;
        equipaInicial = -1;
        equipaAtual = -1;
        personagens.clear();
        equipamentos.clear();

        try (Scanner scanner = new Scanner(file)) {
            int currentLine = 0; // Para rastrear erros de linha

            // Lê dimensões do tabuleiro
            if (!scanner.hasNextLine()) {
                throw new InvalidFileException("Arquivo inválido: dimensões do tabuleiro ausentes.", currentLine);
            }
            currentLine++;
            String linhaDimensao = scanner.nextLine().trim();
            System.out.println("Lendo dimensões: " + linhaDimensao);  // Adicione esta linha para depuração
            String[] tamanho = linhaDimensao.split(" ");
            if (tamanho.length != 2) {
                throw new InvalidFileException("Arquivo inválido: dimensões do tabuleiro mal formatadas.", currentLine);
            }

            int width, height;
            try {
                width = Integer.parseInt(tamanho[0]);
                height = Integer.parseInt(tamanho[1]);
            } catch (NumberFormatException e) {
                throw new InvalidFileException("Dimensões do tabuleiro não são números válidos.", currentLine);
            }
            tabuleiro = new Tabuleiro(width, height);

            // Lê a equipe inicial
            if (!scanner.hasNext()) {
                throw new InvalidFileException("Arquivo inválido: equipe inicial ausente.", currentLine);
            }
            currentLine++;
            try {
                equipaInicial = Integer.parseInt(scanner.next());
            } catch (NumberFormatException e) {
                throw new InvalidFileException("Equipe inicial não é um número válido.", currentLine);
            }
            if (equipaInicial != 10 && equipaInicial != 20) {
                throw new InvalidFileException("Equipe inicial deve ser 10 ou 20.", currentLine);
            }
            equipaAtual = equipaInicial;

            // Lê o número de criaturas
            if (!scanner.hasNext()) {
                throw new InvalidFileException("Arquivo inválido: número de criaturas ausente.", currentLine);
            }
            currentLine++;
            int numCreatures;
            try {
                numCreatures = Integer.parseInt(scanner.next());
            } catch (NumberFormatException e) {
                throw new InvalidFileException("Número de criaturas não é um número válido.", currentLine);
            }
            if (numCreatures < 0) {
                throw new InvalidFileException("Número de criaturas não pode ser negativo.", currentLine);
            }

            // Processa cada criatura
            for (int i = 0; i < numCreatures; i++) {
                currentLine++;
                String linhaCriatura = scanner.nextLine().trim();
                if (linhaCriatura.isEmpty()) {
                    i--;
                    continue;
                }

                String[] criaturaData = linhaCriatura.split(" : ");
                if (criaturaData.length != 6) {
                    throw new InvalidFileException("Dados da criatura mal formatados.", currentLine);
                }

                try {
                    int id = Integer.parseInt(criaturaData[0]);
                    int equipa = Integer.parseInt(criaturaData[1]);
                    int tipoCriatura = Integer.parseInt(criaturaData[2]);
                    String nome = criaturaData[3];
                    int x = Integer.parseInt(criaturaData[4]);
                    int y = Integer.parseInt(criaturaData[5]);

                    if (!tabuleiro.dentroDosLimites(x, y)) {
                        throw new InvalidFileException("Coordenadas da criatura fora dos limites.", currentLine);
                    }

                    Creature criatura;
                    if (equipa == 20) { // Humanos
                        switch (tipoCriatura) {
                            case 0 -> criatura = new Crianca(id, nome, x, y, equipa, true); // Humano
                            case 1 -> criatura = new Adulto(id, nome, x, y, equipa, true); // Humano
                            case 2 -> criatura = new Idoso(id, nome, x, y, equipa, true); // Humano
                            default -> throw new InvalidFileException("Tipo de criatura inválido para humanos.", currentLine);
                        }
                    } else if (equipa == 10) { // Zumbis
                        switch (tipoCriatura) {
                            case 0 -> criatura = new Crianca(id, nome, x, y, equipa, false); // Zumbi
                            case 1 -> criatura = new Adulto(id, nome, x, y, equipa, false); // Zumbi
                            case 2 -> criatura = new Idoso(id, nome, x, y, equipa, false); // Zumbi
                            default -> throw new InvalidFileException("Tipo de criatura inválido para zumbis.", currentLine);
                        }
                    } else {
                        throw new InvalidFileException("Equipe inválida.", currentLine);
                    }

                    personagens.add(criatura);

                } catch (NumberFormatException e) {
                    throw new InvalidFileException("Dados da criatura contêm valores inválidos.", currentLine);
                }
            }

            // Lê o número de equipamentos
            if (!scanner.hasNext()) {
                throw new InvalidFileException("Arquivo inválido: número de equipamentos ausente.", currentLine);
            }
            currentLine++;
            int numEquipments;
            try {
                numEquipments = Integer.parseInt(scanner.next());
            } catch (NumberFormatException e) {
                throw new InvalidFileException("Número de equipamentos não é um número válido.", currentLine);
            }
            if (numEquipments < 0) {
                throw new InvalidFileException("Número de equipamentos não pode ser negativo.", currentLine);
            }

            // Processa cada equipamento
            for (int i = 0; i < numEquipments; i++) {
                currentLine++;
                String linhaEquipamento = scanner.nextLine().trim();
                if (linhaEquipamento.isEmpty()) {
                    i--;
                    continue;
                }

                String[] equipamentoData = linhaEquipamento.split(" : ");
                if (equipamentoData.length != 4) {
                    throw new InvalidFileException("Dados do equipamento mal formatados.", currentLine);
                }

                try {
                    int id = Integer.parseInt(equipamentoData[0]);
                    int tipo = Integer.parseInt(equipamentoData[1]);
                    int x = Integer.parseInt(equipamentoData[2]);
                    int y = Integer.parseInt(equipamentoData[3]);

                    if (!tabuleiro.dentroDosLimites(x, y)) {
                        throw new InvalidFileException("Coordenadas do equipamento fora dos limites.", currentLine);
                    }

                    Equipamento equipamento;
                    switch (tipo) {
                        case 0 -> equipamento = new EscudoDeMadeira(id, x, y);
                        case 1 -> equipamento = new EspadaSamurai(id, x, y);
                        case 2 -> equipamento = new PistolaWaltherPPK(id, x, y);
                        case 3 -> equipamento = new Lixivia(id, x, y);
                        default -> throw new InvalidFileException("Tipo de equipamento inválido.", currentLine);
                    }

                    equipamentos.add(equipamento);

                } catch (NumberFormatException e) {
                    throw new InvalidFileException("Dados do equipamento contêm valores inválidos.", currentLine);
                }
            }

            // Lê o número de Safe Havens
            if (!scanner.hasNext()) {
                numSafeHavens = 0;  // Se não houver número, definimos como 0
            } else {
                currentLine++;
                try {
                    numSafeHavens = Integer.parseInt(scanner.next());
                } catch (NumberFormatException e) {
                    throw new InvalidFileException("Número de Safe Havens não é um número válido.", currentLine);
                }
                if (numSafeHavens < 0) {
                    throw new InvalidFileException("Número de Safe Havens não pode ser negativo.", currentLine);
                }
            }

                // Processa cada Safe Haven
            for (int i = 0; i < numSafeHavens; i++) {
                currentLine++;
                String linhaSafeHaven = scanner.nextLine().trim();
                if (linhaSafeHaven.isEmpty()) {
                    i--;
                    continue;
                }

                String[] coordenadas = linhaSafeHaven.split(" : ");
                if (coordenadas.length != 2) {
                    throw new InvalidFileException("Dados do Safe Haven mal formatados.", currentLine);
                }

                try {
                    int x = Integer.parseInt(coordenadas[0]);
                    int y = Integer.parseInt(coordenadas[1]);

                    if (!tabuleiro.dentroDosLimites(x, y)) {
                        throw new InvalidFileException("Coordenadas do Safe Haven fora dos limites.", currentLine);
                    }

                    tabuleiro.adicionarSafeHaven(x, y);

                } catch (NumberFormatException e) {
                    throw new InvalidFileException("Coordenadas do Safe Haven contêm valores inválidos.", currentLine);
                }
            }


        }
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
        if (!tabuleiro.dentroDosLimites(x, y)) {
            return null; // Retorna null para posições fora do tabuleiro
        }
        for (Creature creature : personagens) {
            if (creature.getX() == x && creature.getY() == y) {
                // Identifica se a criatura é humano ou zumbi
                String tipo = creature.isHuman() ? "H" : "Z";
                return tipo + ":" + creature.getId();
            }
        }

        // Verifica se há um equipamento na posição
        for (Equipamento equipment : equipamentos) {
            if (equipment.getX() == x && equipment.getY() == y) {
                return "E:" + equipment.getId();
            }
        }

        // Verifica se há um Safe Haven na posição
        if (tabuleiro.isSafeHaven(x, y)) {
            return "SH";
        }

        return ""; // Caso a posição esteja vazia
    }

    public String[] getCreatureInfo(int id) {
        String[] Jogador = new String[7];  // Array para armazenar as informações
        String team = "";  // Variável para armazenar o time (Humano ou Zombie)

        // Percorre a lista de personagens (criaturas)
        for (Creature creature : personagens) {  // Usando a lista 'personagens' em vez de 'getCreatures'
            if (creature.getId() == id) {  // Se encontrar a criatura com o ID correspondente
                // Verifica a equipe da criatura (Humano ou Zombie)
                if (creature.getEquipa() == 20) {
                    team = "Humano";  // Se a equipe for 20, é Humano
                } else if (creature.getEquipa() == 10) {
                    team = "Zombie";  // Se a equipe for 10, é Zombie
                }

                // Preenche o array com as informações da criatura
                Jogador[0] = String.valueOf(creature.getId());    // ID da criatura
                Jogador[1] = creature.getTipoCriatura();           // Tipo de criatura (ex.: "Criança", "Adulto", etc.)
                Jogador[2] = team;                                 // Time (Humano ou Zombie)
                Jogador[3] = creature.getNome();                   // Nome da criatura
                Jogador[4] = String.valueOf(creature.getX());      // Coordenada X
                Jogador[5] = String.valueOf(creature.getY());      // Coordenada Y
                Jogador[6] = null;                                 // PNG (pode ser adicionado se necessário)

                return Jogador;  // Retorna o array com as informações da criatura
            }
        }

        // Caso não encontre a criatura, retorna um array com 7 elementos e uma mensagem de erro
        Jogador[0] = "Criatura não encontrada";  // Preenche o índice 0 com a mensagem de erro
        return Jogador;  // Retorna o array com 7 elementos
    }

    public String getCreatureInfoAsString(int id) {
        for (Creature creature : personagens) {
            if (creature.getId() == id) {
                StringBuilder info = new StringBuilder();

                // Adiciona informações básicas da criatura
                info.append(creature.getId()).append(" | ")          // ID
                        .append(creature.getTipoCriatura()).append(" | ") // Tipo de criatura (ex.: "Criança", "Adulto")
                        .append(creature.getTipo()).append(" | ")        // Tipo (ex.: "Humano", "Zombie")
                        .append(creature.getNome()).append(" | ")        // Nome
                        .append("@ (").append(creature.getX()).append(", ").append(creature.getY()).append(")"); // Coordenadas

                // Verifica se a criatura possui um equipamento
                Equipamento equipamentoAtual = creature.getEquipamentoAtual();
                if (equipamentoAtual != null) {
                    info.append(" | Equipamento: ").append(equipamentoAtual.getNome()); // Nome do equipamento
                }

                return info.toString(); // Retorna a string construída
            }
        }

        return "Criatura não encontrada."; // Retorna mensagem padrão para ID inválido
    }



    public String[] getEquipmentInfo(int id) {
        for (Equipamento equipment : equipamentos) {
            if (equipment.getId() == id) {
                // Identifica o tipo numérico com base na classe do equipamento
                String tipoNumerico;
                if (equipment instanceof EscudoDeMadeira) {
                    tipoNumerico = "0"; // Tipo numérico para Escudo de Madeira
                } else if (equipment instanceof EspadaSamurai) {
                    tipoNumerico = "1"; // Tipo numérico para Espada Samurai
                } else if (equipment instanceof PistolaWaltherPPK) {
                    tipoNumerico = "2"; // Tipo numérico para Pistola
                } else if (equipment instanceof Lixivia) {
                    tipoNumerico = "3"; // Tipo numérico para Lixívia
                } else {
                    throw new IllegalArgumentException("Tipo desconhecido para equipamento com ID: " + id);
                }

                // Retorna as informações no formato esperado
                return new String[]{
                        String.valueOf(equipment.getId()), // ID
                        tipoNumerico,                     // Tipo numérico (0, 1, 2, 3)
                        String.valueOf(equipment.getX()), // Posição X
                        String.valueOf(equipment.getY()), // Posição Y
                        null                              // Placeholder para ícones ou imagens
                };
            }
        }
        throw new IllegalArgumentException("Equipamento não encontrado para o ID: " + id);
    }

    public String getEquipmentInfoAsString(int id) {
        for (Equipamento equipamento : equipamentos) {
            if (equipamento.getId() == id) {
                StringBuilder info = new StringBuilder();

                // Constrói a string no formato esperado
                info.append(equipamento.getId()).append(" | ") // ID
                        .append(equipamento.getNome()).append(" @ (") // Nome do equipamento
                        .append(equipamento.getX()).append(", ") // Posição X
                        .append(equipamento.getY()).append(")"); // Posição Y

                // Adiciona informações específicas do equipamento, se aplicável
                String additionalInfo = equipamento.getInfo();
                if (!additionalInfo.isEmpty()) {
                    info.append(" | ").append(additionalInfo);
                }

                return info.toString();
            }
        }
        return ""; // Retorno padrão caso o equipamento não seja encontrado
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
