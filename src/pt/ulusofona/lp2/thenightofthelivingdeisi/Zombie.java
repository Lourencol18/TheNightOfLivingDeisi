package pt.ulusofona.lp2.thenightofthelivingdeisi;

public abstract class Zombie extends Creature {
    public Zombie(int id, String nome, int x, int y) {
        super(id, nome, x, y);
    }

    @Override
    public String getTipo() {
        return "Zombie";
    }
}