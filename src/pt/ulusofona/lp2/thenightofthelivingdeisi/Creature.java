package pt.ulusofona.lp2.thenightofthelivingdeisi;

import java.util.ArrayList;
import java.util.List;

public class Creature {
    int id;
    int tipoCriatura;
    String nome;
    int x;
    int y;
    Equipamento equipamento;
    int contadorEquipamentos = 0;

    public Creature(int id, int tipoCriatura, String nome, int x, int y) {
        this.id = id;
        this.tipoCriatura = tipoCriatura;
        this.nome = nome;
        this.x = x;
        this.y = y;
    }

    public void setEquipamento(Equipamento equipamento) {
        this.equipamento = equipamento;
    }

    public void destruirEquipamento() {
        this.equipamento = equipamento;
        this.contadorEquipamentos++;
    }
    public List<Equipamento> getEquipamentos() {
        List<Equipamento> equipamentosList = new ArrayList<>();
        if (equipamento != null) {
            equipamentosList.add(equipamento);
        }
        return equipamentosList;
    }


    public void apanhaEquipamento() {
        if (this.equipamento != null) {
            this.equipamento = null;
            this.contadorEquipamentos--;  // Decrementa quando perde equipamento
        }
    }

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

    public int getContadorEquipamentos() {
        return contadorEquipamentos;
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
