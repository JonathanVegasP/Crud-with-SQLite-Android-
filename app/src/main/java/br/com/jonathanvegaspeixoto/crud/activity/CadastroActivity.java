package br.com.jonathanvegaspeixoto.crud.activity;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.jonathanvegaspeixoto.crud.R;
import br.com.jonathanvegaspeixoto.crud.config.BancoControle;
import br.com.jonathanvegaspeixoto.crud.model.Usuario;

public class CadastroActivity extends AppCompatActivity {

    private TextInputEditText nome, email, senha, profissao, contato;
    private Button cadastrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        componentes();
        eventos();
    }

    private void componentes(){
        nome = findViewById(R.id.txtNome);
        email = findViewById(R.id.txtEmail);
        senha = findViewById(R.id.txtSenha);
        profissao = findViewById(R.id.txtProfissao);
        contato = findViewById(R.id.txtContato);
        cadastrar = findViewById(R.id.btnCadastrar);
    }

    private void eventos(){
        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cadastrar.setEnabled(false);
                validarCadastro();
            }
        });
    }

    private void validarCadastro(){
        assert nome.getText() != null && email.getText() != null && senha.getText() != null &&
                profissao.getText() != null && contato.getText() != null;
        if (!nome.getText().toString().isEmpty()){
            if (!email.getText().toString().isEmpty()) {
                boolean result = false;
                try {
                    Pattern pattern = Pattern.compile("^[\\w-+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$", Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(email.getText().toString());
                    if (matcher.matches())
                        result = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (result) {
                    if (!senha.getText().toString().isEmpty()) {
                        if(senha.getText().toString().length() >= 6) {
                            if (!profissao.getText().toString().isEmpty()) {
                                if (!contato.getText().toString().isEmpty()) {
                                    Usuario usuario = new Usuario();
                                    usuario.setNome(Base64.encodeToString(nome.getText().toString().getBytes(), Base64.DEFAULT));
                                    usuario.setEmail(Base64.encodeToString(email.getText().toString().toLowerCase().getBytes(), Base64.DEFAULT).replaceAll("([\\n\\r])",""));
                                    usuario.setSenha(Base64.encodeToString(senha.getText().toString().getBytes(), Base64.DEFAULT));
                                    usuario.setProfissao(Base64.encodeToString(profissao.getText().toString().getBytes(), Base64.DEFAULT));
                                    usuario.setContato(Base64.encodeToString(contato.getText().toString().getBytes(), Base64.DEFAULT));
                                    efetuarCadastro(usuario);
                                } else {
                                    cadastrar.setEnabled(true);
                                    Toast.makeText(this, "Preencha o campo contato!", Toast.LENGTH_SHORT).show();
                                    contato.requestFocus();
                                }
                            } else {
                                cadastrar.setEnabled(true);
                                Toast.makeText(this, "Preencha o campo profissão!", Toast.LENGTH_SHORT).show();
                                profissao.requestFocus();
                            }
                        }else{
                            cadastrar.setEnabled(true);
                            Toast.makeText(this, "Digite uma senha mais forte!", Toast.LENGTH_SHORT).show();
                            senha.requestFocus();
                        }
                    } else {
                        cadastrar.setEnabled(true);
                        Toast.makeText(this, "Preencha o campo senha!", Toast.LENGTH_SHORT).show();
                        senha.requestFocus();
                    }
                }else {
                    cadastrar.setEnabled(true);
                    Toast.makeText(this, "Digite um e-mail válido!", Toast.LENGTH_SHORT).show();
                    email.requestFocus();
                }
            }else {
                cadastrar.setEnabled(true);
                Toast.makeText(this, "Preencha o campo e-mail!", Toast.LENGTH_SHORT).show();
                email.requestFocus();
            }
        }else {
            cadastrar.setEnabled(true);
            Toast.makeText(this, "Preencha o campo nome!", Toast.LENGTH_SHORT).show();
            nome.requestFocus();
        }
    }

    private void efetuarCadastro(Usuario usuario){
        BancoControle controle = new BancoControle(this);
        long result = controle.inserirDados(usuario.getEmail(),usuario.getNome(),usuario.getSenha(),usuario.getProfissao(),usuario.getContato());
        if(result == -1){
            Toast.makeText(this, "Este usuário já está cadastrado!", Toast.LENGTH_SHORT).show();
            cadastrar.setEnabled(true);
        }else{
            Toast.makeText(this, "Cadastro efetuado com sucesso!", Toast.LENGTH_SHORT).show();
            controle.logarUsuario(usuario.getEmail());
            finish();
        }
    }
}