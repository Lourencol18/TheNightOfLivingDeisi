package pt.ulusofona.lp2.thenightofthelivingdeisi;

import java.util.ArrayList;

public class Creature {
        int id;
        int tipoCriatura;
        String nome;
        ArrayList<Integer> coordenadas;
        Equipamento equipamento;

        public Creature(int id, int tipoCriatura, String nome, ArrayList<Integer> coordenadas, Equipamento equipamento) {
            this.id = id;
            this.tipoCriatura = tipoCriatura;
            this.nome = nome;
            this.coordenadas = coordenadas;
            this.equipamento = equipamento;
        }
        public void destruirEquipamento(){

        }
        public void apanhaEquipamento(){

        }
}
