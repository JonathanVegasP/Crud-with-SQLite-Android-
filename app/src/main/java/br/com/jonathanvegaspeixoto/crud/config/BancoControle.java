package br.com.jonathanvegaspeixoto.crud.config;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;
import br.com.jonathanvegaspeixoto.crud.model.Usuario;
import static br.com.jonathanvegaspeixoto.crud.config.ConfiguracaoBanco.CONTATO;
import static br.com.jonathanvegaspeixoto.crud.config.ConfiguracaoBanco.ID;
import static br.com.jonathanvegaspeixoto.crud.config.ConfiguracaoBanco.NOME;
import static br.com.jonathanvegaspeixoto.crud.config.ConfiguracaoBanco.PROFISSAO;
import static br.com.jonathanvegaspeixoto.crud.config.ConfiguracaoBanco.SENHA;
import static br.com.jonathanvegaspeixoto.crud.config.ConfiguracaoBanco.TABELA;

public class BancoControle {

    private SQLiteDatabase db;
    private ConfiguracaoBanco banco;
    public static String id = "";

    public BancoControle(Context context) {
        banco = new ConfiguracaoBanco(context);
    }

    public long inserirDados(String id,String nome,String senha,String profissao,String contato){
        ContentValues values = new ContentValues();
        long result;
            db = banco.getWritableDatabase();
            values.put(ID, id);
            values.put(NOME, nome);
            values.put(SENHA, senha);
            values.put(PROFISSAO, profissao);
            values.put(CONTATO, contato);
            result = db.insert(TABELA, null, values);
            db.close();
        return result;
    }

    public void atualizarDados(Usuario usuario){
        ContentValues values;
        db = banco.getWritableDatabase();
        String Where = ID + "=" + "'" + BancoControle.id + "'";
        values = new ContentValues();
        values.put(ID, usuario.getEmail());
        values.put(NOME, usuario.getNome());
        values.put(SENHA, usuario.getSenha());
        values.put(PROFISSAO, usuario.getProfissao());
        values.put(CONTATO, usuario.getContato());
        db.update(TABELA, values,Where,null);
        db.close();
        logarUsuario(usuario.getEmail());
    }

    public void logarUsuario(String id){
        db = banco.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS usuario_logado");
        db.execSQL("CREATE TABLE IF NOT EXISTS usuario_logado(id TEXT)");
        db.close();
        ContentValues values = new ContentValues();
        db = banco.getWritableDatabase();
        values.put(ID, id);
        db.insert("usuario_logado", null, values);
        db.close();
        BancoControle.id = id;
    }

    public void deslogarUsuario(){
        db = banco.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS usuario_logado");
        db.close();
    }

    public Cursor carregarUsuarioLogado(){
        Cursor cursor;
        String[] campos =  {ID};
        db = banco.getReadableDatabase();
        cursor = db.query("usuario_logado", campos, null, null, null, null, null, null);
        if(cursor!=null){
            cursor.moveToFirst();
            id = cursor.getString(cursor.getColumnIndexOrThrow(ID));
        }
        db.close();
        return cursor;
    }

    public Cursor carregarDadosUsuario(){
        Cursor cursor;
        String[] campos =  {ID,NOME, SENHA,PROFISSAO,CONTATO};
        db = banco.getReadableDatabase();
        cursor = db.query(TABELA, campos, null, null, null, null, null, null);
        if(cursor!=null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    public Usuario carregarUsuario(Cursor cursor){
        Usuario usuario = new Usuario();
        usuario.setNome(new String(Base64.decode(cursor.getString(cursor.getColumnIndexOrThrow(NOME)), Base64.DEFAULT)));
        usuario.setEmail(new String(Base64.decode(cursor.getString(cursor.getColumnIndexOrThrow(ID)), Base64.DEFAULT)));
        usuario.setProfissao(new String(Base64.decode(cursor.getString(cursor.getColumnIndexOrThrow(PROFISSAO)), Base64.DEFAULT)));
        usuario.setContato(new String(Base64.decode(cursor.getString(cursor.getColumnIndexOrThrow(CONTATO)), Base64.DEFAULT)));
        return usuario;
    }

    public void deletetarRegistro(){
        db = banco.getReadableDatabase();
        db.delete(TABELA,ID + "=" + "'" + BancoControle.id + "'",null);
        db.close();
        deslogarUsuario();
    }
}