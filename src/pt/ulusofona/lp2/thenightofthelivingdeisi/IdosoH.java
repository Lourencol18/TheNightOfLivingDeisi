package pt.ulusofona.lp2.thenightofthelivingdeisi;

public class IdosoH extends Creature {

    public IdosoH(int id, String nome, int x, int y, int equipa) {
        super(2, nome, x, y, equipa);
    }

    @Override
    public String getTipoCriatura() {
        return "Idoso";
    }

    @Override
    public String getTipo() {
        return "Humano"; // Sempre humano
    }

    @Override
    public void destruirEquipamento() {

    }


    // Idosos Humanos não podem pegar equipamentos
    @Override
    public void pegarEquipamento(Equipamento equipamento) {
        // Não faz nada, pois IdosoH não coleta equipamentos
    }

    // Método para verificar se pode se mover (só de dia e 1 célula)
    public boolean podeMover( int xO, int yO, int xD, int yD) {

        // Verifica movimento limitado a 1 célula em qualquer direção
        int distanciaX = Math.abs(xD - xO);
        int distanciaY = Math.abs(yD - yO);
        return distanciaX <= 1 && distanciaY <= 1 && (distanciaX + distanciaY > 0);
    }

    @Override
    public boolean podeTerEquipamento(int equipmentTypeId) {
        return false; // Idosos não podem ter equipamentos
    }

    @Override
    public String toString() {
        String contadorEquipamentos = "+" + getContadorEquipamentos();
        return id + " | " + getTipoCriatura() + " | " + getTipo() + " | " + nome + " | "
                + contadorEquipamentos + " @ (" + x + ", " + y + ")";
    }
}
