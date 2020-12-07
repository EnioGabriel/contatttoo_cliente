package com.example.apptatuador.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.apptatuador.R;
import com.example.apptatuador.helper.ConfiguracaoFirebase;
import com.example.apptatuador.helper.CustomTextWatcher;
import com.example.apptatuador.helper.UsuarioFirebase;
import com.example.apptatuador.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CadastroDadosContaActivity extends AppCompatActivity {
    private TextInputEditText txtCadEmail,txtCadSenha,txtCadConfirmaSenha, txtCadNomeUsuario;
    private String nome,cpf,celular;
    private Button btnFinalizar;
    private Usuario usuario;
    private FirebaseAuth autenticacao;
    private Boolean senhaConfere = false, repeteNome = true;
    private DatabaseReference raiz;
    private DatabaseReference nomesUsuarios;
    private List<String> listaTatuadores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_dados_conta);

        listaTatuadores = new ArrayList<>();
        //tatuadorRef = ConfiguracaoFirebase.getFirebase().child("tatuadores");
        raiz = ConfiguracaoFirebase.getFirebase().child("nomesUsuarios");
        nomesUsuarios = raiz.child("TATUADORES");

        txtCadEmail = findViewById(R.id.txtCadEmail);
        txtCadSenha = findViewById(R.id.txtCadSenha);
        txtCadConfirmaSenha = findViewById(R.id.txtCadConfirmaSenha);
        txtCadNomeUsuario = findViewById(R.id.txtCadNomeUsuario);
        btnFinalizar = findViewById(R.id.btnFinalizar);

        /*
        //pegando dados digitados
        email = txtCadEmail.getText().toString();
        senha = txtCadSenha.getText().toString();
        confirmarSenha = txtCadConfirmaSenha.getText().toString();
         */

        txtCadNomeUsuario.addTextChangedListener(new CustomTextWatcher(txtCadNomeUsuario, false, 1000) {
            @Override
            public void textWasChanged() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                      repeteNome = verificaNomeUsuario(txtCadNomeUsuario.getText().toString());
                    }
                });
            }
        });

        //Tratando erros após usuario digitar
        txtCadEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (txtCadEmail.length()>0) {
                    //verificando se o email é válido
                    if (!validaEmail(txtCadEmail.getText().toString())) {
                        txtCadEmail.setError("Email inválido");
                        return;
                    }
                    //Tratando erros do Firebase
                    autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
                    autenticacao.fetchSignInMethodsForEmail(txtCadEmail.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                    boolean isNewUser = task.getResult().getSignInMethods().isEmpty();
                                    if (!isNewUser) {
                                        txtCadEmail.setError("Este e-mail já esta em uso, utilize outro!");
                                    }
                                }
                            });
                }
            }
        });

        /*
        txtCadSenha.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                int tamNum = txtCadSenha.length();
                if (tamNum<=5){
                    txtCadSenha.setError("A senha deve ter no mínimo 6 caracteres");
                }

            }
        });
         */
        txtCadSenha.addTextChangedListener(new CustomTextWatcher(txtCadSenha,false,1500) {
            @Override
            public void textWasChanged() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int tamNum = txtCadSenha.length();
                        if (tamNum<=5){
                            txtCadSenha.setError("A senha deve ter no mínimo 6 caracteres");
                        }
                    }
                });
            }
        });

        txtCadConfirmaSenha.addTextChangedListener(new CustomTextWatcher(txtCadConfirmaSenha, false,1500) {
            @Override
            public void textWasChanged() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(txtCadConfirmaSenha.length()>0) {
                            if (txtCadSenha.getText().toString().equals(txtCadConfirmaSenha.getText().toString())) {
                                senhaConfere = true;
                            } else {
                                txtCadConfirmaSenha.setError("As senhas não conferem");
                                senhaConfere = false;
                            }
                        }
                    }
                });
            }
        });

        /*
        txtCadConfirmaSenha.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(txtCadConfirmaSenha.length()>0) {
                    if (txtCadSenha.getText().toString().equals(txtCadConfirmaSenha.getText().toString())) {
                        senhaConfere = true;
                    } else {
                        txtCadConfirmaSenha.setError("As senhas não conferem");
                        senhaConfere = false;
                    }
                }
            }
        });
         */

        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (campoDigitado()&&senhaConfere&&!repeteNome){
                    populandoObjeto();
                    cadastrarUsuario();
                }
            }
        });
    }//Fim onCreate

    @Override
    protected void onStart() {
        super.onStart();
        recebeDadosIntent();
        recuperarNomesCadastrados();
    }

    //colocando nome de usuario em um nó diferente, para facilitar a busca
    private void salvarNomeUsuarios(){
        DatabaseReference nomesUsuariosRef = nomesUsuarios.child(usuario.getId()).child("nome");
        nomesUsuariosRef.setValue(usuario.getNomeUsuario());
    }

    private void recuperarNomesCadastrados(){
        raiz.orderByValue().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaTatuadores.clear();
                String[] nomeUsuario;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    nomeUsuario = snapshot.getValue().toString().split(",");
                    for (int i = 0; i < nomeUsuario.length; i++) {
                        String texto[] = nomeUsuario[i].split("=");
                        Log.i("Nomesgeral", "nomes " + texto[2]);
                        int tamanhoString = texto[2].length() - 1;//definindo tamanho da String
                        String nomeUsuarioFormatado = texto[2].substring(0, tamanhoString);
                        listaTatuadores.add(nomeUsuarioFormatado.replace("}", ""));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private boolean verificaNomeUsuario(String nome){
        for (int i=0; i<listaTatuadores.size(); i++){
            if (listaTatuadores.get(i).equals(nome)) {
                txtCadNomeUsuario.setError("Esse nome de usuário já esta em uso, tente outro!");
                return true;
            }
        }
        return false;
    }

    //pegando dados da 1° activity
    public void recebeDadosIntent(){
        Intent intent = getIntent();
        nome        = intent.getStringExtra("nome");
        cpf         = intent.getStringExtra("cpf");
        celular     = intent.getStringExtra("celular");
    }

    //Valida Email de acordo com o Pattern do android
    private boolean validaEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    private void cadastrarUsuario(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(CadastroDadosContaActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    String idUsuario = task.getResult().getUser().getUid();
                    usuario.setId(idUsuario);
                    usuario.salvarDados();

                    //salvar dados no profile do firebase
                    UsuarioFirebase.atualizarNome(usuario.getNome());

                    salvarNomeUsuarios();
                    abrirLoginUsuario();

                }else {
                    try {
                        throw task.getException();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(),"Erro ao cadastrar usuário "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void abrirLoginUsuario(){
        Intent intent = new Intent(CadastroDadosContaActivity.this, PesquisaEstudioActivity.class);
        startActivity(intent);
        finish();
        /*
        setResult(1000);
        finishActivity(1000);
         */
    }

    private void populandoObjeto(){
        //Instanciando objeto
        usuario = new Usuario();
        //Pupulando objeto
        usuario.setNome(nome);
        usuario.setNomePesquisa(nome);
        usuario.setNomeUsuario(txtCadNomeUsuario.getText().toString());
        usuario.setCpf(cpf);
        usuario.setCelular(celular);
        usuario.setEmail(txtCadEmail.getText().toString());
        usuario.setSenha(txtCadSenha.getText().toString());
    }

    private boolean campoDigitado(){
        if(txtCadNomeUsuario.getText().toString().equals("")){
            txtCadNomeUsuario.setError("Preencha esse campo!");
            return false;
        }
        else if (txtCadEmail.getText().toString().equals("")){
            txtCadEmail.setError("Preencha esse campo!");
            return false;
        }else if (txtCadSenha.getText().toString().equals("")){
            txtCadSenha.setError("Preencha esse campo!");
            return false;
        }else if(txtCadConfirmaSenha.getText().toString().equals("")){
            txtCadConfirmaSenha.setError("Preencha esse campo!");
            return false;
        }
        return true;
    }
}