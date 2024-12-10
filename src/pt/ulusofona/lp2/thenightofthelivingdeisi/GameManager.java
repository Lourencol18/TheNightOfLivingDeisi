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
   private static boolean dia = true;
   private boolean terminado = false;
   private int turnoSemEventos = 0;
    private int numSafeHavens = 0;
    private int turnosSemEventos = 0; // Contador de turnos sem eventos
    private boolean zumbiMorto = false;
    private boolean humanoTransformado = false;

    public void loadGame(File file) throws InvalidFileException, FileNotFoundException {
        tabuleiro = null;
        equipaInicial = -1;
        equipaAtual = -1;
        personagens.clear();
        equipamentos.clear();
        turnoAtual = equipaInicial;
        SafeHaven.getSafeHavens().clear();
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
                            continue; // Ignora Safe Havens fora dos limites
                        }

                        // Cria um novo SafeHaven
                        SafeHaven safeHaven = new SafeHaven(x, y);
                        // Adiciona ao tabuleiro
                        tabuleiro.adicionarSafeHaven(x, y);
                        // Registra no conjunto estático de SafeHavens
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
        String[] Jogador = new String[7];
        String team = "";

        // Primeiro verifica nos SafeHavens
        for (SafeHaven safeHaven : SafeHaven.getSafeHavens()) {
            for (Creature creature : safeHaven.getCriaturasDentro()) {
                if (creature.getId() == id) {
                    // Preenche informações para criaturas no SafeHaven
                    if (creature.getEquipa() == 20) {
                        team = "Humano";
                    } else if (creature.getEquipa() == 10) {
                        team = creature.isTransformed() ? "Zombie (Transformado)" : "Zombie";
                    }

                    Jogador[0] = String.valueOf(creature.getId());
                    Jogador[1] = creature.getTipoCriatura();
                    Jogador[2] = team;
                    Jogador[3] = creature.getNome();
                    // Para criaturas no SafeHaven, coordenadas são null
                    Jogador[4] = null;
                    Jogador[5] = null;
                    Jogador[6] = null;
                    return Jogador;
                }
            }
        }

        // Se não encontrou no SafeHaven, procura nas criaturas no tabuleiro
        for (Creature creature : personagens) {
            if (creature.getId() == id) {
                if (creature.getEquipa() == 20) {
                    team = "Humano";
                } else if (creature.getEquipa() == 10) {
                    team = creature.isTransformed() ? "Zombie (Transformado)" : "Zombie";
                }

                Jogador[0] = String.valueOf(creature.getId());
                Jogador[1] = creature.getTipoCriatura();
                Jogador[2] = team;
                Jogador[3] = creature.getNome();
                Jogador[4] = String.valueOf(creature.getX());
                Jogador[5] = String.valueOf(creature.getY());
                Jogador[6] = null;
                return Jogador;
            }
        }

        // Caso não encontre a criatura
        Jogador[0] = "Criatura não encontrada";
        return Jogador;
    }




    public String getCreatureInfoAsString(int id) {
        // Primeiro verifica nas criaturas dentro dos SafeHavens
        for (SafeHaven safeHaven : SafeHaven.getSafeHavens()) {
            for (Creature creature : safeHaven.getCriaturasDentro()) {
                if (creature.getId() == id) {
                    StringBuilder info = new StringBuilder();
                    info.append(creature.getId())
                            .append(" | ")
                            .append(creature.getTipoCriatura())
                            .append(" | ")
                            .append(creature.isHuman() ? "Humano" : "Zombie")
                            .append(" | ")
                            .append(creature.getNome())
                            .append(" | +")
                            .append(creature.getContadorEquipamentos())
                            .append(" @ Safe Haven");
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
                // Identifica o tipo numérico com base na classe do equipamento
                String tipoNumerico;
                if (equipment instanceof EscudoDeMadeira) {
                    tipoNumerico = "0";
                } else if (equipment instanceof EspadaSamurai) {
                    tipoNumerico = "1";
                } else if (equipment instanceof PistolaWaltherPPK) {
                    tipoNumerico = "2";
                } else if (equipment instanceof Lixivia) {
                    tipoNumerico = "3";
                } else {
                    tipoNumerico = "-1"; // Tipo desconhecido
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

        // Se não encontrar o equipamento, retorna null
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

        // Lógica específica para o cão
        if (creatureToMove.getTipoCriatura().equals("Cão")) {
            if (!creatureToMove.podeMover(xO, yO, xD, yD, isDay())) {
                return false;
            }
        }

        // Verifica se é a vez da equipe correta
        boolean turnoParaHumanos = equipaAtual == 20;
        if ((turnoParaHumanos && !creatureToMove.isHuman()) || (!turnoParaHumanos && !creatureToMove.isZombie())) {
            return false;
        }

        // Regras específicas para idosos
        if (creatureToMove instanceof Idoso) {
            if (creatureToMove.isHuman()) {
                // Idoso humano só pode se mover durante o turno dos humanos e de dia
                if (!turnoParaHumanos || !isDay()) {
                    return false;
                }
            } else if (creatureToMove.isZombie()) {
                // Idoso zumbi só pode se mover durante o turno dos zumbis
                if (turnoParaHumanos) {
                    return false;
                }
            }
            // Verifica se o idoso está carregando um equipamento
            if (creatureToMove.getEquipamentoAtual() != null) {
                Equipamento equipamentoAtual = creatureToMove.getEquipamentoAtual();

                // Deixa o equipamento na posição original antes de mover o idoso
                equipamentoAtual.setX(xO);
                equipamentoAtual.setY(yO);

                // Adiciona o equipamento à lista de equipamentos no tabuleiro
                equipamentos.add(equipamentoAtual);

                // Remove o equipamento do idoso
                creatureToMove.soltarEquipamento();
            }
        }

        // Verifica se o vampiro está tentando se mover de dia
        if (creatureToMove instanceof Vampiro && isDay()) {
            return false;
        }

        // Verifica se o movimento é válido
        if (!creatureToMove.podeMover(xO, yO, xD, yD, isDay())) {
            return false;
        }
        // Verifica se a criatura é um zumbi e está tentando ir para um Safe Haven
        if (creatureToMove.isZombie() && tabuleiro.isSafeHaven(xD, yD)) {
            return false; // Movimento inválido
        }

        // Verifica se há outra criatura na posição de destino
        Creature targetCreature = null;
        for (Creature creature : personagens) {
            if (creature.getX() == xD && creature.getY() == yD) {
                targetCreature = creature;
                break;
            }
        }
        // Lógica de interação entre criaturas
        if (targetCreature != null) {
            if ((creatureToMove.isZombie() || creatureToMove instanceof Vampiro) &&
                    targetCreature.getTipoCriatura().equals("Cão")) {
                return false;
            }
            if (creatureToMove.isZombie() && targetCreature.isHuman()) {
                Equipamento equipamentoAtual = targetCreature.getEquipamentoAtual();
                if (equipamentoAtual != null) {
                    if (equipamentoAtual instanceof PistolaWaltherPPK) {
                        PistolaWaltherPPK pistola = (PistolaWaltherPPK) equipamentoAtual;
                        if (pistola.temBalas()) {
                            pistola.gastarBala();
                            advanceTurn(); // Conta como jogada
                            return true;
                        }
                    }
                    if (equipamentoAtual instanceof Lixivia) {
                        Lixivia lixivia = (Lixivia) equipamentoAtual;

                        if (lixivia.getLitros() > 0.0) {
                            if (lixivia.executarAcao(creatureToMove, targetCreature)) {
                                advanceTurn(); // Conta como jogada
                                return true; // Defesa bem-sucedida
                            }
                        }

                        // Se a lixívia estiver esgotada
                        if (lixivia.getLitros() <= 0.0) {
                            int equipamentosUsados = targetCreature.getContadorEquipamentos();
                            targetCreature.transformar();
                            targetCreature.setEquipa(10);

                            targetCreature.soltarEquipamento();
                            targetCreature.incrementarEquipamentosDestruidos(equipamentosUsados);
                            humanoTransformado = true;
                            advanceTurn();
                            return true; // Transformação realizada
                        }
                    }

                    // Lógica para outros equipamentos defensivos
                    if (equipamentoAtual.getTipo() == 1 || equipamentoAtual.isDefensivo()) {
                        advanceTurn();
                        return true;
                    }
                }

                // Transformação de humano em zumbi
                int equipamentosUsados = targetCreature.getContadorEquipamentos();
                targetCreature.transformar();
                targetCreature.setEquipa(10);

                targetCreature.soltarEquipamento();
                targetCreature.incrementarEquipamentosDestruidos(equipamentosUsados);
                humanoTransformado = true;
                advanceTurn();

                return true;
            }

            // Humano com espada mata zumbi
            if (creatureToMove.isHuman() && targetCreature.isZombie()) {
                Equipamento equipamentoAtual = creatureToMove.getEquipamentoAtual();
                if (equipamentoAtual != null && equipamentoAtual.getTipo() == 1) { // Tipo 1: Espada Samurai
                    personagens.remove(targetCreature);
                    zumbiMorto = true; // Nova linha para marcar morte do zumbi
                    creatureToMove.setX(xD);
                    creatureToMove.setY(yD);
                    advanceTurn();
                    return true;
                }

                // Lógica para pistola (Tipo 2 ou similar)
                if (equipamentoAtual != null && equipamentoAtual instanceof PistolaWaltherPPK) {
                    PistolaWaltherPPK pistola = (PistolaWaltherPPK) equipamentoAtual;
                    if (pistola.temBalas()) {
                        pistola.gastarBala(); // Consome uma bala
                        personagens.remove(targetCreature); // Remove o zumbi do jogo
                        zumbiMorto = true; // Nova linha para marcar morte do zumbi
                        creatureToMove.setX(xD); // Move o humano para a posição do zumbi
                        creatureToMove.setY(yD);
                        advanceTurn();
                        return true;
                    }
                }
                // Caso o humano não tenha espada, não consegue atacar o zumbi
                return false;
            }

            return false; // Movimento inválido
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
            return false;
        }

        // Atualiza a posição da criatura
        creatureToMove.setX(xD);
        creatureToMove.setY(yD);

        // Interação com equipamentos
        if (creatureToMove.isHuman() && equipamentoParaInteragir != null) {
            Equipamento equipamentoAtual = creatureToMove.getEquipamentoAtual();

            if (equipamentoAtual != null) {
                // Deixa o equipamento atual na posição original
                equipamentoAtual.setX(xO); // Define a posição do equipamento para a posição inicial do humano
                equipamentoAtual.setY(yO);
                equipamentos.add(equipamentoAtual); // Adiciona o equipamento ao tabuleiro
                creatureToMove.soltarEquipamento(); // Solta o equipamento atual
            }

            // Pega o novo equipamento
            if (creatureToMove.podePegarEquipamento(equipamentoParaInteragir)) {
                creatureToMove.pegarEquipamento(equipamentoParaInteragir); // Atualiza o equipamento da criatura
                equipamentos.remove(equipamentoParaInteragir); // Remove o novo equipamento do tabuleiro
            }
        }
        if (creatureToMove.isZombie() && equipamentoParaInteragir != null) {
            // Zumbi destrói o equipamento
            creatureToMove.destruirEquipamento();

            // Incrementa o contador de equipamentos destruídos
            creatureToMove.incrementarEquipamentosDestruidos(1);

            // Remove o equipamento do tabuleiro
            equipamentos.remove(equipamentoParaInteragir);
        }

        if (creatureToMove.isHuman() && tabuleiro.isSafeHaven(xD, yD)) {
            for (SafeHaven safeHaven : tabuleiro.getSafeHavens()) {
                if (safeHaven.getX() == xD && safeHaven.getY() == yD) {
                    // Mantém as coordenadas originais antes de entrar no SafeHaven
                    creatureToMove.setX(xO);
                    creatureToMove.setY(yO);

                    // Adiciona a criatura ao Safe Haven
                    safeHaven.entrar(creatureToMove);

                    // Remove a criatura da lista de personagens
                    personagens.remove(creatureToMove);

                    advanceTurn();
                    return true;
                }
            }
        }

        advanceTurn();
        return true;
    }


    private void advanceTurn() {
        turnoAtual++; // Avança o turno

        // Alterna entre turnos de dia e noite
        dia = ((turnoAtual + 1) / 2) % 2 == 0;

        // Alterna a equipe
        equipaAtual = (equipaAtual == 10) ? 20 : 10;

        // Verifica se houve transformações ou mortes significativas
        boolean houveEventos = humanoTransformado || zumbiMorto;

        // Atualiza o contador de turnos sem eventos
        if (houveEventos) {
            turnosSemEventos = 0; // Reseta o contador
            humanoTransformado = false; // Reseta a flag de transformação
            zumbiMorto = false; // Reseta a flag de morte de zumbi
        } else {
            turnosSemEventos++; // Incrementa o contador se não houve evento
        }
    }




    public boolean gameIsOver() {
        // 1. Verifica se passaram exatamente 8 turnos sem eventos significativos
        if (turnosSemEventos >= 8) {
            return true; // O jogo termina se não houver eventos por 8 turnos consecutivos
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

        // Adiciona o texto do número de turnos e o número em linhas separadas
        resultados.add("Nr. de turnos terminados:");
        resultados.add(String.valueOf(turnoAtual + 1)); // Adiciona o número do turno em uma linha separada
        resultados.add("");

        // Separador para os vivos
        resultados.add("OS VIVOS");
        for (Creature creature : personagens) {
            if (creature.isHuman()) { // Tipo 1 representa humano
                resultados.add(creature.getId() + " " + creature.getNome());
            }
        }
// Adiciona humanos que estão no Safe Haven
        for (SafeHaven safeHaven : SafeHaven.getSafeHavens()) {
            for (Creature creature : safeHaven.getCriaturasDentro()) {
                if (creature.isHuman()) {
                    resultados.add(creature.getId() + " " + creature.getNome());
                }
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
