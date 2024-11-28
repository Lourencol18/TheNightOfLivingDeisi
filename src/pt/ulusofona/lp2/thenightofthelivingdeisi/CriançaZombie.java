package pt.ulusofona.lp2.thenightofthelivingdeisi;

public class CriançaZombie extends Creature {

    public CriançaZombie(int id, String nome, int x, int y, int equipa) {
        super(0, nome, x, y, equipa); // ID fixo como 0
    }

    @Override
    public String getTipoCriatura() {
        return "Criança";
    }

    @Override
    public String getTipo() {
        return "Zombie";
    }

    @Override
    public void destruirEquipamento() {

    }

    @Override
    public boolean podeMover(int xO, int yO, int xD, int yD) {
        // Verifica se é horizontal ou vertical e se é apenas 1 casa
        int distanciaX = Math.abs(xD - xO);
        int distanciaY = Math.abs(xD - yD);
        return (distanciaX == 1 && distanciaY == 0) || (distanciaX == 0 && distanciaY == 1);
    }

    @Override
    public boolean podeTerEquipamento(int equipmentTypeId) {
        return false;
    }


    @Override
    public String toString() {
        return id + " | " + getTipoCriatura() + " | " + getTipo() + " | " + nome
                + " | Equipamentos destruídos: " + equipamentos.size() + " @ (" + x + ", " + y + ")";
    }
}