package pt.ulusofona.lp2.thenightofthelivingdeisi;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class GameManager {
   private Tabuleiro tabuleiro;
   private int equipaInicial;
   private int equipaAtual;
   private ArrayList<Equipamento> equipamentos = new ArrayList<>();
   private ArrayList<Creature> personagens = new ArrayList<>();
   private int turnoAtual = 0;
   private  boolean dia = true;
    private int numSafeHavens = 0;
    private int turnosSemEventos = 0; // Contador de turnos sem eventos
    private boolean zumbiMorto = false;
    private boolean humanoTransformado = false;
    private int invalidMovesHumanos = 0;
    private int invalidMovesZombies = 0;

    public void loadGame(File file) throws InvalidFileException, FileNotFoundException {
        tabuleiro = null;
        equipaInicial = -1;
        equipaAtual = -1;
        personagens.clear();
        equipamentos.clear();
        turnoAtual = equipaInicial;
        SafeHaven.getSafeHavens().clear();

        try (Scanner scanner = new Scanner(file)) {
            int currentLine = -1;

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
                height = Integer.parseInt(tamanho[0]);
                width = Integer.parseInt(tamanho[1]);
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

            // Carrega criaturas e equipamentos
            loadCriaturas(scanner, currentLine);
            loadEquipamentos(scanner, currentLine);

            // Carrega Safe Havens
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
                            continue;
                        }

                        SafeHaven safeHaven = new SafeHaven(x, y);
                        tabuleiro.adicionarSafeHaven(x, y);
                        SafeHaven.add(safeHaven);

                    } catch (NumberFormatException e) {
                        throw new InvalidFileException("Coordenadas do Safe Haven contêm valores inválidos.", currentLine);
                    }
                }
            }

        } catch (IOException e) {
            throw new FileNotFoundException("Erro ao abrir o ficheiro.");
        }
    }

    private void loadCriaturas(Scanner scanner, int currentLine) throws InvalidFileException {
        // Verifica se existe informação sobre número de criaturas
        if (!scanner.hasNext()) {
            throw new InvalidFileException("Arquivo inválido: número de criaturas ausente.", currentLine);
        }
        currentLine++;

        // Lê e valida o número de criaturas
        int numCreatures;
        try {
            numCreatures = Integer.parseInt(scanner.next());
        } catch (NumberFormatException e) {
            throw new InvalidFileException("Número de criaturas não é um número válido.", currentLine);
        }
        // Verifica se o número é válido
        if (numCreatures < 0) {
            throw new InvalidFileException("Número de criaturas não pode ser negativo.", currentLine);
        }

        // Para cada criatura no arquivo
        for (int i = 0; i < numCreatures; i++) {
            currentLine++;
            String linhaCriatura = scanner.nextLine().trim();
            // Pula linhas vazias
            if (linhaCriatura.isEmpty()) {
                i--;
                continue;
            }

            // Separa os dados da criatura
            String[] criaturaData = linhaCriatura.split(" : ");
            // Verifica se tem todos os dados necessários
            if (criaturaData.length != 6) {
                throw new InvalidFileException("Dados da criatura mal formatados.", currentLine);
            }

            try {
                // Extrai os dados da linha
                int id = Integer.parseInt(criaturaData[0]);
                int equipa = Integer.parseInt(criaturaData[1]);
                int tipoCriatura = Integer.parseInt(criaturaData[2]);
                String nome = criaturaData[3];
                int x = Integer.parseInt(criaturaData[4]);
                int y = Integer.parseInt(criaturaData[5]);

                // Verifica se posição está dentro do tabuleiro
                if (!tabuleiro.dentroDosLimites(x, y)) {
                    continue;
                }

                // Cria a criatura apropriada baseada na equipe e tipo
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

                // Adiciona a criatura à lista
                personagens.add(criatura);

            } catch (NumberFormatException e) {
                throw new InvalidFileException("Dados da criatura contêm valores inválidos.", currentLine);
            }
        }
    }

    private void loadEquipamentos(Scanner scanner, int currentLine) throws InvalidFileException {
        // Verifica se existe informação sobre número de equipamentos
        if (!scanner.hasNext()) {
            throw new InvalidFileException("Arquivo inválido: número de equipamentos ausente.", currentLine);
        }
        currentLine++;

        // Lê e valida o número de equipamentos
        int numEquipments;
        try {
            numEquipments = Integer.parseInt(scanner.next());
        } catch (NumberFormatException e) {
            throw new InvalidFileException("Número de equipamentos não é um número válido.", currentLine);
        }
        // Verifica se o número é válido
        if (numEquipments < 0) {
            throw new InvalidFileException("Número de equipamentos não pode ser negativo.", currentLine);
        }

        // Para cada equipamento no arquivo
        for (int i = 0; i < numEquipments; i++) {
            currentLine++;
            String linhaEquipamento = scanner.nextLine().trim();
            // Pula linhas vazias
            if (linhaEquipamento.isEmpty()) {
                i--;
                continue;
            }

            // Separa os dados do equipamento
            String[] equipamentoData = linhaEquipamento.split(" : ");
            // Verifica se tem todos os dados necessários
            if (equipamentoData.length != 4) {
                throw new InvalidFileException("Dados do equipamento mal formatados.", currentLine);
            }

            try {
                // Extrai os dados da linha
                int id = Integer.parseInt(equipamentoData[0]);
                int tipo = Integer.parseInt(equipamentoData[1]);
                int x = Integer.parseInt(equipamentoData[2]);
                int y = Integer.parseInt(equipamentoData[3]);

                // Verifica se posição está dentro do tabuleiro
                if (!tabuleiro.dentroDosLimites(x, y)) {
                    continue;
                }

                // Cria o equipamento apropriado baseado no tipo
                Equipamento equipamento;
                switch (tipo) {
                    case 0 -> equipamento = new EscudoDeMadeira(id, tipo, x, y);
                    case 1 -> equipamento = new EspadaSamurai(id, tipo, x, y);
                    case 2 -> equipamento = new PistolaWaltherPPK(id, tipo, x, y);
                    case 3 -> equipamento = new Lixivia(id, tipo, x, y);
                    default -> throw new InvalidFileException("Tipo de equipamento inválido.", currentLine);
                }

                // Adiciona o equipamento à lista
                equipamentos.add(equipamento);

            } catch (NumberFormatException e) {
                throw new InvalidFileException("Dados do equipamento contêm valores inválidos.", currentLine);
            }
        }
    }





