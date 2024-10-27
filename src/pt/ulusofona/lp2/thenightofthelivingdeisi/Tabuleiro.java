package pt.ulusofona.lp2.thenightofthelivingdeisi;

import java.util.ArrayList;

public class Tabuleiro {
     int width;
    int height;
    int equipaInicial;
    boolean dia;
    boolean vazio;

    public Tabuleiro(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ArrayList<Integer> getTamanho() {
        ArrayList<Integer> tamanho = new ArrayList<>();
        tamanho.add(width);
        tamanho.add(height);
        return tamanho;
    }

    public Boolean getDia() {
        return dia;
    }

    public int getTurno() {
        return equipaInicial;
    }



    public boolean isVazio() {
        return vazio;
    }

    public boolean estaVazio() {
        return true;
    }


}
