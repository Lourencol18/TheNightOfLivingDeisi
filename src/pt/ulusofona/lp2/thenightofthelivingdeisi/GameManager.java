package pt.ulusofona.lp2.thenightofthelivingdeisi;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class GameManager {
     Tabuleiro tabuleiro ;
    int equipaInicial;
    int equipaAtual;
    ArrayList<Equipamento> equipamentos = new ArrayList<>();
    ArrayList<Creature> personagens = new ArrayList<>();
    int turno = 0;
    boolean dia = true;
    boolean terminado = false;

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

                // Lê até encontrar uma linha não vazia
                do {
                    if (!scanner.hasNextLine()) {
                        return false;
                    }
                    linhaCriatura = scanner.nextLine().trim();
                } while (linhaCriatura.isEmpty());

                String[] criaturaData = linhaCriatura.split(" : ");
                if (criaturaData.length != 5) {
                    return false;
                }

                try {
                    int id = Integer.parseInt(criaturaData[0].trim());
                    int tipoCriatura = Integer.parseInt(criaturaData[1].trim());
                    String nome = criaturaData[2].trim();
                    int x = Integer.parseInt(criaturaData[3].trim());
                    int y = Integer.parseInt(criaturaData[4].trim());

                    if (x < 0 || x >= tabuleiro.width || y < 0 || y >= tabuleiro.height) {
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
            // Lê cada equipamento
            for (int i = 0; i < numEquipments; i++) {
                String linhaEquipamento;

                // Lê até encontrar uma linha não vazia
                do {
                    if (!scanner.hasNextLine()) {
                        return false;
                    }
                    linhaEquipamento = scanner.nextLine().trim();
                } while (linhaEquipamento.isEmpty());

                String[] equipamentoData = linhaEquipamento.split(" : ");
                if (equipamentoData.length != 4) {
                    return false;
                }

                try {
                    int id = Integer.parseInt(equipamentoData[0].trim());
                    int tipo = Integer.parseInt(equipamentoData[1].trim());
                    int x = Integer.parseInt(equipamentoData[2].trim());
                    int y = Integer.parseInt(equipamentoData[3].trim());

                    if (x < 0 || x >= tabuleiro.width || y < 0 || y >= tabuleiro.height) {
                        return false;
                    }

                    equipamentos.add(new Equipamento(id, tipo, x, y));
                } catch (NumberFormatException e) {
                    return false;
                }
            }

            return true;
        } catch (FileNotFoundException | NumberFormatException e) {
            return false;
        }
    }


    public int[] getWorldSize() {
        return new int[]{tabuleiro.getHeight(), tabuleiro.getWidth()};
    }




    public int getInitialTeamId(){
        return equipaInicial;
    }

    public int getCurrentTeamId(){
        return 0;
    }

    public boolean isDay(){
        return dia;

    }

    public String getSquareInfo(int x, int y) {
        // Verifica se há uma criatura na posição
        for (Creature creature : personagens) {
            if (creature.getX() == x && creature.getY() == y) {
                String tipo = (creature.getTipo() == 1) ? "H" : "Z";  // 0 = Humano, 1 = Zumbi
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
                        "null"                               // PNG
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
                        equipamentoStr + " @ (" + creature.getX() + ", " + creature.getY() + ")";
            }
        }
        return "Criatura não encontrada";
    }


    public String[] getEquipmentInfo(int id) {
        for (Equipamento equipment : equipamentos) {
            if (equipment.getId() == id) {
                return new String[]{
                        String.valueOf(equipment.getId()),    // ID
                        String.valueOf(equipment.getTipo()),  // Tipo
                        String.valueOf(equipment.getX()),     // Posição X
                        String.valueOf(equipment.getY()),     // Posição Y
                        "null"                                // PNG
                };
            }
        }
        return null; // Se o equipamento com o ID fornecido não for encontrado
    }


    public String getEquipmentInfoAsString(int id) {
        for (Equipamento equipment : equipamentos) {
            if (equipment.getId() == id) {
                // Verifica se o equipamento foi capturado
                if (equipment.isCaptured()) {
                    return null; // Retorna null se o equipamento foi capturado
                }

                String tipoEquipamento = (equipment.getTipo() == 0) ? "Escudo de madeira" : "Espada samurai";
                return id + " | " + tipoEquipamento + " @ (" + equipment.getX() + "," + equipment.getY() + ")";
            }
        }
        return null; // Retorna null se o equipamento não for encontrado
    }



    public boolean hasEquipment(int creatureId, int equipmentTypeId){
        return true;
    }

    public boolean move(int xO, int yO, int xD, int yD){
        return true;
    }

    public boolean gameIsOver(){
        return terminado;
    }

    public ArrayList<String> getSurvivors(){
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


    public HashMap<String,String> customizeBoard(){
        return new HashMap<>();
    }


}
