package pt.ulusofona.lp2.thenightofthelivingdeisi;

import java.util.ArrayList;

public class Equipamento {
    int id;
    int tipo;
    ArrayList<Integer> coordenadas;

    public Equipamento(int id,int tipo, ArrayList<Integer> coordenadas) {
        this.id = id;
        this.tipo = tipo;
        this.coordenadas = coordenadas;
    }

    @Override
    public String toString() { // id tanto faz tipo 1 espada e 0 escudo
        if (tipo == 1) {
            return id + "Espada Samurai" + " @ (" + coordenadas.get(0) + ", " + coordenadas.get(1) + ")";
        }
            return id + "Escudo" + " @ (" + coordenadas.get(0) + ", " + coordenadas.get(1) + ")";
    }
}
