package pt.ulusofona.lp2.thenightofthelivingdeisi;

public class Cão extends Creature {

    public Cão(int id, String nome, int x, int y) {
        super(id, nome, x, y);
    }

    @Override
    public String getTipoCriatura() {
        return "Cão";
    }

    @Override
    public String getTipo() {
        return "Humano"; // O cão é sempre considerado do tipo humano no jogo
    }


}