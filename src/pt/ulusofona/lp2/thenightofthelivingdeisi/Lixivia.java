package pt.ulusofona.lp2.thenightofthelivingdeisi;

public class Lixivia extends Equipamento {
    private double litros = 1.0;

    public Lixivia(int id, int tipo ,int x, int y) {
        super(id, 3,"Lixívia", x, y);
    }

    @Override
    public boolean executarAcao(Creature atacante, Creature alvo) {
        if (litros > 0.3) {
            litros -= 0.3;
        } else {
            litros = 0.0;
             // Se litros acabarem, humano vira zumbi
        }
        litros = Math.round(litros * 10.0) / 10.0; // Arredonda para 1 casa decimal
        return false;
    }

    public boolean temLitros() {
        return litros > 0.0;
    }

    @Override
     public String toString() {
        return nome + " (" + litros + " L restantes)";
    }

    public double getLitros() {
        return litros;
    }

    @Override
    public String getInfo() {
        return litros + " litros";
    }

    boolean isOfType(int id) {
        return id == -3;
    }
}
