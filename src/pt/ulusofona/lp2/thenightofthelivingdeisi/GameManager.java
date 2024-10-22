package pt.ulusofona.lp2.thenightofthelivingdeisi;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class GameManager {

    public boolean loadGame(File file){
    return true;
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
