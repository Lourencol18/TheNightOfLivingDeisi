package pt.ulusofona.lp2.thenightofthelivingdeisi;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class GameManager {
    Tabuleiro tabuleiro;
    int equipaInicial;
    int equipaAtual;
    ArrayList<Equipamento> equipamentos = new ArrayList<>();
    ArrayList<Creature> personagens = new ArrayList<>();
    int turno = 0;
    boolean dia = true;
    boolean terminado = false;



    public GameManager() {
    }



    public boolean loadGame(File file) {
        try (Scanner scanner = new Scanner(file)) {
            // Lê as dimensões do tabuleiro
            if (!scanner.hasNextLine()) {
                return false;
            }
            String[] tamanho = scanner.nextLine().split(" ");
            if (tamanho.length != 2) {
                return false;
            }
            int width = Integer.parseInt(tamanho[0]);
            int height = Integer.parseInt(tamanho[1]);
            tabuleiro = new Tabuleiro(width, height);

            // Lê a equipe inicial
            if (!scanner.hasNext()) {
                return false;
            }
            equipaInicial = Integer.parseInt(scanner.next());
            if (equipaInicial != 0 && equipaInicial != 1) {
                return false;
            }
            equipaAtual = equipaInicial;

            // Lê o número de criaturas
            if (!scanner.hasNext()) {
                return false;
            }
            int numCreatures = Integer.parseInt(scanner.next());
            if (numCreatures < 0) {
                return false;
            }

            personagens.clear();
            // Lê cada criatura
            for (int i = 0; i < numCreatures; i++) {
                String linhaCriatura;
                do {
                    if (!scanner.hasNextLine()) {
                        return false;
                    }
                    linhaCriatura = scanner.nextLine();
                } while (linhaCriatura.isEmpty());

                String[] criaturaData = linhaCriatura.split(" : ");
                if (criaturaData.length != 5) {
                    return false;
                }

                try {
                    int id = Integer.parseInt(criaturaData[0]);
                    int tipoCriatura = Integer.parseInt(criaturaData[1]);
                    String nome = criaturaData[2];
                    int x = Integer.parseInt(criaturaData[3]);
                    int y = Integer.parseInt(criaturaData[4]);

                    if (!tabuleiro.dentroDosLimites(x, y)) {
                        return false;
                    }

                    personagens.add(new Creature(id, tipoCriatura, nome, x, y));
                } catch (NumberFormatException e) {
                    return false;
                }
            }

            // Lê o número de equipamentos
            if (!scanner.hasNext()) {
                return false;
            }
            int numEquipments = Integer.parseInt(scanner.next());
            if (numEquipments < 0) {
                return false;
            }

            equipamentos.clear();

            // Somente lê equipamentos se o número for maior que 0
            if (numEquipments > 0) {
                for (int i = 0; i < numEquipments; i++) {
                    String linhaEquipamento;
                    do {
                        if (!scanner.hasNextLine()) {
                            return false;
                        }
                        linhaEquipamento = scanner.nextLine();
                    } while (linhaEquipamento.isEmpty());

                    String[] equipamentoData = linhaEquipamento.split(" : ");
                    if (equipamentoData.length != 4) {
                        return false;
                    }

                    try {
                        int id = Integer.parseInt(equipamentoData[0]);
                        int tipo = Integer.parseInt(equipamentoData[1]);
                        int x = Integer.parseInt(equipamentoData[2]);
                        int y = Integer.parseInt(equipamentoData[3]);

                        if (!tabuleiro.dentroDosLimites(x, y)) {
                            return false;
                        }

                        equipamentos.add(new Equipamento(id, tipo, x, y));
                    } catch (NumberFormatException e) {
                        return false;
                    }
                }
            }

            System.out.println("Nr criaturas: " + personagens.size());
            System.out.println("Nr equipamentos: " + equipamentos.size());

            return true; // Carregamento bem-sucedido
        } catch (FileNotFoundException | NumberFormatException e) {
            System.out.println("Erro ao carregar o arquivo: " + e.getMessage());
            return false; // Erro ao carregar o ficheiro
        }
    }






    public int[] getWorldSize() {
        return new int[]{tabuleiro.getHeight(), tabuleiro.getWidth()};
    }


    public int getInitialTeamId() {
        return equipaInicial;
    }

    public int getCurrentTeamId() {
        // Cada equipe joga em pares de turnos consecutivos
        int parDeTurnos = (turno / 2) % 2;
        return (parDeTurnos == 0) ? equipaInicial : (1 - equipaInicial);
    }


    public boolean isDay() {
        return dia;

    }

    public String getSquareInfo(int x, int y) {
        // Verifica se há uma criatura na posição
        for (Creature creature : personagens) {
            if (creature.getX() == x && creature.getY() == y) {
                String tipo = (creature.getTipo() == 1) ? "H" : "Z";  // 1 = Humano, 0 = Zumbi
                return tipo + ":" + creature.getId();
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
                String tipo = (creature.getTipo() == 1) ? "Humano" : "Zombie";  // 1 = Humano, 0 = Zumbi
                return new String[]{
                        String.valueOf(creature.getId()),    // ID
                        tipo,                                // Tipo
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
        for (Creature creature : personagens) {
            if (creature.getId() == id) {
                String tipo = (creature.getTipo() == 1) ? "Humano" : "Zombie";
                String equipamentoStr;

                if (creature.getEquipamento() != null) {
                    equipamentoStr = creature.getEquipamento().toString();
                } else {
                    String sinal = (creature.getTipo() == 1) ? "+" : "-";
                    equipamentoStr = sinal + creature.getContadorEquipamentos();
                }

                return creature.getId() + " | " + tipo + " | " + creature.getNome() + " | " +
                        equipamentoStr + " @ (" + creature.getX() + "," + creature.getY() + ")";
            }
        }
        return "Criatura não encontrada";
    }


    public String[] getEquipmentInfo(int id) {
        for (Equipamento equipment : equipamentos) {
            if (equipment.getId() == id) {
                // Retorne o tipo como número (0 ou 1) em vez de texto
                return new String[]{
                        String.valueOf(equipment.getId()),    // ID
                        String.valueOf(equipment.getTipo()),  // Tipo como número (0 ou 1)
                        String.valueOf(equipment.getX()),     // Posição X
                        String.valueOf(equipment.getY()),     // Posição Y
                        null                                 // PNG ou caminho do ícone, se aplicável
                };
            }
        }
        return null; // Se o equipamento com o ID fornecido não for encontrado
    }





    public String getEquipmentInfoAsString(int id) {
        for (Equipamento equipment : equipamentos) {
            if (equipment.getId() == id) {
                // Garante que o tipo de equipamento está descrito corretamente
                String tipoEquipamento = (equipment.getTipo() == 0) ? "Escudo de madeira" : "Espada samurai";
                // Formata a string exatamente como o teste espera
                return id + " | " + tipoEquipamento + " @ (" + equipment.getX() + "," + equipment.getY() + ")";
            }
        }
        return null;
    }



    public boolean hasEquipment(int creatureId, int equipmentTypeId) {
        for (Creature creature : personagens) {
            if (creature.getId() == creatureId) {

                if (creature.getTipo() == 1) {
                    return false;
                } else {
                    return creature.getEquipamentoPorTipo(equipmentTypeId) != null;
                }
            }
        }
        return false;
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

        // Verifica se é a vez da equipe correta (zumbi à noite, humano durante o dia)
        if ((!dia && creatureToMove.getTipo() == 1) || (dia && creatureToMove.getTipo() == 0)) {
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
        for (Equipamento equipment : equipamentos) {
            if (equipment.getX() == xD && equipment.getY() == yD) {
                // Zumbis não podem pegar equipamentos
                if (creatureToMove.getTipo() == 1) {
                    return false;
                }
                // Humanos podem pegar equipamentos
                creatureToMove.setEquipamento(equipment);
                equipamentos.remove(equipment);
                break;
            }
        }

        // Executa o movimento
        creatureToMove.x = xD;
        creatureToMove.y = yD;

        // Se for um movimento válido, incrementa o turno
        turno++;
        // A cada 2 turnos, muda entre dia e noite
        if (turno % 2 == 0) {
            dia = !dia;
        }

        return true;
    }



    public boolean gameIsOver() {
        if (turno >= 12){
            return true;
        }
        return false;
    }

    public ArrayList<String> getSurvivors() {
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
                + "<p><i>Agradecimentos especiais à procrastinação, sem ela, este projeto teria sido entregue a tempo.</i></p>"
                + "</center></html>";


        JLabel creditsLabel = new JLabel(creditsText, SwingConstants.CENTER);


        creditsPanel.add(creditsLabel, BorderLayout.CENTER);

        return creditsPanel;
    }


    public HashMap<String, String> customizeBoard() {
        return new HashMap<>();
    }


}
