package pt.ulusofona.lp2.thenightofthelivingdeisi;

public class Crianca extends Creature {

    private boolean isHuman;  // Flag para saber se é humana ou zumbi

    // Construtor
    public Crianca(int id, String nome, int x, int y, int equipa, boolean isHuman) {
        super(id, nome, x, y, equipa); // Chama o construtor da classe pai (Creature)
        this.isHuman = isHuman;  // Define se é humana ou zumbi
    }

    @Override
    public String getTipoCriatura() {
        return "Criança";  // Tipo genérico para todas as criaturas Criança
    }

    @Override
    public String getTipo() {
        return isHuman ? "Humano" : "Zombie";  // Retorna "Humano" ou "Zombie"
    }

    @Override
    public void destruirEquipamento() {
        // Implementação para destruição de equipamentos (caso necessário)
    }

    @Override
    public boolean podePegarEquipamento(Equipamento equipamento) {
        return equipamento.getId() == 0 || equipamento.getId() == 3;
    }

    @Override
    public boolean podeMover(int xO, int yO, int xD, int yD) {
        // Verifica se é horizontal ou vertical e se é apenas 1 casa
        int distanciaX = Math.abs(xD - xO);
        int distanciaY = Math.abs(xD - yD);
        return (distanciaX == 1 && distanciaY == 0) || (distanciaX == 0 && distanciaY == 1);
    }

    @Override
    public void pegarEquipamento(Equipamento equipamento) {
        // Crianças podem pegar apenas certos tipos de equipamentos
        if (isHuman) {
            // Crianças humanas podem pegar apenas equipamentos defensivos
            if (equipamento instanceof Lixivia || equipamento instanceof EscudoDeMadeira) {
                super.pegarEquipamento(equipamento);  // Chama a lógica base
            } else {
                System.out.println("Crianças só podem pegar equipamentos defensivos!");
            }
        } else {
            // Crianças zumbis não podem pegar equipamentos
            System.out.println("Zumbis não podem pegar equipamentos!");
        }
    }

    @Override
    public boolean podeTerEquipamento(int equipmentTypeId) {
        // Crianças humanas podem ter apenas certos tipos de equipamento
        if (isHuman) {
            return equipmentTypeId == 1 || equipmentTypeId == 3; // Apenas Lixívia ou Escudo
        }
        return false; // Zumbis não podem ter equipamentos
    }

    @Override
    public String toString() {
        String equipamentoInfo = (equipamentoAtual != null) ? equipamentoAtual.toString() : "Sem equipamento";
        return id + " | " + getTipoCriatura() + " | " + getTipo() + " | " + nome
                + " | Equipamentos usados: " + equipamentos.size()
                + " | " + equipamentoInfo + " @ (" + x + ", " + y + ")";
    }
}
