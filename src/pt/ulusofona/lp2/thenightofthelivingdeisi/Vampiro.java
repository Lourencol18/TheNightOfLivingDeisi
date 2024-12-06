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
        contadorEquipamentos++; // Incrementa o contador de destruições
    }


    @Override
    public boolean podePegarEquipamento(Equipamento equipamento) {
        return false;
    }

    @Override
    public boolean podeMover(int xO, int yO, int xD, int yD, boolean isDay) {
        // Pode mover 1 casa em qualquer direção
        int dx = Math.abs(xD - xO);
        int dy = Math.abs(yD - yO);
        return dx <= 1 && dy <= 1;
    }

    @Override
    public boolean podeMoverParaComEquipamento(Equipamento equipamento) {
        return true;
    }


    @Override
    public String getInfoAsString() {
        return id + " | Vampiro | " + nome + " @ (" + x + ", " + y + ")";
    }


}