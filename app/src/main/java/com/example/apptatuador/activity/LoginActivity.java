package com.example.apptatuador.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.apptatuador.R;
import com.example.apptatuador.helper.ConfiguracaoFirebase;
import com.example.apptatuador.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private Button btnEntrar;
    private EditText txtEmail, txtSenha;
    private ProgressBar progressLogin;
    private TextView txtCadastro;
    private Usuario usuario;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //verifica se o usuario ja esta logado, impedido de fazer login toda hora
        verificarUsuarioLogado();

        progressLogin = findViewById(R.id.progressLogin);
        //esconde a progress
        progressLogin.setVisibility(View.GONE);

        txtEmail = findViewById(R.id.txtEmail);
        txtSenha = findViewById(R.id.txtSenha);
        txtCadastro = findViewById(R.id.txtCadastro);

        txtCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CadastroDadosPessoaisActivity.class);
                startActivity(intent);
                //finish();
            }
        });

        btnEntrar = findViewById(R.id.btnEntrar);

        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textoEmail = txtEmail.getText().toString();
                String textoSenha = txtSenha.getText().toString();

                if (!textoEmail.isEmpty()){
                    if (!textoSenha.isEmpty()){
                        usuario = new Usuario();
                        usuario.setEmail(textoEmail);
                        usuario.setSenha(textoSenha);
                        validarLogin(usuario);
                    }else {
                        txtEmail.setError("Digite a senha!");
                    }
                }else {
                    txtEmail.setError("Digite o e-mail!");
                }
            }
        });
    }

    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    //Verifica se o usuario que fez o login esta no grupo de Tatuadores
    public void isUsuario(String idUsuario ) {
        DatabaseReference usuarioRef = ConfiguracaoFirebase.getFirebase().child("tatuadores").child(idUsuario);
        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    progressLogin.setVisibility(View.GONE);
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }else {
                    auth.signOut();
                    Toast.makeText(getApplicationContext(), "Erro ao fazer login, certifique-se que é um usuário do tipo tatuador", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void verificarUsuarioLogado(){
        auth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        if (auth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    public void validarLogin(Usuario usuario){
        progressLogin.setVisibility(View.VISIBLE);
        auth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        auth.signInWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    isUsuario(auth.getUid());
                    progressLogin.setVisibility(View.GONE);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Erro ao fazer login", Toast.LENGTH_SHORT).show();
                    progressLogin.setVisibility(View.GONE);
                }
            }
        });
    }
}