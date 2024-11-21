package pt.ulusofona.lp2.thenightofthelivingdeisi;

public class AdultoH extends Creature {
    private boolean isZombie;

    public AdultoH(int id, String nome, int x, int y, int equipa, boolean isZombie) {
        super(id, nome, x, y, equipa);
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