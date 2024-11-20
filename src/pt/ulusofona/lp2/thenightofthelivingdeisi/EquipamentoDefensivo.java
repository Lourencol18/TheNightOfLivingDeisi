package pt.ulusofona.lp2.thenightofthelivingdeisi;

public abstract class EquipamentoDefensivo extends Equipamento {

    public EquipamentoDefensivo(int id, String nome, int x, int y) {
        super(id, nome, x, y);
    }

    @Override
    public void executarAcao(Creature alvo) {
        defender(alvo);
    }

    // Método específico de defesa
    protected void defender(Creature usuario) {
        System.out.println(nome + " foi usado para defender " + usuario.getNome() + "!");
        // Lógica de defesa pode ser implementada aqui
    }
}