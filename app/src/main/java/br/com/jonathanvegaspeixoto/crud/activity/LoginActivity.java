package br.com.jonathanvegaspeixoto.crud.activity;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import br.com.jonathanvegaspeixoto.crud.R;
import br.com.jonathanvegaspeixoto.crud.config.BancoControle;
import static br.com.jonathanvegaspeixoto.crud.config.ConfiguracaoBanco.ID;
import static br.com.jonathanvegaspeixoto.crud.config.ConfiguracaoBanco.SENHA;

public class LoginActivity extends AppCompatActivity {

    private TextView cadastro;
    private TextInputEditText email, senha;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        componentes();
        eventos();
    }

    private void componentes(){
        cadastro = findViewById(R.id.lblCadastro);
        email = findViewById(R.id.txtEmail);
        senha = findViewById(R.id.txtSenha);
        login = findViewById(R.id.btnLogin);
    }

    private void eventos(){
        cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,CadastroActivity.class));
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login.setEnabled(false);
                validarLogin();
            }
        });
    }

    private void validarLogin(){
        assert email.getText() != null && senha.getText() != null;
        if(!email.getText().toString().isEmpty()){
            if(!senha.getText().toString().isEmpty()){
                BancoControle controle = new BancoControle(this);
                Cursor cursor = controle.carregarDadosUsuario();
                if(cursor != null) {
                    boolean cadastrado = false;
                    while (!cursor.isAfterLast()) {
                        if (Base64.encodeToString(email.getText().toString().toLowerCase().getBytes(), Base64.DEFAULT).replaceAll("([\\n\\r])","").equals(cursor.getString(cursor.getColumnIndexOrThrow(ID)))) {
                            cadastrado = true;
                            if(Base64.encodeToString(senha.getText().toString().getBytes(), Base64.DEFAULT).equals(cursor.getString(cursor.getColumnIndexOrThrow(SENHA)))){
                                efetuarLogin(cursor);
                            }else {
                                login.setEnabled(true);
                                senha.setText("");
                                Toast.makeText(this, "Senha incorreta!", Toast.LENGTH_SHORT).show();
                                senha.requestFocus();
                            }
                        }
                        cursor.moveToNext();
                    }
                    if(!cadastrado){
                        login.setEnabled(true);
                        Toast.makeText(this, "Este usuário não está cadastrado!", Toast.LENGTH_SHORT).show();
                        senha.setText("");
                        email.setText("");
                        email.requestFocus();
                    }
                }else {
                    Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
                }
            }else {
                login.setEnabled(true);
                Toast.makeText(this, "Preencha o campo senha!", Toast.LENGTH_SHORT).show();
                senha.requestFocus();
            }
        }else {
            login.setEnabled(true);
            Toast.makeText(this, "Preencha o campo e-mail!", Toast.LENGTH_SHORT).show();
            email.requestFocus();
        }
    }

    private void efetuarLogin(Cursor cursor){
        BancoControle controle = new BancoControle(this);
        controle.logarUsuario(cursor.getString(cursor.getColumnIndex(ID)));
        startActivity(new Intent(this,CrudActivity.class));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            BancoControle controle = new BancoControle(this);
            if(controle.carregarUsuarioLogado() != null){
                startActivity(new Intent(this,CrudActivity.class));
                finish();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}