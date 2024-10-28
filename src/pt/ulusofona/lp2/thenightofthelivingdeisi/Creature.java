package pt.ulusofona.lp2.thenightofthelivingdeisi;

public class Creature {
    int id;
    int tipoCriatura;
    String nome;
    int x;
    int y;
    Equipamento equipamento;

    public Creature(int id, int tipoCriatura, String nome, int x, int y) {
        this.id = id;
        this.tipoCriatura = tipoCriatura;
        this.nome = nome;
        this.x = x;
        this.y = y;
    }

    public void destruirEquipamento() {
        this.equipamento = equipamento;
        this.contadorEquipamentos++;
    }

    public void apanhaEquipamento() {
        if (this.equipamento != null) {
            this.equipamento = null;
            this.contadorEquipamentos--;  // Decrementa quando perde equipamento
        }
    }
    // Na classe Creature
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    public int getId() {
        return id;
    }

    public int getTipo() {
        return tipoCriatura;
    }

    public String getNome() {
        return nome;
    }

    public Equipamento getEquipamento() {
        return equipamento;
    }


    @Override
    public String toString() {
        String equipamentoStr = (equipamento != null) ? equipamento.toString() : "-";
        if (tipoCriatura == 0) {
            return id + " | " + "Humano" + " | " + nome + " | " + equipamentoStr + " @ (" + x + ", " + y + ")";
        }
        return id + " | " + "Zombie" + " | " + nome + " | " + equipamentoStr + " @ (" + x + ", " + y + ")";
    }
}