////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



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
        // Array que vai armazenar as informações da criatura
        String[] Jogador = new String[7];
        String team = "";

        // Primeiro procura a criatura nos Safe Havens
        for (SafeHaven safeHaven : SafeHaven.getSafeHavens()) {
            for (Creature creature : safeHaven.getCriaturasDentro()) {
                if (creature.getId() == id) {
                    // Define o tipo de equipe baseado no ID da equipe
                    if (creature.getEquipa() == 20) {
                        team = "Humano";
                    } else if (creature.getEquipa() == 10) {
                        // Se for zumbi, verifica se é transformado
                        team = creature.isTransformed() ? "Zombie (Transformado)" : "Zombie";
                    }

                    // Preenche o array com as informações
                    Jogador[0] = String.valueOf(creature.getId());     // ID
                    Jogador[1] = creature.getTipoCriatura();          // Tipo (Adulto, Criança, etc)
                    Jogador[2] = team;                                // Equipe (Humano/Zombie)
                    Jogador[3] = creature.getNome();                  // Nome
                    // Criaturas no SafeHaven não têm coordenadas
                    Jogador[4] = null;                                // X
                    Jogador[5] = null;                                // Y
                    Jogador[6] = null;                                // Reservado
                    return Jogador;
                }
            }
        }

        // Se não encontrou no SafeHaven, procura nas criaturas no tabuleiro
        for (Creature creature : personagens) {
            if (creature.getId() == id) {
                // Define o tipo de equipe
                if (creature.getEquipa() == 20) {
                    team = "Humano";
                } else if (creature.getEquipa() == 10) {
                    team = creature.isTransformed() ? "Zombie (Transformado)" : "Zombie";
                }

                // Preenche o array com as informações
                Jogador[0] = String.valueOf(creature.getId());        // ID
                Jogador[1] = creature.getTipoCriatura();             // Tipo
                Jogador[2] = team;                                   // Equipe
                Jogador[3] = creature.getNome();                     // Nome
                Jogador[4] = String.valueOf(creature.getX());        // Posição X
                Jogador[5] = String.valueOf(creature.getY());        // Posição Y
                Jogador[6] = null;                                   // Reservado
                return Jogador;
            }
        }

        // Se não encontrou a criatura em lugar nenhum
        Jogador[0] = "Criatura não encontrada";
        return Jogador;
    }




    public String getCreatureInfoAsString(int id) {
         // Primeiro verifica nas criaturas dentro dos SafeHavens
        for (SafeHaven safeHaven : SafeHaven.getSafeHavens()) {
            for (Creature creature : safeHaven.getCriaturasDentro()) {
                if (creature.getId() == id) {
                    StringBuilder info = new StringBuilder();

                    // Formato base
                    info.append(creature.getId())
                            .append(" | ")
                            .append(creature.getTipoCriatura())
                            .append(" | ");

                    // Para Cão, usa o formato simplificado
                    if (creature.getTipoCriatura().equals("Cão")) {
                        info.append(creature.getNome())
                                .append(" @ Safe Haven");
                    } else {
                        // Para outras criaturas, usa o formato completo
                        info.append("Humano | ")
                                .append(creature.getNome())
                                .append(" | +")
                                .append(creature.getContadorEquipamentos())
                                .append(" @ Safe Haven");
                    }

                    return info.toString();
                }
            }
        }

        // Se não encontrou no SafeHaven, procura nas criaturas no tabuleiro
        for (Creature creature : personagens) {
            if (creature.getId() == id) {
                StringBuilder info = new StringBuilder();

                // Caso específico: Cão
                if (creature.getTipoCriatura().equals("Cão")) {
                    info.append(creature.getId())
                            .append(" | ")
                            .append(creature.getTipoCriatura())
                            .append(" | ")
                            .append(creature.getNome())
                            .append(" @ (")
                            .append(creature.getX())
                            .append(", ")
                            .append(creature.getY())
                            .append(")");
                    return info.toString();
                }

                // Caso específico: Vampiro
                if (creature.getTipoCriatura().equals("Vampiro")) {
                    info.append(creature.getId())
                            .append(" | ")
                            .append(creature.getTipoCriatura())
                            .append(" | ")
                            .append(creature.getNome())
                            .append(" | -")
                            .append(creature.getEquipamentosDestruidos())
                            .append(" @ (")
                            .append(creature.getX())
                            .append(", ")
                            .append(creature.getY())
                            .append(")");
                    return info.toString();
                }

                // Outros tipos de criaturas
                info.append(creature.getId())
                        .append(" | ")
                        .append(creature.getTipoCriatura())
                        .append(" | ");

                if (creature.isZombie() && creature.isTransformed()) {
                    info.append("Zombie (Transformado)");
                } else if (creature.isHuman()) {
                    info.append("Humano");
                } else {
                    info.append("Zombie");
                }

                info.append(" | ")
                        .append(creature.getNome())
                        .append(" | ");

                if (creature.isHuman()) {
                    info.append("+").append(creature.getContadorEquipamentos());
                } else {
                    info.append("-").append(creature.getEquipamentosDestruidos());
                }

                info.append(" @ (")
                        .append(creature.getX())
                        .append(", ")
                        .append(creature.getY())
                        .append(")");

                // Adiciona informações do equipamento se houver
                if (creature.getEquipamentoAtual() != null) {
                    Equipamento equipamento = creature.getEquipamentoAtual();
                    info.append(" | ")
                            .append(equipamento.getId())
                            .append(" | ")
                            .append(equipamento.getNome())
                            .append(" @ (")
                            .append(equipamento.getX())
                            .append(", ")
                            .append(equipamento.getY())
                            .append(")");

                    String additionalInfo = equipamento.getInfo();
                    if (!additionalInfo.isEmpty()) {
                        info.append(" | ").append(additionalInfo);
                    }
                }

                return info.toString();
            }
        }
        return null;
    }






    public String[] getEquipmentInfo(int id) {
        for (Equipamento equipment : equipamentos) {
            if (equipment.getId() == id) {
                // Retorna as informações no formato esperado
                return new String[]{
                        String.valueOf(equipment.getId()),    // ID
                        String.valueOf(equipment.getTipo()),  // Tipo numérico (0, 1, 2, 3)
                        String.valueOf(equipment.getX()),     // Posição X
                        String.valueOf(equipment.getY()),     // Posição Y
                        null                                 // Placeholder para ícones ou imagens
                };
            }
        }
        return null;
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
                    case "Idoso":
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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean move(int xO, int yO, int xD, int yD) {
        // Verifica se o destino está dentro dos limites do tabuleiro
        if (!tabuleiro.dentroDosLimites(xD, yD)) {
            incrementInvalidMoves();
            return false;
        }

        // Encontra a criatura que vai se mover baseado na posição origem
        Creature creatureToMove = null;
        for (Creature creature : personagens) {
            if (creature.getX() == xO && creature.getY() == yO) {
                creatureToMove = creature;
                break;
            }
        }

        // Se não encontrou criatura na posição origem, movimento é inválido
        if (creatureToMove == null) {
            incrementInvalidMoves();
            return false;
        }

        // Regra especial para Cão: verifica se pode fazer o movimento
        if (creatureToMove.getTipoCriatura().equals("Cão")) {
            if (!creatureToMove.podeMover(xO, yO, xD, yD, isDay())) {
                incrementInvalidMoves();
                return false;
            }
        }

        // Verifica se é o turno correto para a criatura se mover
        boolean turnoParaHumanos = equipaAtual == 20;
        if ((turnoParaHumanos && !creatureToMove.isHuman()) || (!turnoParaHumanos && !creatureToMove.isZombie())) {
            incrementInvalidMoves();
            return false;
        }

        // Nova regra: Movimento de adultos humanos para criar criança
        if (creatureToMove.getTipoCriatura().equals("Adulto") && creatureToMove.isHuman()) {
            // Verifica se há outro adulto humano na posição de destino
            Creature targetCreature = null;
            for (Creature creature : personagens) {
                if (creature.getX() == xD && creature.getY() == yD &&
                        creature.getTipoCriatura().equals("Adulto") && creature.isHuman()) {
                    targetCreature = creature;
                    break;
                }
            }

            // Apenas permite o movimento se nenhum dos adultos tiver equipamento
            if (targetCreature != null && creatureToMove.getEquipamentoAtual() == null &&
                    targetCreature.getEquipamentoAtual() == null) {
                // Cria a criança na posição à esquerda do "progenitor"
                int childX = xD - 1;
                int childY = yD;
                if (!tabuleiro.dentroDosLimites(childX, childY) || isPositionOccupied(childX, childY)) {
                    // Tenta outras posições em sentido horário
                    childX = xD;
                    childY = yD - 1;
                    if (!tabuleiro.dentroDosLimites(childX, childY) || isPositionOccupied(childX, childY)) {
                        childX = xD + 1;
                        childY = yD;
                        if (!tabuleiro.dentroDosLimites(childX, childY) || isPositionOccupied(childX, childY)) {
                            childX = xD;
                            childY = yD + 1;
                            if (!tabuleiro.dentroDosLimites(childX, childY) || isPositionOccupied(childX, childY)) {
                                incrementInvalidMoves();
                                return false;
                            }
                        }
                    }
                }

                // Gera o ID concatenando os IDs dos pais
                int childId = Integer.parseInt(creatureToMove.getId() + "" + targetCreature.getId());
                String childName = creatureToMove.getNome() + " & " + targetCreature.getNome();
                Crianca child = new Crianca(childId, childName, childX, childY, 20, true);
                personagens.add(child);

                // Não movemos nenhum dos adultos, eles ficam nas suas posições originais
                advanceTurn();
                return true;
            } else {
                incrementInvalidMoves();
                return false;
            }
        }

        // Regras especiais para Idoso
        if (creatureToMove.getTipoCriatura().equals("Idoso")) {
            if (creatureToMove.isHuman()) {
                if (!turnoParaHumanos || !isDay()) {
                    incrementInvalidMoves();
                    return false;
                }
            } else if (creatureToMove.isZombie()) {
                if (turnoParaHumanos) {
                    incrementInvalidMoves();
                    return false;
                }
            }
            if (creatureToMove.getEquipamentoAtual() != null) {
                Equipamento equipamentoAtual = creatureToMove.getEquipamentoAtual();
                equipamentoAtual.setX(xO);
                equipamentoAtual.setY(yO);
                equipamentos.add(equipamentoAtual);
                creatureToMove.soltarEquipamento();
            }
        }

        if (creatureToMove.getTipoCriatura().equals("Vampiro") && isDay()) {
            incrementInvalidMoves();
            return false;
        }

        if (!creatureToMove.podeMover(xO, yO, xD, yD, isDay())) {
            incrementInvalidMoves();
            return false;
        }

        if (creatureToMove.isZombie() && tabuleiro.isSafeHaven(xD, yD)) {
            incrementInvalidMoves();
            return false;
        }

        Creature targetCreature = null;
        for (Creature creature : personagens) {
            if (creature.getX() == xD && creature.getY() == yD) {
                targetCreature = creature;
                break;
            }
        }

        if (targetCreature != null) {
            if ((creatureToMove.isZombie() || creatureToMove.getTipoCriatura().equals("Vampiro")) &&
                    targetCreature.getTipoCriatura().equals("Cão")) {
                incrementInvalidMoves();
                return false;
            }

            if (creatureToMove.isZombie() && targetCreature.isHuman()) {
                return processarDefesa(creatureToMove, targetCreature);
            }

            if (creatureToMove.isHuman() && targetCreature.isZombie()) {
                return processarAtaque(creatureToMove, targetCreature, xD, yD);
            }
            incrementInvalidMoves();
            return false;
        }

        Equipamento equipamentoParaInteragir = null;
        for (Equipamento equipamento : equipamentos) {
            if (equipamento.getX() == xD && equipamento.getY() == yD) {
                equipamentoParaInteragir = equipamento;
                break;
            }
        }

        if (equipamentoParaInteragir != null && !creatureToMove.podeMoverParaComEquipamento(equipamentoParaInteragir)) {
            incrementInvalidMoves();
            return false;
        }

        creatureToMove.setX(xD);
        creatureToMove.setY(yD);

        if (creatureToMove.isHuman() && equipamentoParaInteragir != null) {
            Equipamento equipamentoAtual = creatureToMove.getEquipamentoAtual();
            if (equipamentoAtual != null) {
                equipamentoAtual.setX(xO);
                equipamentoAtual.setY(yO);
                equipamentos.add(equipamentoAtual);
                creatureToMove.soltarEquipamento();
            }

            if (creatureToMove.podePegarEquipamento(equipamentoParaInteragir)) {
                creatureToMove.pegarEquipamento(equipamentoParaInteragir);
                equipamentos.remove(equipamentoParaInteragir);
            }
        }

        if (creatureToMove.isZombie() && equipamentoParaInteragir != null) {
            creatureToMove.destruirEquipamento();
            creatureToMove.incrementarEquipamentosDestruidos(1);
            equipamentos.remove(equipamentoParaInteragir);
        }

        if (creatureToMove.isHuman() && tabuleiro.isSafeHaven(xD, yD)) {
            for (SafeHaven safeHaven : tabuleiro.getSafeHavens()) {
                if (safeHaven.getX() == xD && safeHaven.getY() == yD) {
                    creatureToMove.setX(xO);
                    creatureToMove.setY(yO);
                    safeHaven.entrar(creatureToMove);
                    personagens.remove(creatureToMove);
                    advanceTurn();
                    return true;
                }
            }
        }
        advanceTurn();
        return true;
    }

    private boolean processarDefesa(Creature zumbi, Creature humano) {
        // Verifica se o humano tem algum equipamento
        Equipamento equipamentoAtual = humano.getEquipamentoAtual();
        if (equipamentoAtual != null) {
            // Caso seja Pistola (tipo 2)
            if (equipamentoAtual.getTipo() == 2) {
                PistolaWaltherPPK pistola = (PistolaWaltherPPK) equipamentoAtual;
                if (pistola.temBalas()) {
                    // Usa uma bala para se defender
                    pistola.gastarBala();
                    advanceTurn();
                    return true; // Defesa bem sucedida
                }
            }

            // Caso seja Lixívia (tipo 3)
            if (equipamentoAtual.getTipo() == 3) {
                Lixivia lixivia = (Lixivia) equipamentoAtual;
                // Se ainda tem lixívia disponível
                if (lixivia.getLitros() > 0.0) {
                    if (lixivia.executarAcao(zumbi, humano)) {
                        advanceTurn();
                        return true; // Defesa bem sucedida
                    }
                }
                // Se a lixívia acabou, humano é transformado
                if (lixivia.getLitros() <= 0.0) {
                    int equipamentosUsados = humano.getContadorEquipamentos();
                    humano.transformar();
                    humano.setEquipa(10); // Muda para equipe dos zumbis
                    humano.soltarEquipamento();
                    humano.incrementarEquipamentosDestruidos(equipamentosUsados);
                    humanoTransformado = true;
                    advanceTurn();
                    return true;
                }
            }

            // Caso seja Espada (tipo 1) ou equipamento defensivo
            if (equipamentoAtual.getTipo() == 1 || equipamentoAtual.isDefensivo()) {
                advanceTurn();
                return true; // Defesa bem sucedida
            }
        }

        // Se não tem equipamento ou equipamento não ajudou na defesa
        // Humano é transformado em zumbi
        int equipamentosUsados = humano.getContadorEquipamentos();
        humano.transformar();
        humano.setEquipa(10); // Muda para equipe dos zumbis
        humano.soltarEquipamento();
        humano.incrementarEquipamentosDestruidos(equipamentosUsados);
        humanoTransformado = true;
        advanceTurn();
        return true;
    }

    private boolean processarAtaque(Creature humano, Creature zumbi, int xD, int yD) {
        // Verifica se o humano está equipado com alguma arma
        Equipamento equipamentoAtual = humano.getEquipamentoAtual();
        if (equipamentoAtual != null) {
            // Caso seja Espada (tipo 1)
            if (equipamentoAtual.getTipo() == 1) {
                personagens.remove(zumbi);     // Remove o zumbi do jogo
                zumbiMorto = true;             // Marca que houve morte de zumbi
                humano.setX(xD);               // Move o humano para posição do zumbi
                humano.setY(yD);
                advanceTurn();                 // Avança o turno
                return true;                   // Ataque bem sucedido
            }

            // Caso seja Pistola (tipo 2)
            if (equipamentoAtual.getTipo() == 2) {
                PistolaWaltherPPK pistola = (PistolaWaltherPPK) equipamentoAtual;
                if (pistola.temBalas()) {
                    pistola.gastarBala();          // Gasta uma bala
                    personagens.remove(zumbi);     // Remove o zumbi do jogo
                    zumbiMorto = true;             // Marca que houve morte de zumbi
                    humano.setX(xD);               // Move o humano para posição do zumbi
                    humano.setY(yD);
                    advanceTurn();                 // Avança o turno
                    return true;                   // Ataque bem sucedido
                }
            }
        }
        return false;   // Ataque falhou (sem equipamento ou sem balas)
    }

    private void advanceTurn() {
        // Incrementa o contador de turnos
        turnoAtual++;

        // Alterna entre dia e noite
        // Se (turnoAtual + 1) / 2 for par, é dia; se for ímpar, é noite
        dia = ((turnoAtual + 1) / 2) % 2 == 0;

        // Alterna a equipe atual entre humanos (20) e zumbis (10)
        equipaAtual = (equipaAtual == 10) ? 20 : 10;

        // Verifica se houve eventos significativos neste turno
        boolean houveEventos = humanoTransformado || zumbiMorto;

        if (houveEventos) {
            // Se houve eventos, reseta o contador de turnos sem eventos
            turnosSemEventos = 0;
            // Reseta as flags de eventos
            humanoTransformado = false;
            zumbiMorto = false;
        } else {
            // Se não houve eventos, incrementa o contador de turnos sem eventos
            turnosSemEventos++;
        }
    }
    private void incrementInvalidMoves() {
        if (equipaAtual == 20) {
            invalidMovesHumanos++;
        } else {
            invalidMovesZombies++;
        }
    }
    // Método auxiliar para verificar se uma posição está ocupada por alguma criatura
    private boolean isPositionOccupied(int x, int y) {
        for (Creature creature : personagens) {
            if (creature.getX() == x && creature.getY() == y) {
                return true;
            }
        }
        return false;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean gameIsOver() {
        // 1. Verifica se passaram exatamente 8 turnos sem eventos significativos
        if (turnosSemEventos >= 8) {
            return true; // O jogo termina se não houver eventos por 8 turnos consecutivos
        }

        if (invalidMovesHumanos >= 6 || invalidMovesZombies >= 6) {
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
        }

        // O jogo termina se apenas humanos ou apenas zumbis existirem
        if (!existemHumanos || !existemZumbis) {
            return true;
        }

        // O jogo continua enquanto houver humanos e zumbis e menos de 8 turnos sem eventos
        return false;
    }














    public ArrayList<String> getSurvivors() {
        ArrayList<String> resultados = new ArrayList<>();
        ArrayList<Creature> vivos = new ArrayList<>();

        // Coleta todos os humanos vivos (tanto do tabuleiro quanto do SafeHaven)
        for (Creature creature : personagens) {
            if (creature.isHuman()) {
                vivos.add(creature);
            }
        }

        // Adiciona humanos do Safe Haven à lista de vivos
        for (SafeHaven safeHaven : SafeHaven.getSafeHavens()) {
            for (Creature creature : safeHaven.getCriaturasDentro()) {
                if (creature.isHuman()) {
                    vivos.add(creature);
                }
            }
        }



        // Adiciona cabeçalho
        resultados.add("Nr. de turnos terminados:");
        resultados.add(String.valueOf(turnoAtual + 1));
        resultados.add("Nr. de jogadas invalidas:");
        resultados.add("humanos:" + invalidMovesHumanos +" "+"zombies:" + invalidMovesZombies);
        resultados.add("");

        // Adiciona os vivos ordenados
        resultados.add("OS VIVOS");
        for (Creature creature : vivos) {
            resultados.add(creature.getId() + " " + creature.getNome());
        }
        resultados.add("");

        // Adiciona os outros (zumbis)
        resultados.add("OS OUTROS");
        for (Creature creature : personagens) {
            if (creature.isZombie()) {
                resultados.add(creature.getId() + " (antigamente conhecido como " + creature.getNome() + ")");
            }
        }
        resultados.add("-----");

        return resultados;
    }


    public void saveGame(File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Grava as dimensões do tabuleiro
            writer.write(tabuleiro.getWidth() + " " + tabuleiro.getHeight() + "\n");

            // Grava a equipe inicial
            writer.write(equipaAtual + "\n");

            // Grava os personagens
            writer.write(personagens.size() + "\n");
            for (Creature creature : personagens) {
                // Determina o tipo numérico da criatura com base em `getTipoCriatura`
                int tipoNumerico = switch (creature.getTipoCriatura()) {
                    case "Criança" -> 0;
                    case "Adulto" -> 1;
                    case "Idoso" -> 2;
                    case "Cão" -> 3;
                    case "Vampiro" -> 4;
                    default -> -1; // Tipo desconhecido
                };

                writer.write(creature.getId() + " : "
                        + creature.getEquipa() + " : "
                        + tipoNumerico + " : " // Grava o número correspondente ao tipo
                        + creature.getNome() + " : "
                        + creature.getX() + " : "
                        + creature.getY() + "\n");
            }

            // Grava os equipamentos
            writer.write(equipamentos.size() + "\n");
            for (Equipamento equipamento : equipamentos) {
                writer.write(equipamento.getId() + " : "
                        + equipamento.getTipo() + " : "
                        + equipamento.getX() + " : "
                        + equipamento.getY() + "\n");
            }

            // Grava os Safe Havens
            writer.write(tabuleiro.getSafeHavens().size() + "\n");
            for (SafeHaven safeHaven : tabuleiro.getSafeHavens()) {
                writer.write(safeHaven.getX() + " : " + safeHaven.getY() + "\n");
            }
        }
    }





    public List<Integer> getIdsInSafeHaven() {
        List<Integer> idsInSafeHaven = new ArrayList<>();

        // Percorre todos os Safe Havens existentes
        for (SafeHaven safeHaven : SafeHaven.getSafeHavens()) {
            // Para cada Safe Haven, obtém todas as criaturas dentro dele
            List<Creature> criaturas = safeHaven.getCriaturasDentro();
            // Para cada criatura, adiciona seu ID à lista
            for (Creature creature : criaturas) {
                idsInSafeHaven.add(creature.getId());
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
