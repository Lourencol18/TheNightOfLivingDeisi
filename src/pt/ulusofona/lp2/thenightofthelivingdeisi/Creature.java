package pt.ulusofona.lp2.thenightofthelivingdeisi;

public abstract class Creature {
    protected int id;
    protected String nome;
    protected int x;
    protected int y;

    public Creature(int id, String nome, int x, int y) {
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

    // Método abstrato para retornar o tipo de criatura
    public abstract String getTipoCriatura();

    // Método abstrato para retornar se é humano ou zumbi
    public abstract String getTipo();



}