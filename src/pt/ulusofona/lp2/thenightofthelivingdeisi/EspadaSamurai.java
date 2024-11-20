package pt.ulusofona.lp2.thenightofthelivingdeisi;

public class EspadaSamurai extends EquipamentoOfensivo {

    public EspadaSamurai(int id, int x, int y) {
        super(id, "Espada Samurai", x, y);
    }

    @Override
    protected void atacar(Creature alvo) {
        System.out.println("Espada Samurai foi usada para um corte preciso em " + alvo.getNome() + "!");
        // Lógica adicional específica da Espada Samurai
    }
}