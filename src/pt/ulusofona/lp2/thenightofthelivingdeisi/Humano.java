package pt.ulusofona.lp2.thenightofthelivingdeisi;

public abstract class Humano extends Creature {
    public Humano(int id, String nome, int x, int y) {
        super(id, nome, x, y);
    }

    @Override
    public String getTipo() {
        return "Humano";
    }
}
