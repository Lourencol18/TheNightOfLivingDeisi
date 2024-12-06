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
    private int turnosSemEventos = 0; // Contador de turnos sem eventos

    public void loadGame(File file) throws InvalidFileException, FileNotFoundException {
        tabuleiro = null;
        equipaInicial = -1;
        equipaAtual = -1;
        personagens.clear();
        equipamentos.clear();
        turnoAtual = equipaInicial;

        try (Scanner scanner = new Scanner(file)) {
            int currentLine = -1; // Para rastrear erros de linha

            // Lê dimensões do tabuleiro
            if (!scanner.hasNextLine()) {
                throw new InvalidFileException("Arquivo inválido: dimensões do tabuleiro ausentes.", currentLine);
            }
            currentLine++;
            String[] tamanho = scanner.nextLine().trim().split(" ");
            if (tamanho.length != 2) {
                throw new InvalidFileException("Arquivo inválido: dimensões do tabuleiro mal formatadas.", currentLine);
            }
            int width, height;
            try {
                height = Integer.parseInt(tamanho[0]); // Linhas (altura)
                width = Integer.parseInt(tamanho[1]); // Colunas (largura)
            } catch (NumberFormatException e) {
                throw new InvalidFileException("Dimensões do tabuleiro não são números válidos.", currentLine);
            }

            // Inicializa o tabuleiro
            tabuleiro = new Tabuleiro(width, height);
            System.out.println("Tabuleiro criado com dimensões: " + height + "x" + width);

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
                    int x = Integer.parseInt(criaturaData[4]); // Coluna
                    int y = Integer.parseInt(criaturaData[5]); // Linha

                    if (!tabuleiro.dentroDosLimites(x, y)) {
                        System.out.println("Coordenadas fora dos limites: (" + x + ", " + y + "). Criatura ignorada.");
                        continue; // Ignora criaturas fora dos limites
                    }

                    Creature criatura;
                    if (equipa == 20) { // Humanos
                        switch (tipoCriatura) {
                            case 0 -> criatura = new Crianca(id, nome, x, y, equipa, true);
                            case 1 -> criatura = new Adulto(id, nome, x, y, equipa, true);
                            case 2 -> criatura = new Idoso(id, nome, x, y, equipa, true);
                            case 3 -> criatura = new Cao(id, nome, x, y, equipa, true);
                            default -> throw new InvalidFileException("Tipo de criatura inválido para humanos.", currentLine);
                        }
                    } else if (equipa == 10) { // Zumbis
                        switch (tipoCriatura) {
                            case 0 -> criatura = new Crianca(id, nome, x, y, equipa, false);
                            case 1 -> criatura = new Adulto(id, nome, x, y, equipa, false);
                            case 2 -> criatura = new Idoso(id, nome, x, y, equipa, false);
                            case 4 -> criatura = new Vampiro(id, nome, x, y, equipa);
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
                        System.out.println("Coordenadas fora dos limites: (" + x + ", " + y + "). Equipamento ignorado.");
                        continue; // Ignora equipamentos fora dos limites
                    }

                    Equipamento equipamento;
                    switch (tipo) {
                        case 0 -> equipamento = new EscudoDeMadeira(id, tipo, x, y);
                        case 1 -> equipamento = new EspadaSamurai(id, tipo, x, y);
                        case 2 -> equipamento = new PistolaWaltherPPK(id, tipo, x, y);
                        case 3 -> equipamento = new Lixivia(id, tipo, x, y);
                        default -> throw new InvalidFileException("Tipo de equipamento inválido.", currentLine);
                    }

                    equipamentos.add(equipamento);

                } catch (NumberFormatException e) {
                    throw new InvalidFileException("Dados do equipamento contêm valores inválidos.", currentLine);
                }
            }

            // Lê o número de Safe Havens
            int numSafeHavens = 0; // Se não houver número, definimos como 0
            if (scanner.hasNext()) {
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
                        System.out.println("Coordenadas fora dos limites: (" + x + ", " + y + "). Safe Haven ignorado.");
                        continue; // Ignora Safe Havens fora dos limites
                    }

                    tabuleiro.adicionarSafeHaven(x, y);

                } catch (NumberFormatException e) {
                    throw new InvalidFileException("Coordenadas do Safe Haven contêm valores inválidos.", currentLine);
                }
            }

        } catch (IOException e) {
            throw new FileNotFoundException("Erro ao abrir o ficheiro.");
        }
    }







    public int[] getWorldSize() {
        return new int[]{tabuleiro.getHeight(), tabuleiro.getWidth()};
    }

    public int getInitialTeamId() {
        return equipaInicial;
    }

    public int getCurrentTeamId() {
        return equipaAtual;
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

                // Caso específico: Cão
                if (creature instanceof Cao) {
                    info.append(creature.getId()).append(" | ")         // ID
                            .append(creature.getTipoCriatura()).append(" | ") // Tipo (Cão)
                            .append(creature.getNome()).append(" @ (")      // Nome
                            .append(creature.getX()).append(", ").append(creature.getY()).append(")"); // Posição
                    return info.toString();
                }

                // Caso específico: Vampiro
                if (creature instanceof Vampiro) {
                    info.append(creature.getId()).append(" | ")         // ID
                            .append(creature.getTipoCriatura()).append(" | ") // Tipo (Vampiro)
                            .append(creature.getNome()).append(" | ")      // Nome
                            .append("-").append(creature.getEquipamentosDestruidos()) // Contador de equipamentos destruídos
                            .append(" @ (").append(creature.getX()).append(", ").append(creature.getY()).append(")"); // Posição
                    return info.toString();
                }

                // Determina o prefixo baseado no tipo da criatura (humano ou zumbi)
                String vidaPrefix = creature.isHuman() ? "+" : "-";

                info.append(creature.getId()).append(" | ")             // ID
                        .append(creature.getTipoCriatura()).append(" | ");   // Tipo (Criança, Adulto, etc.)

                // Atualiza o tipo de equipe
                if (creature.isZombie() && creature.isTransformed()) {
                    info.append("Zombie (Transformado)"); // Exibe Transformado para zumbis
                } else if (creature.isHuman()) {
                    info.append("Humano");
                } else if (!(creature instanceof Vampiro)) {
                    info.append("Zombie");
                }

                info.append(" | ")                                     // Separador
                        .append(creature.getNome()).append(" | ")      // Nome
                        .append(vidaPrefix);

                // Exibe o contador de equipamentos destruídos para zumbis
                if (creature.isZombie()) {
                    info.append(creature.getEquipamentosDestruidos());
                } else {
                    info.append(creature.getContadorEquipamentos());
                }

                info.append(" @ (").append(creature.getX()).append(", ").append(creature.getY()).append(")"); // Posição

                // Adiciona informações de equipamento, se aplicável
                if (creature.getEquipamentoAtual() != null) {
                    Equipamento equipamento = creature.getEquipamentoAtual();
                    info.append(" | ").append(equipamento.getId()).append(" | ")
                            .append(equipamento.getNome()).append(" @ (")
                            .append(equipamento.getX()).append(", ").append(equipamento.getY()).append(")");
                    String additionalInfo = equipamento.getInfo();
                    if (!additionalInfo.isEmpty()) {
                        info.append(" | ").append(additionalInfo);
                    }
                }

                return info.toString();
            }
        }
        return "Criatura não encontrada.";
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
                        null                             // Placeholder para ícones ou imagens
                };
            }
        }
        throw new IllegalArgumentException("Equipamento não encontrado para o ID: " + id);
    }

    public String getEquipmentInfoAsString(int id) {
        for (Equipamento equipamento : equipamentos) {
            if (equipamento.getId() == id) {
                StringBuilder info = new StringBuilder();
                info.append(equipamento.getId()).append(" | ") // ID do equipamento
                        .append(equipamento.getNome()).append(" @ (")
                        .append(equipamento.getX()).append(", ")
                        .append(equipamento.getY()).append(")");

                // Adiciona informações extras específicas do equipamento, se disponíveis
                String additionalInfo = equipamento.getInfo();
                if (additionalInfo != null && !additionalInfo.isEmpty()) {
                    info.append(" | ").append(additionalInfo);
                }

                return info.toString();
            }
        }
        return null; // Caso o equipamento não seja encontrado
    }





    public boolean hasEquipment(int creatureId, int equipmentTypeId) {
        for (Creature creature : personagens) {
            if (creature.getId() == creatureId) {
                // Apenas humanos podem ter equipamentos
                if (!creature.isHuman()) {
                    return false;
                }

                // Verifica se a criatura tem um equipamento atual
                Equipamento equipamentoAtual = creature.getEquipamentoAtual();
                if (equipamentoAtual == null) {
                    return false; // Nenhum equipamento
                }

                // Verifica o tipo de criatura e suas restrições de equipamentos
                switch (creature.getTipoCriatura()) {

                    case "Criança": // Crianças só podem ter equipamentos defensivos (0 e 3)
                        if (equipmentTypeId == 0 || equipmentTypeId == 3) { // Apenas tipos defensivos
                            return equipamentoAtual.getTipo() == equipmentTypeId;
                        }

                    case "Adulto": // Adultos podem ter todos os tipos de equipamentos
                        if (equipmentTypeId == 0 || equipmentTypeId == 1 | equipmentTypeId == 2 | equipmentTypeId == 3) {
                            return equipamentoAtual.getTipo() == equipmentTypeId;
                        }



                    default:
                        return false; // Caso o tipo não seja reconhecido
                }
            }
        }
        return false; // Criatura não encontrada
    }

    public boolean move(int xO, int yO, int xD, int yD) {
        // Verifica se as coordenadas de destino estão dentro do tabuleiro
        if (!tabuleiro.dentroDosLimites(xD, yD)) {
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

        // Verifica se é a vez da equipe correta
        boolean turnoParaHumanos = equipaAtual == 20;
        if ((turnoParaHumanos && !creatureToMove.isHuman()) || (!turnoParaHumanos && !creatureToMove.isZombie())) {
            return false;
        }

        // Verifica se o vampiro está tentando se mover de dia
        if (creatureToMove instanceof Vampiro && isDay()) {
            return false;
        }

        // Verifica se o movimento é válido
        if (!creatureToMove.podeMover(xO, yO, xD, yD, isDay())) {
            return false;
        }

        // Verifica se há outra criatura na posição de destino
        Creature targetCreature = null;
        for (Creature creature : personagens) {
            if (creature.getX() == xD && creature.getY() == yD) {
                targetCreature = creature;
                break;
            }
        }

        // Interação entre criaturas
        if (targetCreature != null) {
            if (creatureToMove.isHuman() && targetCreature.isZombie()) {
                Equipamento equipamentoAtual = creatureToMove.getEquipamentoAtual();
                if (equipamentoAtual instanceof PistolaWaltherPPK) {
                    PistolaWaltherPPK pistola = (PistolaWaltherPPK) equipamentoAtual;
                    if (pistola.temBalas()) {
                        pistola.gastarBala();
                        personagens.remove(targetCreature); // Zumbi morto
                        creatureToMove.setX(xD); // Move o humano para a casa onde o zumbi estava
                        creatureToMove.setY(yD);
                        advanceTurn();
                        return true;
                    }
                }
                return false; // Humano não tem como se defender
            } else if (creatureToMove.isZombie() && targetCreature.isHuman()) {
                Equipamento equipamentoAtual = targetCreature.getEquipamentoAtual();
                if (equipamentoAtual instanceof PistolaWaltherPPK) {
                    PistolaWaltherPPK pistola = (PistolaWaltherPPK) equipamentoAtual;
                    if (pistola.temBalas()) {
                        pistola.gastarBala();
                        advanceTurn();
                        return true; // Defesa bem-sucedida
                    }
                }

                // Transferência do contador de equipamentos
                int equipamentosUsados = targetCreature.getContadorEquipamentos();
                targetCreature.transformar(); // Humano vira zumbi
                targetCreature.setEquipa(10); // Atualiza a equipe
                targetCreature.soltarEquipamento(); // Remove o equipamento do humano transformado
                targetCreature.incrementarEquipamentosDestruidos(equipamentosUsados); // Adiciona o valor ao contador destruído
                advanceTurn();
                return true;
            } else {
                return false; // Movimento inválido
            }
        }

        // Verifica se há um equipamento na posição de destino
        Equipamento equipamentoParaInteragir = null;
        for (Equipamento equipamento : equipamentos) {
            if (equipamento.getX() == xD && equipamento.getY() == yD) {
                equipamentoParaInteragir = equipamento;
                break;
            }
        }

        if (equipamentoParaInteragir != null && !creatureToMove.podeMoverParaComEquipamento(equipamentoParaInteragir)) {
            System.out.println("Movimento bloqueado: a criatura não pode se mover para a posição (" + xD + ", " + yD + ") por causa do equipamento.");
            return false;
        }

        // Atualiza a posição da criatura
        creatureToMove.setX(xD);
        creatureToMove.setY(yD);

        // Interação com equipamentos
        if (creatureToMove.isHuman() && equipamentoParaInteragir != null) {
            if (creatureToMove.podePegarEquipamento(equipamentoParaInteragir)) {
                creatureToMove.pegarEquipamento(equipamentoParaInteragir);
                equipamentos.remove(equipamentoParaInteragir);
            }
        }

        if (creatureToMove.isZombie() && equipamentoParaInteragir != null) {
            creatureToMove.destruirEquipamento();
            creatureToMove.incrementarEquipamentosDestruidos(); // Incrementa o contador
            equipamentos.remove(equipamentoParaInteragir); // Zumbi destrói equipamento
        }

        // Interação com Safe Haven
        if (creatureToMove.isHuman() && tabuleiro.isSafeHaven(xD, yD)) {
            for (SafeHaven safeHaven : SafeHaven.getSafeHavens()) {
                if (safeHaven.getX() == xD && safeHaven.getY() == yD) {
                    if (safeHaven.entrar(creatureToMove)) {
                        personagens.remove(creatureToMove); // Remove do jogo
                        advanceTurn();
                        return true;
                    }
                }
            }
        }

        advanceTurn(); // Avança o turno
        return true;
    }



















    private void advanceTurn() {
        turnoAtual++;

        // Alterna entre 2 turnos de dia e 2 turnos de noite
        dia = ((turnoAtual + 1) / 2) % 2 == 0;

        // Alterna a equipe
        equipaAtual = (equipaAtual == 10) ? 20 : 10;

        // Verifica se houve eventos significativos (mortos ou transformações)
        boolean houveEventos = false;

        for (Creature creature : personagens) {
            if (creature.isZombie() && creature.getEquipamentosDestruidos() > 0) {
                houveEventos = true; // Equipamentos destruídos contam como evento
                break;
            }
            if (creature.isHuman() && creature.isTransformed()) {
                houveEventos = true; // Transformação conta como evento
                break;
            }
        }

        // Atualiza o contador de turnos sem eventos
        if (houveEventos) {
            turnosSemEventos = 0; // Reinicia o contador
        } else {
            turnosSemEventos++; // Incrementa se nenhum evento ocorreu
        }
    }















    public boolean gameIsOver() {
        // 1. Verifica se passaram 8 turnos sem transformações ou mortes
        if (turnosSemEventos >= 8) {
            return true;
        }

        // 2. Verifica se restam apenas elementos de uma equipe no tabuleiro
        boolean existemHumanos = false;
        boolean existemZumbis = false;

        for (Creature creature : personagens) {
            if (creature.isHuman()) {
                existemHumanos = true;
            }
            if (creature.isZombie()) {
                existemZumbis = true;
            }
            if (existemHumanos && existemZumbis) {
                break; // Ambos existem, jogo continua
            }
        }

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


    public List<Integer> getIdsInSafeHaven() {
        List<Integer> idsInSafeHaven = new ArrayList<>();

        for (SafeHaven safeHaven : SafeHaven.getSafeHavens()) {
            for (Creature criatura : safeHaven.getCriaturasDentro()) {

                idsInSafeHaven.add(criatura.getId());
            }
        }

        return idsInSafeHaven;
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
