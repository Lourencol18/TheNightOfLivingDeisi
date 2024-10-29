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
        if (tipoCriatura == 1) { // Incrementa apenas para humanos
            contadorEquipamentos++;
        }
    }

    public void destruirEquipamento() {
        this.equipamento = null;  // Remove o equipamento da criatura
        if (tipoCriatura == 0) { // Incrementa apenas para zumbis
            contadorEquipamentos++;
        }
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

    public Equipamento getEquipamentoPorTipo(int equipmentTypeId) {
        if (equipamento != null && equipamento.tipo == equipmentTypeId) {
            return equipamento;
        }
        return null;
    }

    public int getContadorEquipamentos() {
        return contadorEquipamentos;
    }

    @Override
    public String toString() {
        String equipamentoStr;
        if (tipoCriatura == 1) { // Humanos
            equipamentoStr = "+" + contadorEquipamentos;
        } else { // Zumbis
            equipamentoStr = "-" + contadorEquipamentos;
        }

        return id + " | " + (tipoCriatura == 1 ? "Humano" : "Zombie") + " | " + nome + " | " + equipamentoStr + " @ (" + x + ", " + y + ")";
    }
}
