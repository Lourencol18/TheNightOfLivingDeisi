package pt.ulusofona.lp2.thenightofthelivingdeisi;

public class Vampiro extends Creature {

    private boolean isDay;

    public Vampiro(int id, String nome, int x, int y, int equipa) {
        super(id, nome, x, y, equipa);
    }

    @Override
    public String getTipoCriatura() {
        return "Vampiro";
    }

    @Override
    public String getTipo() {
        return "Zombie"; // Vampiro sempre será um tipo de zumbi
    }

    @Override
    public void destruirEquipamento() {
        contadorEquipamentos++;
    }

    @Override
    public boolean podeMover(int xO, int yO, int xD, int yD) {
        // Vampiros só podem se mover à noite
        if (isDay) {
            return false; // Não pode se mover durante o dia
        }

        int distanciaX = Math.abs(xD - xO);
        int distanciaY = Math.abs(yD - yO);

        // Movimento máximo de 1 casa em qualquer direção
        return distanciaX <= 1 && distanciaY <= 1;
    }

    @Override
    public boolean podeTerEquipamento(int equipmentTypeId) {
        return false;
    }

    @Override
    public String getInfoAsString() {
        return id + " | Vampiro | " + nome + " @ (" + x + ", " + y + ")";
    }


}