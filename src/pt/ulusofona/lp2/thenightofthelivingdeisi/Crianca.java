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
        contadorEquipamentos++; // Incrementa o contador de destruições
    }




    @Override
    public boolean podePegarEquipamento(Equipamento equipamento) {
      return equipamento.isDefensivo();
    }


    @Override
    public boolean podeMover(int xO, int yO, int xD, int yD, boolean isDay) {
        // Pode mover 1 casa na horizontal ou vertical (não pode diagonal)
        int dx = Math.abs(xD - xO);
        int dy = Math.abs(yD - yO);
        return (dx + dy == 1) && (dx == 0 || dy == 0);
    }

    @Override
    public boolean podeMoverParaComEquipamento(Equipamento equipamento) {
        if (equipamento == null ) {
            return true; // Se não houver equipamento, pode mover
        } else if(equipamento.isDefensivo()){
            return true;
        }else if (!equipamento.isDefensivo() && isZombie()){
            return true;
        }

        return false;
    }

    @Override
    public boolean podeSerAtacado() {
        if(equipamentoAtual.isDefensivo() ){
            return false;
        }
        if (isZombie()){
            return false;
        }
        return true;
    }


    public void pegarEquipamento(Equipamento equipamento) {
        if (podePegarEquipamento(equipamento)) {
            this.equipamentoAtual = equipamento;
            contadorEquipamentos++;
        } else {
            throw new IllegalStateException("A criança não pode pegar este equipamento.");
        }
        // Incrementa o contador
    }




    @Override
    public String toString() {
        String equipamentoInfo = (equipamentoAtual != null) ? equipamentoAtual.toString() : "Sem equipamento";
        return id + " | " + getTipoCriatura() + " | " + getTipo() + " | " + nome
                + " | Equipamentos usados: " + equipamentos.size()
                + " | " + equipamentoInfo + " @ (" + x + ", " + y + ")";
    }
}
