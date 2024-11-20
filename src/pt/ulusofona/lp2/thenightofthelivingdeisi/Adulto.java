package pt.ulusofona.lp2.thenightofthelivingdeisi;

public class Adulto extends Creature {
    private boolean isZombie;

    public Adulto(int id, String nome, int x, int y, boolean isZombie) {
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
        return "Adulto";
    }

    @Override
    public String getTipo() {
        return isZombie ? "Zombie" : "Humano";
    }


}