package pt.ulusofona.lp2.thenightofthelivingdeisi;

public class CriançaH extends Creature {

    public CriançaH(String nome, int x, int y, int equipa) {
        super(0, nome, x, y, equipa); // ID fixo como 0
    }

    @Override
    public String getTipoCriatura() {
        return "Criança";
    }

    @Override
    public String getTipo() {
        return "Humano";
    }

    @Override
    public boolean podeMover(int xO, int yO, int xD, int yD) {
        // Verifica se é horizontal ou vertical e se é apenas 1 casa
        int distanciaX = Math.abs(xD - xO);
        int distanciaY = Math.abs(yD - yO);
        return (distanciaX == 1 && distanciaY == 0) || (distanciaX == 0 && distanciaY == 1);
    }

    // Sobrescreve o método para pegar equipamentos defensivos apenas
    @Override
    public void pegarEquipamento(Equipamento equipamento) {
        if (equipamento instanceof Lixivia || equipamento instanceof EscudoDeMadeira) {
            super.pegarEquipamento(equipamento); // Chama a lógica base
        } else {
            System.out.println("Crianças só podem pegar equipamentos defensivos!");
        }
    }

    @Override
    public String toString() {
        String equipamentoInfo = (equipamentoAtual != null) ? equipamentoAtual.toString() : "Sem equipamento";
        return id + " | " + getTipoCriatura() + " | " + getTipo() + " | " + nome + " | Equipamentos usados: "
                + equipamentos.size() + " | " + equipamentoInfo + " @ (" + x + ", " + y + ")";
    }
}