package pt.ulusofona.lp2.thenightofthelivingdeisi;

public abstract class Equipamento {
    protected int id;
    protected int tipo;
    protected String nome;
    protected int x;
    protected int y;


    public Equipamento(int id, int tipo,String nome, int x, int y) {
        this.id = id;
        this.tipo = tipo;
        this.nome = nome;
        this.x = x;
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public int getTipo(){
        return tipo;
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

    public void setY(int y) {
        this.y = y;
    }

    public void setX(int x) {
        this.x = x;
    }

    // Métodos abstratos para ações específicas
    public abstract boolean executarAcao(Creature atacante, Creature alvo);

    public abstract String getInfo();



    public abstract boolean isDefensivo();
}