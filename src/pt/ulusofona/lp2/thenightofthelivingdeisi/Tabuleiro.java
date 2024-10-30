package pt.ulusofona.lp2.thenightofthelivingdeisi;

import java.util.ArrayList;

public class Tabuleiro {
    int width;
    int height;


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


    public boolean dentroDosLimites(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

}
