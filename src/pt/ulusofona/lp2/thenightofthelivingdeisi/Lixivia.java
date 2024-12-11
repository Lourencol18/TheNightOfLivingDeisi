package pt.ulusofona.lp2.thenightofthelivingdeisi;

public class Lixivia extends Equipamento {
    private double litros = 1.0;

    public Lixivia(int id, int tipo ,int x, int y) {
        super(id, 3,"Lixívia", x, y);
    }

    @Override
    public boolean executarAcao(Creature atacante, Creature alvo) {
        if (litros > 0.0) {
            // Consome 0.3 litros por uso
            litros -= 0.3;
            if (litros < 0.0) {
                litros = 0.0; // Garante que não fique negativo
            }
            litros = Math.round(litros * 10.0) / 10.0; // Arredonda para 1 casa decimal
            return true; // Defesa bem-sucedida
        } else {
            return false; // Lixívia esgotada, ataque passa
        }
    }





    public double getLitros() {
        return litros;
    }

    @Override
    public String getInfo() {
        return litros + " litros";
    }


    @Override
    public boolean isDefensivo() {
        return true;
    }
}
