package pt.ulusofona.lp2.thenightofthelivingdeisi;

public class PistolaWaltherPPK extends Equipamento {
    private int balas = 3;

    public PistolaWaltherPPK(int id, int tipo ,int x, int y) {
        super(id,2,"Pistola Walther PPK", x, y);
    }

    @Override
    public boolean executarAcao(Creature atacante, Creature alvo) {
        if (balas > 0 && alvo.isZombie()) {
            balas--;
            return true;

        }
        return false;
    }

    public boolean temBalas() {
        return balas > 0;
    }

    public void gastarBala() {
        if (balas > 0) {
            balas--;
        }
    }

    public int getBalas() {
        return balas;
    }

    @Override
    public String getInfo() {
        return balas + " balas";
    }

    @Override
    public String toString() {
        return nome + " (" + balas + " balas restantes)";
    }

    boolean isOfType(int id) {
        return id == 2;
    }
}
