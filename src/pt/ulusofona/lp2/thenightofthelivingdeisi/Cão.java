package pt.ulusofona.lp2.thenightofthelivingdeisi;

public class Cão extends Creature {


    public Cão(int id, String nome, int x, int y, int equipa) {
        super(id, nome, x, y, equipa);
    }

    @Override
    public String getTipoCriatura() {
        return "Cão";
    }

    @Override
    public String getTipo() {
        return "Humano"; // O cão é sempre considerado do tipo humano no jogo
    }

    @Override
    public void destruirEquipamento() {
    }

    @Override
    public boolean podeMover(int xO, int yO, int xD, int yD) {
        int distanciaX = Math.abs(xD - xO);
        int distanciaY = Math.abs(yD - yO);

        // Apenas movimento horizontal ou vertical, máximo de 2 casas
        return (distanciaX == 0 && distanciaY <= 2) || (distanciaY == 0 && distanciaX <= 2);
    }

    @Override
    public boolean podeTerEquipamento(int equipmentTypeId) {
        return false;
    }

    @Override
    public String getInfoAsString() {
        return id + " | Cão | " + nome + " @ (" + x + ", " + y + ")";
    }

}