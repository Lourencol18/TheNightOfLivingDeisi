package pt.ulusofona.lp2.thenightofthelivingdeisi;

public class Idoso extends Creature {
    private boolean isZombie;

    public Idoso(int id, String nome, int x, int y, boolean isZombie) {
        super(id, nome, x, y);
        this.isZombie = isZombie;
    }

    public boolean isZombie() {
        return isZombie;
    }

    public void transformarEmZombie() {
        this.isZombie = true;
    }

    @Override
    public String getTipoCriatura() {
        return "Idoso";
    }

    @Override
    public String getTipo() {
        return isZombie ? "Zombie" : "Humano";
    }


}