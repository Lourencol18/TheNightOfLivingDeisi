package pt.ulusofona.lp2.thenightofthelivingdeisi;

import java.util.ArrayList;
import java.util.List;

public class Creature {
    int id;
    int tipoCriatura;
    String nome;
    int x;
    int y;
    List<Equipamento> equipamentos = new ArrayList<>(); // Lista para múltiplos equipamentos
    int contadorEquipamentos = 0;

    public Creature(int id, int tipoCriatura, String nome, int x, int y) {
        this.id = id;
        this.tipoCriatura = tipoCriatura;
        this.nome = nome;
        this.x = x;
        this.y = y;
    }

    public void apanhaequipamento(Equipamento equipamento) {
        if (tipoCriatura == 1) { // Apenas humanos pegam equipamentos
            equipamentos.add(equipamento); // Adiciona à lista de equipamentos
            contadorEquipamentos++;
        }
    }

    public void destruirEquipamento() {
        if (tipoCriatura == 0 && !equipamentos.isEmpty()) { // Apenas zumbis destroem
            equipamentos.remove(0); // Remove o primeiro equipamento da lista
            contadorEquipamentos++;
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

    public List<Equipamento> getEquipamentos() {
        return equipamentos;
    }

    public Equipamento getEquipamentoPorTipo(int equipmentTypeId) {
        for (Equipamento equipamento : equipamentos) {
            if (equipamento.tipo == equipmentTypeId) {
                return equipamento;
            }
        }
        return null;
    }

    public int getContadorEquipamentos() {
        return contadorEquipamentos;
    }

    @Override
    public String toString() {
        String equipamentoStr = (tipoCriatura == 1) ? "+" + contadorEquipamentos : "-" + contadorEquipamentos;
        return id + " | " + (tipoCriatura == 1 ? "Humano" : "Zombie") + " | " + nome + " | " + equipamentoStr + " @ (" + x + ", " + y + ")";
    }
}
