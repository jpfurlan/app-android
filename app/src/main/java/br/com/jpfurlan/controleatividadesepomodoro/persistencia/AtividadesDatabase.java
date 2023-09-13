package br.com.jpfurlan.controleatividadesepomodoro.persistencia;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import br.com.jpfurlan.controleatividadesepomodoro.Atividade;

@Database(entities = {Atividade.class}, version = 1, exportSchema = false)
public abstract class AtividadesDatabase extends RoomDatabase {

    public abstract AtividadeDAO atividadeDAO();

    private static AtividadesDatabase instance;

    public static AtividadesDatabase getDatabase(final Context context) {

        if (instance == null) {

            synchronized (AtividadesDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context,
                            AtividadesDatabase.class,
                            "atividades.db").allowMainThreadQueries().build();
                }
            }
        }
        return instance;
    }
}
