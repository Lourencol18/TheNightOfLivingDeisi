package pt.ulusofona.lp2.thenightofthelivingdeisi;

public class IdosoZ extends Creature {

    public IdosoZ(int id, String nome, int x, int y, int equipa) {
        super(2, nome, x, y, equipa);
    }

    @Override
    public String getTipoCriatura() {
        return "Idoso";
    }

    @Override
    public String getTipo() {
        return "Zombie"; // Sempre zumbi
    }

    // IdosoZ destrói equipamentos ao encontrá-los
    @Override
    public void destruirEquipamento() {
        contadorEquipamentos++; // Incrementa o contador de destruições
    }

    // Método para verificar se pode se mover (de dia ou à noite, 1 célula)
    public boolean podeMover(int xO, int yO, int xD, int yD) {
        // Verifica movimento limitado a 1 célula em qualquer direção
        int distanciaX = Math.abs(xD - xO);
        int distanciaY = Math.abs(yD - yO);
        return distanciaX <= 1 && distanciaY <= 1 && (distanciaX + distanciaY > 0);
    }

    @Override
    public String toString() {
        String contadorDestruicoes = "-" + contadorEquipamentos;
        return id + " | " + getTipoCriatura() + " | " + getTipo() + " | " + nome + " | "
                + contadorDestruicoes + " @ (" + x + ", " + y + ")";
    }
}
