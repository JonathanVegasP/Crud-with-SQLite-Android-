package br.com.jonathanvegaspeixoto.crud.config;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ConfiguracaoBanco extends SQLiteOpenHelper {

    private static final String BANCO = "banco.db";
    static final String TABELA = "usuarios";
    public static final String ID = "id";
    static final String NOME = "nome";
    public static final String SENHA = "senha";
    static final String PROFISSAO = "profissao";
    static final String CONTATO = "contato";
    private static final int VERSION = 1;

    ConfiguracaoBanco(Context context) {
        super(context, BANCO, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "+TABELA+"("+ID+" TEXT PRIMARY KEY, "+NOME+" TEXT, "+
                SENHA+" TEXT, "+PROFISSAO+" TEXT, "+CONTATO+" TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELA);
        onCreate(db);
    }
}
