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

    // Método para verificar se pode se mover (limitação de 1 célula)
    @Override
    public boolean podeMover(int xO, int yO, int xD, int yD) {
        int distanciaX = Math.abs(xD - xO);
        int distanciaY = Math.abs(yD - yO);
        return distanciaX <= 1 && distanciaY <= 1 && (distanciaX + distanciaY > 0);
    }

    @Override
    public boolean podeTerEquipamento(int equipmentTypeId) {
        return false;  // Idosos não podem ter equipamentos
    }

    @Override
    public String toString() {
        String contadorInfo = isHuman ? "+" + getContadorEquipamentos() : "-" + contadorEquipamentos;
        return id + " | " + getTipoCriatura() + " | " + getTipo() + " | " + nome
                + " | " + contadorInfo + " @ (" + x + ", " + y + ")";
    }
}
