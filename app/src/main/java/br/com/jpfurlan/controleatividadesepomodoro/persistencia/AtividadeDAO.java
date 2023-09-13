package br.com.jpfurlan.controleatividadesepomodoro.persistencia;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.jpfurlan.controleatividadesepomodoro.Atividade;

@Dao
public interface AtividadeDAO {

    @Insert
    void insert(Atividade atividade);

    @Update
    void update(Atividade atividade);

    @Delete
    void delete(Atividade atividade);

    @Query("SELECT * FROM Atividade")
    List<Atividade> getAllAtividades();

    @Query("SELECT * FROM Atividade WHERE id = :atividadeId")
    Atividade getAtividadeById(int atividadeId);

    @Query("SELECT * FROM Atividade WHERE nome = :nomeAtividade")
    Atividade getAtividadeByNome(String nomeAtividade);

}