package pt.ulusofona.lp2.thenightofthelivingdeisi;

public class Idoso extends Creature {

    private boolean isHuman;  // Flag para saber se é humano ou zumbi

    // Construtor
    public Idoso(int id, String nome, int x, int y, int equipa, boolean isHuman) {
        super(id, nome, x, y, equipa); // Chama o construtor da classe pai (Creature)
        this.isHuman = isHuman;  // Define se é humano ou zumbi
    }

    @Override
    public String getTipoCriatura() {
        return "Idoso";  // Tipo genérico para todos os idosos
    }

    @Override
    public String getTipo() {
        return isHuman ? "Humano" : "Zombie";  // Retorna "Humano" ou "Zombie"
    }

    @Override
    public void destruirEquipamento() {
        if (!isHuman) {
            contadorEquipamentos++;  // Incrementa o contador de destruição de equipamentos para zumbis
        }
    }

    @Override
    public boolean podePegarEquipamento(Equipamento equipamento) {
        return false;
    }

    @Override
    public boolean podeMover(int xO, int yO, int xD, int yD) {
        // Pode mover 1 casa apenas na diagonal
        int dx = Math.abs(xD - xO);
        int dy = Math.abs(yD - yO);
        return dx == 1 && dy == 1;
    }

    @Override
    public boolean podeMoverParaComEquipamento(Equipamento equipamento) {
        return true;
    }


    @Override
    public String toString() {
        String contadorInfo = isHuman ? "+" + getContadorEquipamentos() : "-" + contadorEquipamentos;
        return id + " | " + getTipoCriatura() + " | " + getTipo() + " | " + nome
                + " | " + contadorInfo + " @ (" + x + ", " + y + ")";
    }
}
