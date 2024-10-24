package pt.ulusofona.lp2.thenightofthelivingdeisi;

import java.util.ArrayList;

public class Humano {
    int id;
    String TipoCriatura;
    String nome;
    ArrayList<Integer> coordenadas;
    Equipamento equipamento;

    public Humano(int id, String tipoCriatura, ArrayList<Integer> coordenadas, String nome, Equipamento equipamento) {
        this.id = id;
        TipoCriatura = tipoCriatura;
        this.coordenadas = coordenadas;
        this.nome = nome;
        this.equipamento = equipamento;
    }

    public apanharEquipamento() {

    }
}
