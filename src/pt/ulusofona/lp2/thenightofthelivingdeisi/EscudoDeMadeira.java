package pt.ulusofona.lp2.thenightofthelivingdeisi;

public class EscudoDeMadeira extends Equipamento {

    public EscudoDeMadeira(int id, int x, int y) {
        super(id, "Escudo de Madeira", x, y);
    }

    @Override
    public boolean executarAcao(Creature atacante, Creature alvo) {
        return false; // Ataque é inválido
    }

    @Override
    public String toString() {
        return nome;
    }
}
