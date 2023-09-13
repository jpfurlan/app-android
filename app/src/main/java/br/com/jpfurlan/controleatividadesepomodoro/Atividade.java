package br.com.jpfurlan.controleatividadesepomodoro;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Atividade {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String nome;

    private String prioridade;

    private String status;

    private String complexidade;

    private String tipo;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    @NonNull
    public String getNome() {
        return nome;
    }

    public void setNome(@NonNull String nome) {
        this.nome = nome;
    }

    public String getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(String prioridade) {
        this.prioridade = prioridade;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComplexidade() {
        return complexidade;
    }

    public void setComplexidade(String complexidade) {
        this.complexidade = complexidade;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "Atividade{" +
                "nome='" + nome + '\'' +
                ", prioridade=" + prioridade +
                ", status='" + status + '\'' +
                ", complexidade='" + complexidade + '\'' +
                ", tipo='" + tipo + '\'' +
                '}';
    }

    @Ignore
    public Atividade(String nome, String prioridade, String status, String complexidade, String tipo) {
        this.nome = nome;
        this.prioridade = prioridade;
        this.status = status;
        this.complexidade = complexidade;
        this.tipo = tipo;
    }

    public Atividade() {
    }
}
