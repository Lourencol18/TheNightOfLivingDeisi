package pt.ulusofona.lp2.thenightofthelivingdeisi;

public class Lixivia extends EquipamentoDefensivo {

    public Lixivia(int id, int x, int y) {
        super(id, "Lixívia", x, y);
    }

    @Override
    protected void defender(Creature usuario) {
        System.out.println("Lixívia foi usada para afastar zumbis de " + usuario.getNome() + "!");
        // Lógica adicional específica da lixívia
    }
}