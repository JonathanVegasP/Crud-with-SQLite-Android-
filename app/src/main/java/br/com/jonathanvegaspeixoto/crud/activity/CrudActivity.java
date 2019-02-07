package br.com.jonathanvegaspeixoto.crud.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.jonathanvegaspeixoto.crud.R;
import br.com.jonathanvegaspeixoto.crud.config.BancoControle;
import br.com.jonathanvegaspeixoto.crud.model.Usuario;

import static br.com.jonathanvegaspeixoto.crud.config.ConfiguracaoBanco.ID;

public class CrudActivity extends AppCompatActivity {

    private TextView nome,email,profissao,contato;
    private Button alterar;
    private boolean altera = false;
    private TextInputLayout tilNome, tilEmail,tilSenha,tilProfissao,tilContato;
    private TextInputEditText txtNome,txtEmail,txtSenha,txtProfissao,txtContato;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crud);
        componentes();
        eventos();
    }

    private void componentes(){
        nome = findViewById(R.id.lblNome);
        email = findViewById(R.id.lblEmail);
        profissao = findViewById(R.id.lblProfissao);
        contato = findViewById(R.id.lblContato);
        alterar = findViewById(R.id.btnAlterar);
        tilNome = findViewById(R.id.tilNome);
        tilEmail = findViewById(R.id.tilEmail);
        tilSenha = findViewById(R.id.tilSenha);
        tilProfissao = findViewById(R.id.tilProfissao);
        tilContato = findViewById(R.id.tilContato);
        txtNome = findViewById(R.id.txtNome);
        txtEmail = findViewById(R.id.txtEmail);
        txtSenha = findViewById(R.id.txtSenha);
        txtProfissao = findViewById(R.id.txtProfissao);
        txtContato = findViewById(R.id.txtContato);
        recuperarUsuario();
    }

    @SuppressLint("SetTextI18n")
    private void recuperarUsuario(){
        BancoControle controle = new BancoControle(this);
        Cursor cursor = controle.carregarDadosUsuario();
        if(cursor != null)
            while (!cursor.isAfterLast()){
                if(BancoControle.id.equals(cursor.getString(cursor.getColumnIndexOrThrow(ID)))){
                    Usuario usuario = controle.carregarUsuario(cursor);
                    nome.setText(usuario.getNome());
                    email.setText(email.getText().toString()+usuario.getEmail());
                    profissao.setText(profissao.getText().toString()+usuario.getProfissao());
                    contato.setText(contato.getText().toString()+usuario.getContato());
                }
                cursor.moveToNext();
            }
    }

    private void eventos(){
        alterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alterar.setEnabled(false);
                if(!altera){
                    alterar.setText(getString(R.string.salvar));
                    nome.setVisibility(View.GONE);
                    email.setVisibility(View.GONE);
                    profissao.setVisibility(View.GONE);
                    contato.setVisibility(View.GONE);
                    tilNome.setVisibility(View.VISIBLE);
                    tilEmail.setVisibility(View.VISIBLE);
                    tilSenha.setVisibility(View.VISIBLE);
                    tilProfissao.setVisibility(View.VISIBLE);
                    tilContato.setVisibility(View.VISIBLE);
                    alterar.setEnabled(true);
                    altera = true;
                }else {
                    validarAlteracao();
                }
            }
        });
    }

    private void validarAlteracao(){
        assert txtNome.getText() != null && txtEmail.getText() != null && txtSenha.getText() != null
                && txtProfissao.getText() != null && txtContato.getText() != null;
        if(!txtNome.getText().toString().isEmpty()){
            if (!txtEmail.getText().toString().isEmpty()) {
                boolean result = false;
                try {
                    Pattern pattern = Pattern.compile("^[\\w-+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$", Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(txtEmail.getText().toString());
                    if (matcher.matches())
                        result = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (result) {
                    if (!txtSenha.getText().toString().isEmpty()) {
                        if(txtSenha.getText().toString().length() >= 6) {
                            if (!txtProfissao.getText().toString().isEmpty()) {
                                if (!txtContato.getText().toString().isEmpty()) {
                                    Usuario usuario = new Usuario();
                                    usuario.setNome(Base64.encodeToString(txtNome.getText().toString().getBytes(), Base64.DEFAULT));
                                    usuario.setEmail(Base64.encodeToString(txtEmail.getText().toString().toLowerCase().getBytes(), Base64.DEFAULT).replaceAll("([\\n\\r])",""));
                                    usuario.setSenha(Base64.encodeToString(txtSenha.getText().toString().getBytes(), Base64.DEFAULT));
                                    usuario.setProfissao(Base64.encodeToString(txtProfissao.getText().toString().getBytes(), Base64.DEFAULT));
                                    usuario.setContato(Base64.encodeToString(txtContato.getText().toString().getBytes(), Base64.DEFAULT));
                                    efetuarAlteracao(usuario);
                                } else {
                                    alterar.setEnabled(true);
                                    Toast.makeText(this, "Preencha o campo contato!", Toast.LENGTH_SHORT).show();
                                    txtContato.requestFocus();
                                }
                            } else {
                                alterar.setEnabled(true);
                                Toast.makeText(this, "Preencha o campo profissão!", Toast.LENGTH_SHORT).show();
                                txtProfissao.requestFocus();
                            }
                        }else{
                            alterar.setEnabled(true);
                            Toast.makeText(this, "Digite uma senha mais forte!", Toast.LENGTH_SHORT).show();
                            txtSenha.requestFocus();
                        }
                    } else {
                        alterar.setEnabled(true);
                        Toast.makeText(this, "Preencha o campo senha!", Toast.LENGTH_SHORT).show();
                        txtSenha.requestFocus();
                    }
                }else {
                    alterar.setEnabled(true);
                    Toast.makeText(this, "Digite um e-mail válido!", Toast.LENGTH_SHORT).show();
                    txtEmail.requestFocus();
                }
            }else {
                alterar.setEnabled(true);
                Toast.makeText(this, "Preencha o campo e-mail!", Toast.LENGTH_SHORT).show();
                txtEmail.requestFocus();
            }
        }else {
            Toast.makeText(this, "Preencha o campo nome!", Toast.LENGTH_SHORT).show();
            txtNome.requestFocus();
        }
    }

    private void efetuarAlteracao(Usuario usuario){
        BancoControle controle = new BancoControle(this);
        if(usuario != null)
        controle.atualizarDados(usuario);
        nome.setVisibility(View.VISIBLE);
        email.setVisibility(View.VISIBLE);
        profissao.setVisibility(View.VISIBLE);
        contato.setVisibility(View.VISIBLE);
        tilNome.setVisibility(View.GONE);
        tilEmail.setVisibility(View.GONE);
        tilSenha.setVisibility(View.GONE);
        tilProfissao.setVisibility(View.GONE);
        tilContato.setVisibility(View.GONE);
        email.setText(getString(R.string.lbl_e_mail));
        profissao.setText(getString(R.string.lbl_profissao));
        contato.setText(getString(R.string.lbl_contato));
        recuperarUsuario();
        alterar.setEnabled(true);
        alterar.setText(getString(R.string.alterar));
        altera = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuSair:
                BancoControle controle = new BancoControle(this);
                controle.deslogarUsuario();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
            case R.id.menuExcluir:
                excluirConta();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void excluirConta(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Excluir Conta");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage("Deseja excluir a sua conta?");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BancoControle controle = new BancoControle(CrudActivity.this);
                controle.deletetarRegistro();
                startActivity(new Intent(CrudActivity.this,LoginActivity.class));
                finish();
            }
        });
        builder.setNegativeButton("Não", null);
        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}