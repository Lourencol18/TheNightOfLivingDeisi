package pt.ulusofona.lp2.thenightofthelivingdeisi;

public abstract class Equipamento {
    protected int id;
    protected String nome;
    protected int x;
    protected int y;

    protected int idDoTipo = -1;

    public Equipamento(int id, String nome, int x, int y) {
        this.id = id;
        this.nome = nome;
        this.x = x;
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }



    // Métodos abstratos para ações específicas
    public abstract boolean executarAcao(Creature atacante, Creature alvo);

    public abstract String getInfo();

    abstract boolean isOfType (int typeId);
}