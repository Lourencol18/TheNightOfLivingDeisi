package pt.ulusofona.lp2.thenightofthelivingdeisi;

public class ZombieTransformado extends Zombie {
    private String nomeAnterior;

    public ZombieTransformado(int id, String nomeAnterior, int x, int y) {
        super(id, "Transformado: " + nomeAnterior, x, y);
        this.nomeAnterior = nomeAnterior;
    }

    public String getNomeAnterior() {
        return nomeAnterior;
    }

    @Override
    public String getTipoCriatura() {
        return "Zombie (Transformado)";
    }


}