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

    public boolean loadGame(File file) {
        try (Scanner scanner = new Scanner(file)) {
            // Lê e valida as dimensões do tabuleiro
            int width = scanner.nextInt();
            int height = scanner.nextInt();

            if (width <= 0 || height <= 0) {
                return false;
            }

            // Cria e armazena o Tabuleiro na variável de instância
            tabuleiro = new Tabuleiro(width, height);

            // Ler ID da equipe inicial
            int teamId = scanner.nextInt();
            if (teamId != 0 && teamId != 1) {
                return false;
            }

            // Ler número de criaturas
            int numCreatures = scanner.nextInt();
            if (numCreatures < 0) {
                return false;
            }

            // Processa criaturas e suas coordenadas
            for (int i = 0; i < numCreatures; i++) {
                int id = scanner.nextInt();
                scanner.next(); // Ler ":"
                int team = scanner.nextInt();
                scanner.next(); // Ler ":"

                // Ler nome da criatura (pode conter espaços)
                StringBuilder nomeBuilder = new StringBuilder(scanner.next());
                String token;
                while (!(token = scanner.next()).equals(":")) {
                    nomeBuilder.append(" ").append(token);
                }
                String nome = nomeBuilder.toString();

                // Coordenadas
                int x = scanner.nextInt();
                scanner.next(); // Ler ":"
                int y = scanner.nextInt();

                // Verifica se as coordenadas são válidas
                if (x < 0 || x >= width || y < 0 || y >= height) {
                    return false;
                }
            }

            // Ler e processar equipamentos
            int numEquipments = scanner.nextInt();
            if (numEquipments < 0) {
                return false;
            }

            for (int i = 0; i < numEquipments; i++) {
                int id = scanner.nextInt();
                scanner.next(); // Ler ":"
                int tipo = scanner.nextInt();
                scanner.next(); // Ler ":"
                int x = scanner.nextInt();
                scanner.next(); // Ler ":"
                int y = scanner.nextInt();

                // Valida as coordenadas do equipamento
                if (x < 0 || x >= width || y < 0 || y >= height) {
                    return false;
                }
            }

            return true;

        } catch (FileNotFoundException e) {
            return false;
        }
    }
    public int[] getWorldSize() {
        // Usa o tabuleiro armazenado para obter as dimensões
        if (tabuleiro != null) {
            return new int[]{tabuleiro.getHeight(), tabuleiro.getWidth()};
        }
        return new int[]{0, 0}; // Caso o tabuleiro não esteja inicializado
    }




    public int getInitialTeamId(){
    return 0;
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
        return true;
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
