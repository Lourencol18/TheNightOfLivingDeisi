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

            // Lê e valida as dimensões do tabuleiro a partir da linha
            if (scanner.hasNextLine()) {
                String[] tamanho = scanner.nextLine().split(" ");
                if (tamanho.length == 2) {
                    tabuleiro = new Tabuleiro(Integer.parseInt(tamanho[0]), Integer.parseInt(tamanho[0]));
                }
            } else {
                return false;
            }

            // Lê o ID da equipe inicial e valida
            String equipaInicialStr = scanner.next();
            try {
                equipaInicial = Integer.parseInt(equipaInicialStr);
                if (equipaInicial != 0 && equipaInicial != 1) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false; // Retorna false se não puder converter para int
            }


            String numCreaturesStr = scanner.next();
            int numCreatures;
            try {
                numCreatures = Integer.parseInt(numCreaturesStr);
                if (numCreatures < 0) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }


            for (int i = 0; i < numCreatures; i++) {
                if (scanner.hasNextLine()) {
                    // Lê a linha completa e divide em partes usando " : " como delimitador
                    String[] criaturaData = scanner.nextLine().split(" ");


                    // Verifica se todos os elementos necessários estão presentes
                    if (criaturaData.length < 5) {
                        return false;
                    }

                    // Extrai os dados da criatura
                    int id;
                    int tipoCriatura;
                    int x;
                    int y;
                    try {
                        id = Integer.parseInt(criaturaData[0]);
                        tipoCriatura = Integer.parseInt(criaturaData[1]);
                        x = Integer.parseInt(criaturaData[3]);
                        y = Integer.parseInt(criaturaData[4]);
                    } catch (NumberFormatException e) {
                        return false; // Retorna false se a conversão falhar
                    }

                    // Verifica se as coordenadas são válidas
                    if (x < 0 || x >= tabuleiro.width || y < 0 || y >= tabuleiro.height) {
                        return false;
                    }

                    // Constrói o nome (parte que pode conter espaços)
                    String nome = criaturaData[2];

                    // Cria uma lista de coordenadas
                    ArrayList<Integer> coordenadas = new ArrayList<>();
                    coordenadas.add(x);
                    coordenadas.add(y);

                    // Cria uma instância de Creature e adiciona ao ArrayList
                    Creature criatura = new Creature(id, tipoCriatura, nome, coordenadas, null); // Equipamento está como null inicialmente
                    personagens.add(criatura);
                } else {
                    return false; // Retorna false se não houver mais linhas para ler
                }
            }
            // Inicializa o ArrayList de equipamentos
            equipamentos = new ArrayList<>();

            // Ler e processar o número de equipamentos
            String numEquipmentsStr = scanner.next();
            int numEquipments;
            try {
                numEquipments = Integer.parseInt(numEquipmentsStr);
                if (numEquipments < 0) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false; // Retorna false se não puder converter para int
            }

            for (int i = 0; i < numEquipments; i++) {
                if (scanner.hasNextLine()) {
                    // Lê a linha completa e divide em partes usando " : " como delimitador
                    String[] equipamentoData = scanner.nextLine().split(" ");

                    // Verifica se todos os elementos necessários estão presentes
                    if (equipamentoData.length < 4) {
                        return false;
                    }

                    // Extrai os dados do equipamento
                    int id;
                    int tipo;
                    int x;
                    int y;
                    try {
                        id = Integer.parseInt(equipamentoData[0]);
                        tipo = Integer.parseInt(equipamentoData[1]);
                        x = Integer.parseInt(equipamentoData[2]);
                        y = Integer.parseInt(equipamentoData[3]);
                    } catch (NumberFormatException e) {
                        return false; // Retorna false se a conversão falhar
                    }

                    // Verifica se as coordenadas são válidas
                    if (x < 0 || x >= tabuleiro.width || y < 0 || y >= tabuleiro.height) {
                        return false;
                    }

                    // Cria uma lista de coordenadas
                    ArrayList<Integer> coordenadas = new ArrayList<>();
                    coordenadas.add(x);
                    coordenadas.add(y);

                    // Cria uma instância de Equipamento e adiciona ao ArrayList
                    Equipamento equipamento = new Equipamento(id, tipo, coordenadas);
                    equipamentos.add(equipamento);
                } else {
                    return false; // Retorna false se não houver mais linhas para ler
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
 return true;
    }

    public String getSquareInfo(int x, int y){
    return "ola";
    }

    public String[] getCreatureInfo(int id){
       return new String[]{"ola"};
    }

    public String getCreatureInfoAsString(int id){
    return "ola";
    }

    public String[] getEquipmentInfo(int id){
        return new String[]{"ola"};
    }

    public String getEquipmentInfoAsString(int id){
        return "ola";
    }

    public boolean hasEquipment(int creatureId, int equipmentTypeId){
        return true;
    }

    public boolean move(int xO, int yO, int xD, int yD){
        return true;
    }

    public boolean gameIsOver(){
        return false;
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
