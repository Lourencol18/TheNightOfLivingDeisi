package pt.ulusofona.lp2.thenightofthelivingdeisi;

public class AdultoH extends Creature {

    public AdultoH(String nome, int x, int y, int equipa) {
        super(1, nome, x, y, equipa); // ID fixo como 1
    }

    @Override
    public String getTipoCriatura() {
        return "Adulto";
    }

    @Override
    public String getTipo() {
        return "Humano";
    }

    @Override
    public void destruirEquipamento() {

    }

    @Override
    public boolean podeMover(int xO, int yO, int xD, int yD) {
        // Permite movimento em linha reta ou diagonal até 2 casas
        int distanciaX = Math.abs(xD - xO);
        int distanciaY = Math.abs(yD - yO);
        return (distanciaX <= 2 && distanciaY <= 2);
    }

    @Override
    public void pegarEquipamento(Equipamento equipamento) {
        // Adultos humanos podem pegar qualquer tipo de equipamento
        if (equipamentoAtual != null) {
            equipamentos.add(equipamentoAtual); // Adiciona o equipamento atual ao histórico
        }
        equipamentoAtual = equipamento; // Define o novo equipamento como atual
    }

    @Override
    public boolean podeTerEquipamento(int equipmentTypeId) {
        return true; // Adultos podem ter qualquer equipamento
    }

    @Override
    public String toString() {
        String equipamentoInfo = (equipamentoAtual != null) ? equipamentoAtual.toString() : "Sem equipamento";
        return id + " | " + getTipoCriatura() + " | " + getTipo() + " | " + nome + " | Equipamentos usados: "
                + equipamentos.size() + " | " + equipamentoInfo + " @ (" + x + ", " + y + ")";
    }
}
