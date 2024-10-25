package pt.ulusofona.lp2.thenightofthelivingdeisi;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class GameManager {


    public boolean loadGame(File file) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);

            // Criar tabuleiro
            Tabuleiro tabuleiro = new Tabuleiro();

            // Ler dimensões do tabuleiro
            ArrayList<Integer> tamanho = new ArrayList<>();
            tamanho.add(scanner.nextInt()); // width
            tamanho.add(scanner.nextInt()); // height
            tabuleiro.tamanho = tamanho;

            if (tabuleiro.getTamanho().get(0) <= 0 || tabuleiro.getTamanho().get(1) <= 0) {
                return false;
            }

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

            // Ler cada criatura
            for (int i = 0; i < numCreatures; i++) {
                int id = scanner.nextInt();
                scanner.next(); // Ler ":"
                int team = scanner.nextInt();
                scanner.next(); // Ler ":"

                // Ler nome (pode conter espaço)
                StringBuilder nomeBuilder = new StringBuilder(scanner.next());
                String token;
                while (!(token = scanner.next()).equals(":")) {
                    nomeBuilder.append(" ").append(token);
                }
                String nome = nomeBuilder.toString();

                int x = scanner.nextInt();
                scanner.next(); // Ler ":"
                int y = scanner.nextInt();

                // Validar coordenadas
                if (x < 0 || x >= tabuleiro.getTamanho().get(0) ||
                        y < 0 || y >= tabuleiro.getTamanho().get(1)) {
                    return false;
                }

                // Criar lista de coordenadas
                ArrayList<Integer> coordenadas = new ArrayList<>();
                coordenadas.add(x);
                coordenadas.add(y);
            }

            // Ler equipamentos
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

                // Validar coordenadas
                if (x < 0 || x >= tabuleiro.getTamanho().get(0) ||
                        y < 0 || y >= tabuleiro.getTamanho().get(1)) {
                    return false;
                }
            }

            return true;

        } catch (FileNotFoundException e) {
            return false;
        } catch (Exception e) {
            return false;
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }


        public int[] getWorldSize(){


        return new int[]{0};
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
