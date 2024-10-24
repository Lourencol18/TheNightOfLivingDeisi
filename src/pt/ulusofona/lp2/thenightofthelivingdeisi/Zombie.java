package pt.ulusofona.lp2.thenightofthelivingdeisi;

import java.util.ArrayList;

public class Zombie {
    int id;
    String TipoCriatura;
    String nome;
    ArrayList<Integer> coordenadas;
    Equipamento equipamento;

    public Zombie(int id, String tipoCriatura, String nome, ArrayList<Integer> coordenadas, Equipamento equipamento) {
        this.id = id;
        TipoCriatura = tipoCriatura;
        this.nome = nome;
        this.coordenadas = coordenadas;
        this.equipamento = equipamento;
    }
    public void destruirEquipamento(){

    }
}