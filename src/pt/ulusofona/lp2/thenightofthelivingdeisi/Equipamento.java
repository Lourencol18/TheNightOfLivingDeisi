package pt.ulusofona.lp2.thenightofthelivingdeisi;

public class Equipamento {
    int id;
    int tipo;
    int x;
    int y;

    public Equipamento(int id, int tipo, int x, int y) {
        this.id = id;
        this.tipo = tipo;
        this.x = x;
        this.y = y;
    }
    // Na classe Creature
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    public int getId() {
        return id;
    }


    @Override
    public String toString() {
        if (tipo == 1) {
            return id + " Espada Samurai" + " @ (" + x + ", " + y + ")";
        }
        return id + " Escudo" + " @ (" + x + ", " + y + ")";
    }


}
