package pt.ulusofona.lp2.thenightofthelivingdeisi;

import java.util.ArrayList;

public class Creature {
        int id;
        int tipoCriatura;
        String nome;;
        ArrayList<Integer> coordenadas;
        Equipamento equipamento;

        public Creature(int id, int tipoCriatura, String nome, ArrayList<Integer> coordenadas) {
            this.id = id;
            this.tipoCriatura = tipoCriatura;
            this.nome = nome;
            this.coordenadas = coordenadas;
        }
        public void destruirEquipamento(){

        }
        public void apanhaEquipamento(){

        }

    @Override
    public String toString() {
        String equipamentoStr = (equipamento != null) ? equipamento.toString() : "-";
            if (tipoCriatura == 0){
                return id + " | " + "Humano" + " | " + nome + " | " + equipamentoStr + " @ (" + coordenadas.get(0) + ", " + coordenadas.get(1) + ")";
            }
                return id + " | " + "Zombie" + " | " + nome + " | " + equipamentoStr + " @ (" + coordenadas.get(0) + ", " + coordenadas.get(1) + ")";
}}
