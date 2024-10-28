package pt.ulusofona.lp2.thenightofthelivingdeisi;

public class Equipamento {
    int id;
    int tipo;
    int x;
    int y;
    boolean captured = false;

    public Equipamento(int id, int tipo, int x, int y) {
        this.id = id;
        this.tipo = tipo;
        this.x = x;
        this.y = y;
    }
    public void setCaptured(boolean captured) {
        this.captured = captured;
    }

    public boolean isCaptured() {
        return captured;
    }
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    public int getId() {
        return id;
    }
    public int getTipo() {
        return tipo;
    }



    @Override
    public String toString() {
        if (tipo == 1) {
            return id + " Espada Samurai" + " @ (" + x + ", " + y + ")";
        }
        return id + " Escudo de madeira" + " @ (" + x + ", " + y + ")";
    }


}
