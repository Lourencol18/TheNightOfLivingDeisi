package pt.ulusofona.lp2.thenightofthelivingdeisi;

public class Cao extends Creature {


    private boolean isHuman;

    public Cao(int id, String nome, int x, int y, int equipa, boolean isHuman) {
        super(id, nome, x, y, equipa); // Chama o construtor da classe pai (Creature)
        this.isHuman = isHuman;  // Define se é humano ou zumbi
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
    public boolean podePegarEquipamento(Equipamento equipamento) {
        return false;
    }

    @Override
    public boolean podeMover(int xO, int yO, int xD, int yD, boolean isDay) {
        // Pode mover até 2 casas na horizontal ou vertical (não pode diagonal)
        int dx = Math.abs(xD - xO);
        int dy = Math.abs(yD - yO);
        if ((dx == 2 && dy == 0) || (dx == 0 && dy == 2)) { // Movimento em linha reta
            return true;
        }
        return false;
    }

    @Override
    public boolean podeMoverParaComEquipamento(Equipamento equipamento) {
        return false;
    }

    @Override
    public boolean podeSerAtacado() {
        return false;
    }


    @Override
    public String getInfoAsString() {
        return id + " | Cão | " + nome + " @ (" + x + ", " + y + ")";
    }

}