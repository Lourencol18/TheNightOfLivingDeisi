package pt.ulusofona.lp2.thenightofthelivingdeisi;

public class PistolaWaltherPPK extends EquipamentoOfensivo {

    public PistolaWaltherPPK(int id, int x, int y) {
        super(id, "Pistola Walther PPK", x, y);
    }

    @Override
    protected void atacar(Creature alvo) {
        System.out.println("Pistola Walther PPK disparou contra " + alvo.getNome() + "!");
        // Lógica adicional específica da pistola
    }
}