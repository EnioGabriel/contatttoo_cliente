package com.example.apptatuador.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.apptatuador.R;
import com.example.apptatuador.helper.ConfiguracaoFirebase;
import com.example.apptatuador.helper.CustomTextWatcher;
import com.example.apptatuador.helper.UsuarioFirebase;
import com.example.apptatuador.model.Usuario;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NomearUsuarioActivity extends AppCompatActivity {

    private Button btnFinalizarUsuario;
    private TextInputEditText txtCadNomeUsuario;

    private DatabaseReference usuarioRef;

    private List<String> listaTatuadores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nomear_usuario);

        listaTatuadores = new ArrayList<>();
        usuarioRef = ConfiguracaoFirebase.getFirebase().child("tatuadores");

        btnFinalizarUsuario = findViewById(R.id.btnFinalizarUsuario);
        btnFinalizarUsuario.setEnabled(false);
        txtCadNomeUsuario = findViewById(R.id.txtCadastrarNomeUsuario);

        txtCadNomeUsuario.addTextChangedListener(new CustomTextWatcher(txtCadNomeUsuario, false, 1) {
            @Override
            public void textWasChanged() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnFinalizarUsuario.setEnabled(false);
                        verificaNomeUsuario(txtCadNomeUsuario.getText().toString());
                    }
                });
            }
        });

        btnFinalizarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!txtCadNomeUsuario.getText().toString().isEmpty()) {
                    salvarNomeUsuario();
                    startActivity(new Intent(NomearUsuarioActivity.this, MainActivity.class));
                    finish();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarNomesCadastrados();
    }
    private void salvarNomeUsuario(){
        Usuario usuario = new Usuario();
        usuario.setNomeUsuario(txtCadNomeUsuario.getText().toString());
        usuario.setId(UsuarioFirebase.getIdentificadorUsuario());
        usuario.salvarDados();
    }

    private void recuperarNomesCadastrados(){
        Query query = usuarioRef.orderByChild("nome");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaTatuadores.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Usuario usuario = snapshot.getValue(Usuario.class);
                    listaTatuadores.add(usuario.getNome());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private boolean verificaNomeUsuario(String nome){
        for (int i=0; i<listaTatuadores.size(); i++){
            Log.i("NomeTatuadores", "nomes "+listaTatuadores.get(i));
            if (listaTatuadores.get(i).equals(nome)) {
                txtCadNomeUsuario.setError("Já possui usuário com esse nome, tente outro!");
                btnFinalizarUsuario.setEnabled(false);
                return false;
            }
        }
        btnFinalizarUsuario.setEnabled(true);
        return true;
    }
}