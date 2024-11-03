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

    public boolean loadGame(File file) {
        //  limpa listas
        tabuleiro = null;
        equipaInicial = -1;
        equipaAtual = -1;
        personagens.clear();
        equipamentos.clear();

        try (Scanner scanner = new Scanner(file)) {
            // le dimensões do tabuleiro (primeiro linhas depois colunas)
            if (!scanner.hasNextLine()) {
                return false;
            }
            String[] tamanho = scanner.nextLine().split(" ");
            if (tamanho.length != 2) {
                return false;
            }
            int height = Integer.parseInt(tamanho[0]); // Lê o número de linhas primeiro (altura)
            int width = Integer.parseInt(tamanho[1]); // Depois o número de colunas (largura)
            tabuleiro = new Tabuleiro(width, height);

            //le equipe inicial
            if (!scanner.hasNext()) {
                return false;
            }
            equipaInicial = Integer.parseInt(scanner.next());
            if (equipaInicial != 0 && equipaInicial != 1) {
                return false;
            }
            equipaAtual = equipaInicial;

            // le o numero de criaturas
            if (!scanner.hasNext()) {
                return false;
            }
            int numCreatures = Integer.parseInt(scanner.next());
            if (numCreatures < 0) {
                return false;
            }

            personagens.clear();
            // le cada criatura
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

            // le o número de equipamentos
            if (!scanner.hasNext()) {
                return false;
            }
            int numEquipments = Integer.parseInt(scanner.next());
            if (numEquipments == 0) {
                return true; //le ficheiros  sem equipamentos
            }
            if (numEquipments < 0) {
                return false;
            }

            equipamentos.clear();

            // le equipamentos se o número for maior que 0
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
        // alterna entre as equipas a cada turno
        return (turno % 2 == 0) ? equipaInicial : (1 - equipaInicial);
    }



    public boolean isDay() {
        return dia;

    }

    public String getSquareInfo(int x, int y) {
        // verifica se ha criatura na posicao
        for (Creature creature : personagens) {
            if (creature.getX() == x && creature.getY() == y) {
                String tipo = (creature.getTipo() == 1) ? "H" : "Z";  // 1 = Humano, 0 = Zumbi
                return tipo + ":" + creature.getId();
            }
        }

        // verifica se ha equipamento na posicao
        for (Equipamento equipment : equipamentos) {
            if (equipment.getX() == x && equipment.getY() == y) {
                return "E:" + equipment.getId();
            }
        }

        return ""; //  não ha nada na posicao
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
                        null                                   //PNG
                };
            }
        }
        return null; //  criatura com o ID  não for encontrada
    }


    public String getCreatureInfoAsString(int id) {
        for (Creature creature : personagens) {
            if (creature.getId() == id) {
                String tipo = (creature.getTipo() == 1) ? "Humano" : "Zombie";
                String equipamentoStr;

                if (creature.getTipo() == 1) { // para humanos
                    equipamentoStr = "+" + creature.getContadorEquipamentos();
                } else { // para zombis
                    equipamentoStr = "-" + creature.getContadorEquipamentos();
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
                // retorna o tipo como número em vez de texto
                return new String[]{
                        String.valueOf(equipment.getId()),    // ID
                        String.valueOf(equipment.getTipo()),  // Tipo como número (0 ou 1)
                        String.valueOf(equipment.getX()),     // Posição X
                        String.valueOf(equipment.getY()),     // Posição Y
                        null                                 // PNG
                };
            }
        }
        return null; // se o equipamento com o ID nao for encontrado
    }





    public String getEquipmentInfoAsString(int id) {
        for (Equipamento equipment : equipamentos) {
            if (equipment.getId() == id) {
                // faz com que o tipo de equipamento está  correto
                String tipoEquipamento = (equipment.getTipo() == 0) ? "Escudo de madeira" : "Espada samurai";
                // formata a string
                return id + " | " + tipoEquipamento + " @ (" + equipment.getX() + "," + equipment.getY() + ")";
            }
        }
        return null;
    }




    public boolean hasEquipment(int creatureId, int equipmentTypeId) {
        for (Creature creature : personagens) {
            if (creature.getId() == creatureId) {

                if (creature.getTipo() == 0) {
                    return false;
                } else {
                    return creature.getEquipamentoPorTipo(equipmentTypeId) != null;
                }
            }
        }
        return false;
    }


    public boolean move(int xO, int yO, int xD, int yD) {
        // verifica se as coordenadas estao dentro do tabuleiro
        if (xD < 0 || xD >= tabuleiro.getWidth() || yD < 0 || yD >= tabuleiro.getHeight()) {
            return false;
        }

        // encontra a criatura na posição de origem
        Creature creatureToMove = null;
        for (Creature creature : personagens) {
            if (creature.getX() == xO && creature.getY() == yO) {
                creatureToMove = creature;
                break;
            }
        }

        // se nao houver criatura na origem
        if (creatureToMove == null) {
            return false;
        }

        // determina a equipa correta com base no turno e na equipe inicial
        boolean turnoParaHumanos = (turno % 2 == 0 && equipaInicial == 1) || (turno % 2 == 1 && equipaInicial == 0);
        boolean equipeCorreta = (turnoParaHumanos && creatureToMove.getTipo() == 1) || (!turnoParaHumanos && creatureToMove.getTipo() == 0);

        // verifica se e a vez da equipa correta
        if (!equipeCorreta) {
            return false;
        }

        // verifica se ha outra criatura na casa
        for (Creature creature : personagens) {
            if (creature.getX() == xD && creature.getY() == yD) {
                return false;
            }
        }

        // verifica se o movimento e valido
        int distanciaX = Math.abs(xD - xO);
        int distanciaY = Math.abs(yD - yO);

        // permite apenas movimento em linha reta
        if (!((distanciaX == 1 && distanciaY == 0) || (distanciaX == 0 && distanciaY == 1))) {
            return false;
        }

        // verifica equipamentos na posicao final
        Equipamento equipamentoParaInteragir = null;
        for (Equipamento equipment : equipamentos) {
            if (equipment.getX() == xD && equipment.getY() == yD) {
                equipamentoParaInteragir = equipment;
                break;
            }
        }

        // executa o movimento
        creatureToMove.x = xD;
        creatureToMove.y = yD;

        // se houver equipamento na posição final
        if (equipamentoParaInteragir != null) {
            if (creatureToMove.getTipo() == 1) { // humanos tem equipamentos
                creatureToMove.apanhaequipamento(equipamentoParaInteragir); // adiciona o equipamento ao humano
                equipamentos.remove(equipamentoParaInteragir); // remove o equipamento do tabuleiro
            } else if (creatureToMove.getTipo() == 0) { // zombis destroem equipamentos
                creatureToMove.destruirEquipamento(); // incrementa o contador de destruicoes
                equipamentos.remove(equipamentoParaInteragir); // remove o equipamento do tabuleiro
            }
        }

        // incrementa o turno para alternar a equipa
        turno++;

        // a cada 2 turnos alterna entre dia e noite
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
        ArrayList<String> resultados = new ArrayList<>();

        // numero de turnos terminados
        resultados.add("Nr. de turnos terminados: " + turno);
        resultados.add("");

        // separador para os vivos
        resultados.add("OS VIVOS");
        for (Creature creature : personagens) {
            if (creature.getTipo() == 1) {
                resultados.add(creature.getId() + " " + creature.getNome());
            }
        }
        resultados.add(""); // linha em branco entre os vivos e os outros

        // separador para os outros
        resultados.add("OS OUTROS");
        for (Creature creature : personagens) {
            if (creature.getTipo() == 0) {
                resultados.add(creature.getId() + " (antigamente conhecido como " + creature.getNome() + ")");
            }
        }
        resultados.add("-----"); // separador final

        return resultados;
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
