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
            System.out.println("Iniciando a leitura do arquivo...");

            // Lê as dimensões do tabuleiro
            if (!scanner.hasNextLine()) {
                System.out.println("Erro: Dimensões do tabuleiro ausentes.");
                return false;
            }
            String[] tamanho = scanner.nextLine().split(" ");
            if (tamanho.length != 2) {
                System.out.println("Erro: Dimensões do tabuleiro inválidas.");
                return false;
            }
            int width = Integer.parseInt(tamanho[0]);
            int height = Integer.parseInt(tamanho[1]);
            tabuleiro = new Tabuleiro(width, height);
            System.out.println("Dimensões do tabuleiro lidas com sucesso: " + width + " x " + height);

            // Lê a equipe inicial
            if (!scanner.hasNext()) {
                System.out.println("Erro: Equipe inicial ausente.");
                return false;
            }
            equipaInicial = Integer.parseInt(scanner.next());
            if (equipaInicial != 0 && equipaInicial != 1) {
                System.out.println("Erro: Equipe inicial inválida.");
                return false;
            }
            System.out.println("Equipe inicial lida com sucesso: " + equipaInicial);

            // Lê o número de criaturas
            if (!scanner.hasNext()) {
                System.out.println("Erro: Número de criaturas ausente.");
                return false;
            }
            int numCreatures = Integer.parseInt(scanner.next());
            if (numCreatures < 0) {
                System.out.println("Erro: Número de criaturas inválido.");
                return false;
            }
            System.out.println("Número de criaturas lido com sucesso: " + numCreatures);

            personagens.clear();

            // Lê cada criatura
            for (int i = 0; i < numCreatures; i++) {
                String linhaCriatura;

                // Lê até encontrar uma linha não vazia
                do {
                    if (!scanner.hasNextLine()) {
                        System.out.println("Erro: Dados da criatura " + i + " ausentes.");
                        return false;
                    }
                    linhaCriatura = scanner.nextLine().trim();
                } while (linhaCriatura.isEmpty()); // Continua até encontrar uma linha não vazia

                System.out.println("Linha da criatura " + i + ": " + linhaCriatura);  // Imprime a linha exata da criatura para depuração

                String[] criaturaData = linhaCriatura.split(" : ");
                if (criaturaData.length != 5) {
                    System.out.println("Erro: Dados da criatura " + i + " incompletos ou em formato incorreto. Partes encontradas: " + criaturaData.length);
                    return false;
                }

                int id = Integer.parseInt(criaturaData[0].trim());
                int tipoCriatura = Integer.parseInt(criaturaData[1].trim());
                String nome = criaturaData[2].trim();
                int x = Integer.parseInt(criaturaData[3].trim());
                int y = Integer.parseInt(criaturaData[4].trim());

                if (x < 0 || x >= tabuleiro.width || y < 0 || y >= tabuleiro.height) {
                    System.out.println("Erro: Coordenadas da criatura " + i + " fora dos limites.");
                    return false;
                }

                personagens.add(new Creature(id, tipoCriatura, nome, x, y));
                System.out.println("Criatura " + nome + " adicionada com sucesso: ID=" + id + ", Tipo=" + tipoCriatura + ", Coordenadas=(" + x + ", " + y + ")");
            }




            // Lê o número de equipamentos
            equipamentos.clear();
            if (!scanner.hasNext()) {
                System.out.println("Erro: Número de equipamentos ausente.");
                return false;
            }
            int numEquipments = Integer.parseInt(scanner.next());
            if (numEquipments < 0) {
                System.out.println("Erro: Número de equipamentos inválido.");
                return false;
            }
            System.out.println("Número de equipamentos lido com sucesso: " + numEquipments);

            // Lê cada equipamento
            for (int i = 0; i < numEquipments; i++) {
                String linhaEquipamento;

                // Lê até encontrar uma linha não vazia
                do {
                    if (!scanner.hasNextLine()) {
                        System.out.println("Erro: Dados do equipamento " + i + " ausentes.");
                        return false;
                    }
                    linhaEquipamento = scanner.nextLine().trim();
                } while (linhaEquipamento.isEmpty()); // Continua até encontrar uma linha não vazia

                System.out.println("Linha do equipamento " + i + ": " + linhaEquipamento);  // Imprime a linha exata do equipamento para depuração

                String[] equipamentoData = linhaEquipamento.split(" : ");
                // Verifica se há exatamente 4 elementos na linha
                if (equipamentoData.length != 4) {
                    System.out.println("Erro: Dados do equipamento " + i + " incompletos ou em formato incorreto. Partes encontradas: " + equipamentoData.length);
                    return false;
                }

                int id = Integer.parseInt(equipamentoData[0].trim());
                int tipo = Integer.parseInt(equipamentoData[1].trim());
                int x = Integer.parseInt(equipamentoData[2].trim());
                int y = Integer.parseInt(equipamentoData[3].trim());

                if (x < 0 || x >= tabuleiro.width || y < 0 || y >= tabuleiro.height) {
                    System.out.println("Erro: Coordenadas do equipamento " + i + " fora dos limites.");
                    return false;
                }

                equipamentos.add(new Equipamento(id, tipo, x, y));
                System.out.println("Equipamento adicionado com sucesso: ID=" + id + ", Tipo=" + tipo + ", Coordenadas=(" + x + ", " + y + ")");
            }


            System.out.println("Arquivo carregado com sucesso.");
            return true;
        } catch (FileNotFoundException | NumberFormatException e) {
            e.printStackTrace();
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
