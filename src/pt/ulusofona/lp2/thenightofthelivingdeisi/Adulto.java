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
        contadorEquipamentos++; // Incrementa o contador de destruições
    }


    @Override
    public boolean podePegarEquipamento(Equipamento equipamento) {
        return true;
    }

    @Override
    public boolean podeMover(int xO, int yO, int xD, int yD) {
        // Pode mover até 2 casas em qualquer direção
        int dx = Math.abs(xD - xO);
        int dy = Math.abs(yD - yO);

        // Movimento normal de até 2 casas
        return dx <= 2 && dy <= 2;
    }




    public void pegarEquipamento(Equipamento equipamento) {
        if (equipamentoAtual != null) {
            equipamentos.add(equipamentoAtual); // Adiciona o equipamento atual ao histórico
        }
        equipamentoAtual = equipamento;
        contadorEquipamentos++; // Incrementa o contador
    }


    @Override
    public String toString() {
        String equipamentoInfo = (equipamentoAtual != null) ? equipamentoAtual.toString() : "Sem equipamento";
        return id + " | " + getTipoCriatura() + " | " + getTipo() + " | " + nome
                + " | Equipamentos usados: " + equipamentos.size()
                + " | " + equipamentoInfo + " @ (" + x + ", " + y + ")";
    }
}
