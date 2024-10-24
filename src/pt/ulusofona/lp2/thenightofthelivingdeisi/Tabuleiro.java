package pt.ulusofona.lp2.thenightofthelivingdeisi;

import java.util.ArrayList;

public class Tabuleiro {
    ArrayList<Integer> tamanho;
    int rodada;
    String turno;
    Boolean  dia;
    boolean vazio;

    public ArrayList<Integer> getTamanho() {
        return tamanho;
    }

    public Boolean getDia() {
        return dia;
    }

    public String getTurno() {
        return turno;
    }

    public int getRodada() {
        return rodada;
    }

    public boolean isVazio() {
        return vazio;
    }
    public boolean estaVazio(){
        return true;
    }
}
