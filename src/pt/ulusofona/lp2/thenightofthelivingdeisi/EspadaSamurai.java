package pt.ulusofona.lp2.thenightofthelivingdeisi;

public class EspadaSamurai extends Equipamento {

    public EspadaSamurai(int id, int x, int y) {
        super(id, "Espada samurai", x, y);
        System.out.println("Espada Samurai criada com ID=" + id + ", Coordenadas=(" + x + ", " + y + ")");
    }

    @Override
    public boolean executarAcao(Creature atacante, Creature alvo) {
        if (alvo.isZombie()) {
            return true; // Ataque bem-sucedido
        }
        return false; // Ataque falhou (alvo não é zumbi)
    }

    @Override
    public String getInfo() {
        return "";
    }

    @Override
    public String toString() {
        return nome;
    }
}
