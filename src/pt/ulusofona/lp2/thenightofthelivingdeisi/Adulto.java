package pt.ulusofona.lp2.thenightofthelivingdeisi;

public class Adulto extends Creature {

    private boolean isHuman;  // Flag para saber se é humano ou zumbi

    // Construtor
    public Adulto(int id, String nome, int x, int y, int equipa, boolean isHuman) {
        super(id, nome, x, y, equipa); // Chama o construtor da classe pai (Creature)
        this.isHuman = isHuman;  // Define se é humano ou zumbi
    }

    @Override
    public String getTipoCriatura() {
        return "Adulto";  // Tipo genérico para todas as criaturas Adulto
    }

    @Override
    public String getTipo() {
        return isHuman ? "Humano" : "Zombie";  // Retorna "Humano" ou "Zombie"
    }

    @Override
    public void destruirEquipamento() {
        // Zumbis e humanos podem ter comportamentos diferentes ao destruir equipamentos.
        // Podemos deixar aqui o comportamento específico para cada tipo.
        if (!isHuman) {
            contadorEquipamentos++;  // Exemplo para zumbis destruírem equipamentos
        }
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
        // Humanos podem pegar qualquer equipamento, zumbis não
        return isHuman;
    }

    @Override
    public void pegarEquipamento(Equipamento equipamento) {
        // Somente humanos podem pegar equipamentos
        if (isHuman) {
            if (equipamentoAtual != null) {
                equipamentos.add(equipamentoAtual);  // Adiciona o equipamento atual ao histórico
            }
            equipamentoAtual = equipamento;  // Define o novo equipamento como atual
        }
    }

    @Override
    public String toString() {
        String equipamentoInfo = (equipamentoAtual != null) ? equipamentoAtual.toString() : "Sem equipamento";
        return id + " | " + getTipoCriatura() + " | " + getTipo() + " | " + nome
                + " | Equipamentos usados: " + equipamentos.size()
                + " | " + equipamentoInfo + " @ (" + x + ", " + y + ")";
    }
}
