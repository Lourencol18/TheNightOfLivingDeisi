package pt.ulusofona.lp2.thenightofthelivingdeisi;

public class Vampiro extends Creature {

    public Vampiro(int id, String nome, int x, int y) {
        super(id, nome, x, y);
    }

    @Override
    public String getTipoCriatura() {
        return "Vampiro";
    }

    @Override
    public String getTipo() {
        return "Zombie"; // Vampiro sempre será um tipo de zumbi
    }




}