package pt.ulusofona.lp2.thenightofthelivingdeisi;

public class EscudoDeMadeira extends Equipamento {

    public EscudoDeMadeira(int id, int x, int y) {
        super(0, "Escudo de madeira", x, y);
    }

    @Override
    public boolean executarAcao(Creature atacante, Creature alvo) {
        return false; // Ataque é inválido
    }

    @Override
    public String getInfo() {
        return ""; // Sem informações adicionais
    }

    @Override
    public String toString() {
        return nome;
    }

    boolean isOfType(int id) {
        return id == 0;
    }
}
