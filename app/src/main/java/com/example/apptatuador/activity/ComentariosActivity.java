package com.example.apptatuador.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.apptatuador.R;
import com.example.apptatuador.adapter.AdapterComentarios;
import com.example.apptatuador.helper.ConfiguracaoFirebase;
import com.example.apptatuador.helper.UsuarioFirebase;
import com.example.apptatuador.model.Comentario;
import com.example.apptatuador.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ComentariosActivity extends AppCompatActivity {
    private ImageView imageViewPerfil;
    private Button btnEnviarComentario;
    private EditText txtComentarioPostagem;
    private RecyclerView recyclerViewComentarios;

    private Usuario usuario;

    private String idPostagem;

    private AdapterComentarios adapterComentarios;

    private List<Comentario> listaComentarios;

    private DatabaseReference firebaseRef;
    private DatabaseReference comentariosRef;
    private ValueEventListener valueEventListenerComentarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);

        firebaseRef = ConfiguracaoFirebase.getFirebase();


        usuario = UsuarioFirebase.getDadosUsuarioLogado();

        listaComentarios = new ArrayList<>();

        recyclerViewComentarios = findViewById(R.id.recyclerViewComentarios);

        btnEnviarComentario = findViewById(R.id.btnEnviarComentarioPost);
        txtComentarioPostagem = findViewById(R.id.txtComentarioPostagem);

        //Desabilitando logo toolbar
        imageViewPerfil = findViewById(R.id.imageViewPerfil);
        imageViewPerfil.setVisibility(View.GONE);

        //Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_perfil);
        setSupportActionBar(toolbar);
        //btnVoltar na toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        getSupportActionBar().setTitle("Comentários");

        adapterComentarios = new AdapterComentarios(listaComentarios,getApplicationContext());
        recyclerViewComentarios.setHasFixedSize(true);
        recyclerViewComentarios.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewComentarios.setAdapter(adapterComentarios);
        recyclerViewComentarios.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));

        //Recuperar IDPostagem
        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            idPostagem = bundle.getString("idPostagem");
        }

        btnEnviarComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salvarComentario();
            }
        });
    }

    private void salvarComentario(){
        String textoComentario = txtComentarioPostagem.getText().toString();
        if (textoComentario != null && !textoComentario.equals("")){
            Comentario comentario = new Comentario();
            comentario.setIdPostagem(idPostagem);
            comentario.setIdUsuario(usuario.getId());
            comentario.setNomeUsuario(usuario.getNomeUsuario());
            comentario.setCaminhoFoto(usuario.getCaminhoFoto());
            comentario.setComentario(textoComentario);
            if (comentario.salvarComentario()){
                Toast.makeText(getApplicationContext(),"Comentário publicado",Toast.LENGTH_SHORT).show();
            }
        }else{
            txtComentarioPostagem.setError("Insira um comentário");
        }
        //Limpar texto
        txtComentarioPostagem.setText("");
    }

    private void recuperarComentarios(){
        comentariosRef = firebaseRef.child("comentarios").child(idPostagem);
        valueEventListenerComentarios = comentariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaComentarios.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    listaComentarios.add(ds.getValue(Comentario.class));
                }
                adapterComentarios.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        comentariosRef.removeEventListener(valueEventListenerComentarios);
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarComentarios();
    }

    //corrigindo btnVoltar
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}