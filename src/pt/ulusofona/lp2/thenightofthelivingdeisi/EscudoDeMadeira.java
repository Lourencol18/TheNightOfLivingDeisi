package pt.ulusofona.lp2.thenightofthelivingdeisi;

public class EscudoDeMadeira extends EquipamentoDefensivo {

    public EscudoDeMadeira(int id, int x, int y) {
        super(id, "Escudo de Madeira", x, y);
    }

    @Override
    protected void defender(Creature usuario) {
        System.out.println("Escudo de Madeira bloqueou um ataque em " + usuario.getNome() + "!");
        // Lógica adicional específica do escudo
    }
}