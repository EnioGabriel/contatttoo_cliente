package com.example.apptatuador.activity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.apptatuador.R;
import com.example.apptatuador.model.Postagem;
import com.example.apptatuador.model.Usuario;

import de.hdodenhof.circleimageview.CircleImageView;

public class VisualizarPostagemActivity extends AppCompatActivity {
    private ImageView imgPostagemSelecionada;
    private CircleImageView imgPerfilPostagem;
    private TextView txtCurtidas;
    private TextView txtNomeUsuarioPostagem;
    private TextView txtDescricaoPostagemSelecionada;
    private ImageView imgVisualizarComentariosPostagem;

    private Usuario usuarioPostagem;
    private Postagem postagemSelecionadaUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_postagem);

        imgPostagemSelecionada = findViewById(R.id.imgPostagemSelecionada);
        imgPerfilPostagem = findViewById(R.id.imgPerfilPostagem);
        txtCurtidas = findViewById(R.id.lblQtdCurtidas);
        txtNomeUsuarioPostagem = findViewById(R.id.lblNomeUsuarioPostagem);
        txtDescricaoPostagemSelecionada = findViewById(R.id.lblDescricaoPostagemSelecionada);
        imgVisualizarComentariosPostagem = findViewById(R.id.imgComentarioFeed);


        //Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Publicação");
        //btnVoltar na toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);

        //Recuperar dados do usuario passsados pelo recyclerClick
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            usuarioPostagem = (Usuario) bundle.getSerializable("usuario");
            postagemSelecionadaUsuario = (Postagem) bundle.getSerializable("postagem");

            //SETANDO FOTO DE PERFIL
            String caminhoFotoPerfil = usuarioPostagem.getCaminhoFoto();
            if (caminhoFotoPerfil != null) {
                Uri url = Uri.parse(caminhoFotoPerfil);
                Glide.with(VisualizarPostagemActivity.this)
                        .load(url)
                        .into(imgPerfilPostagem);
            }
            txtNomeUsuarioPostagem.setText(usuarioPostagem.getNomeUsuario());

            //SETANDO Postagem
            String caminhoFotoPostagem = postagemSelecionadaUsuario.getCaminhoFoto();
            if (caminhoFotoPostagem != null) {
                Uri urlPostagem = Uri.parse(caminhoFotoPostagem);
                Glide.with(VisualizarPostagemActivity.this)
                        .load(urlPostagem)
                        .into(imgPostagemSelecionada);
            }
            txtDescricaoPostagemSelecionada.setText(postagemSelecionadaUsuario.getDescricao());
        }
    }
    //corrigindo btnVoltar
    @Override
    public boolean onSupportNavigateUp () {
        finish();
        return false;
    }
}