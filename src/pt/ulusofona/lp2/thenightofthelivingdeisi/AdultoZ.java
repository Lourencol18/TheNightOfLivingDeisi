package pt.ulusofona.lp2.thenightofthelivingdeisi;

public class AdultoZ extends Creature {

    public AdultoZ(String nome, int x, int y, int equipa) {
        super(1, nome, x, y, equipa); // ID fixo como 1
    }

    @Override
    public String getTipoCriatura() {
        return "Adulto";
    }

    @Override
    public String getTipo() {
        return "Zombie";
    }

    @Override
    public void destruirEquipamento() {
        contadorEquipamentos++;
    }

    @Override
    public boolean podeMover(int xO, int yO, int xD, int yD) {
        // Permite movimento em linha reta ou diagonal até 2 casas
        int distanciaX = Math.abs(xD - xO);
        int distanciaY = Math.abs(yD - yO);
        return (distanciaX <= 2 && distanciaY <= 2);
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
