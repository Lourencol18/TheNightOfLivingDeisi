package pt.ulusofona.lp2.thenightofthelivingdeisi;

import java.util.ArrayList;
import java.util.List;

public abstract class Creature {
    protected int id;
    protected String nome;
    protected int x;
    protected int y;
    protected int equipa; // 10 para zumbis, 20 para humanos
    protected List<Equipamento> equipamentos = new ArrayList<>(); // Histórico de equipamentos
    protected Equipamento equipamentoAtual = null; // Equipamento em uso
    protected int contadorEquipamentos = 0;
    protected boolean transformado = false;

    public Creature(int id, String nome, int x, int y, int equipa) {
        this.id = id;
        this.nome = nome;
        this.x = x;
        this.y = y;
        this.equipa = equipa;
    }
    public boolean isTransformed() {
        return transformado;
    }

    public void transformar() {
        transformado = true;
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


    public void setX(int x) {
        this.x = x;
    }


    public void setY(int y) {
        this.y = y;
    }


    public int getEquipa() {
        return equipa;
    }

    public boolean isZombie() {
        return equipa == 10;
    }

    public boolean isHuman() {
        return equipa == 20;
    }

    public Equipamento getEquipamentoAtual() {
        return equipamentoAtual;
    }

    public List<Equipamento> getHistoricoEquipamentos() {
        return equipamentos;
    }

    public int getContadorEquipamentos(){
        return contadorEquipamentos;
    }

    // Método para pegar um novo equipamento
    public void pegarEquipamento(Equipamento equipamento) {
        if (equipamentoAtual != null) {
            // Adiciona o equipamento atual ao histórico antes de trocá-lo
            equipamentos.add(equipamentoAtual);
        }
        // Define o novo equipamento como atual
        equipamentoAtual = equipamento;
    }

    // Método para soltar o equipamento atual
    public void soltarEquipamento() {
        if (equipamentoAtual != null) {
            // Adiciona o equipamento ao histórico antes de soltar
            equipamentos.add(equipamentoAtual);
            equipamentoAtual = null;
        }
    }

    public abstract String getTipoCriatura();

    public abstract String getTipo();

    // IdosoZ destrói equipamentos ao encontrá-los
    public abstract void destruirEquipamento();

    public abstract boolean podePegarEquipamento(Equipamento equipamento);


    // Método abstrato para movimentação
    public abstract boolean podeMover(int xO, int yO, int xD, int yD);


    public String getInfoAsString() {
        String tipoEquipe = isHuman() ? "Humano" : "Zombie";
        String modificador = isHuman() ? "+0" : "-0";
        return id + " | " + getTipoCriatura() + " | " + tipoEquipe + " | " + nome + " | " + modificador + " @ (" + x + ", " + y + ")";
    }

    @Override
    public String toString() {
        String equipamentoInfo = (equipamentoAtual != null) ? equipamentoAtual.toString() : "Sem equipamento";
        return id + " | " + nome + " | " + equipamentoInfo + " @ (" + x + ", " + y + ")";
    }
}