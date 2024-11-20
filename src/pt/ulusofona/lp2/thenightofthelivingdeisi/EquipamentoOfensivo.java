package pt.ulusofona.lp2.thenightofthelivingdeisi;

public abstract class EquipamentoOfensivo extends Equipamento {

    public EquipamentoOfensivo(int id, String nome, int x, int y) {
        super(id, nome, x, y);
    }

    @Override
    public void executarAcao(Creature alvo) {
        atacar(alvo);
    }

    // Método específico de ataque
    protected void atacar(Creature alvo) {
        System.out.println(nome + " foi usado para atacar " + alvo.getNome() + "!");
        // Lógica de ataque pode ser implementada aqui
    }
}