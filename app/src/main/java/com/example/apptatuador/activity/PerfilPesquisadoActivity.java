package com.example.apptatuador.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.apptatuador.R;
import com.example.apptatuador.adapter.AdapterGrid;
import com.example.apptatuador.helper.ConfiguracaoFirebase;
import com.example.apptatuador.helper.UsuarioFirebase;
import com.example.apptatuador.model.Postagem;
import com.example.apptatuador.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilPesquisadoActivity extends AppCompatActivity {

    private Button btnSeguir;
    private ImageView imageViewPerfil;
    private CircleImageView imgPerfil;
    private TextView txtPublicacoes, txtSeguindo, txtSeguidores;
    private GridView gridViewPerfil;
    private ProgressBar progressBarPerfil;

    private AdapterGrid adapterGrid;

    private Usuario usuarioRecebido;
    private Usuario usuarioLogado;

    private DatabaseReference usuarioRef;
    private DatabaseReference usuarioLogadoRef;
    private DatabaseReference usuarioRecebidoRef;
    private DatabaseReference perfilUsuarios;
    private DatabaseReference seguidoresRef;
    private DatabaseReference postagensUsuarioRef;
    private ValueEventListener valueEventListenerPerfilUsuarios;

    private String idUsuarioLogado;
    private String tipoUsuarioRecebido;
    private String postagens,seguidores,seguindo;
    private List<Postagem> listaPostagens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_pesquisado);

        usuarioRef = ConfiguracaoFirebase.getFirebase().child("tatuadores");
        seguidoresRef = ConfiguracaoFirebase.getFirebase().child("seguidores");
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();

        btnSeguir = findViewById(R.id.btnEditarPerfil);
        btnSeguir.setText("Caregando..");

        //Desabilitando logo toolbar
        imageViewPerfil = findViewById(R.id.imageViewPerfil);
        imageViewPerfil.setVisibility(View.GONE);

        imgPerfil = findViewById(R.id.imgPerfil);
        txtPublicacoes = findViewById(R.id.txtPublicacoes);
        txtSeguidores = findViewById(R.id.txtSeguidores);
        txtSeguindo = findViewById(R.id.txtSeguindo);

        gridViewPerfil = findViewById(R.id.gridPerfil);

        progressBarPerfil = findViewById(R.id.progressBarGridPerfil);
        progressBarPerfil.setVisibility(View.GONE);

        //Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_perfil);
        setSupportActionBar(toolbar);
        //btnVoltar na toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);

        //Recuperar dados do usuario passsados via recyclerClick
        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            usuarioRecebido = (Usuario) bundle.getSerializable("usuarioSelecionado");
            tipoUsuarioRecebido = bundle.getString("tipoUsuario");
            getSupportActionBar().setTitle(usuarioRecebido.getNome());

            //SETANDO FOTO DE PERFIL
            String caminhoFoto = usuarioRecebido.getCaminhoFoto();
            if (caminhoFoto != null){
                Uri url = Uri.parse(caminhoFoto);
                Glide.with(PerfilPesquisadoActivity.this)
                        .load(url)
                        .into(imgPerfil);
            }
        }
        usuarioRecebidoRef = ConfiguracaoFirebase.getFirebase().child(tipoUsuarioRecebido);
        //Criando referencia das postagens
        postagensUsuarioRef = ConfiguracaoFirebase.getFirebase()
                .child("postagens")
                .child(usuarioRecebido.getId());

        carregarPostagem();

        inicializarImageLoader();

        //abre foto clicada
        gridViewPerfil.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Postagem postagem = listaPostagens.get(position);
                Intent intent = new Intent(PerfilPesquisadoActivity.this,VisualizarPostagemActivity.class);
                intent.putExtra("postagem", postagem);
                intent.putExtra("usuario", usuarioRecebido);
                startActivity(intent);
            }
        });
    }

    private void recuperarDadosUsuarioLogado(){
        usuarioLogadoRef = usuarioRef.child(idUsuarioLogado);
        usuarioLogadoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Recupera dados do usuario logado
                usuarioLogado = dataSnapshot.getValue(Usuario.class);

                verificaSegueUsuario();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarDadosPerfilUsuarios();
        recuperarDadosUsuarioLogado();
    }

    @Override
    protected void onStop() {
        super.onStop();
        perfilUsuarios.removeEventListener(valueEventListenerPerfilUsuarios);
    }

    private void recuperarDadosPerfilUsuarios(){
        perfilUsuarios = usuarioRecebidoRef.child(usuarioRecebido.getId());
        valueEventListenerPerfilUsuarios = perfilUsuarios.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Usuario usuario = dataSnapshot.getValue(Usuario.class);
                        if(dataSnapshot.exists()) {
                            //convertendo int para string
                           postagens = String.valueOf(usuario.getPublicacoes());
                           seguidores = String.valueOf(usuario.getSeguidores());
                           seguindo = String.valueOf(usuario.getSeguindo());
                            //Setando valores na tela
                            txtSeguindo.setText(seguindo);
                            txtSeguidores.setText(seguidores);
                            txtPublicacoes.setText(postagens);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }

    private void verificaSegueUsuario(){
        //Pegando id de usuario logado e usuario que vai seguir
        DatabaseReference seguidorRef = seguidoresRef
                .child(usuarioRecebido.getId())
                .child(idUsuarioLogado);
        seguidorRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            //Ja esta seguindo
                            habilitarBotaoSeguir(true);
                            deixarDeSeguir(true);
                        }else {
                            //Ainda nao esta seguindo
                            habilitarBotaoSeguir(false);

                            btnSeguir.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    salvarSeguidor(usuarioLogado, usuarioRecebido);
                                    deixarDeSeguir(true);
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }

    private void inicializarImageLoader(){
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration
                .Builder(this)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .build();
        ImageLoader.getInstance().init(configuration);
    }

    private void carregarPostagem(){
        listaPostagens = new ArrayList<>();
        postagensUsuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Config tamanho do gridLayout
                int tamanhoGrid = getResources().getDisplayMetrics().widthPixels;//getDisplayMetrics() pega o tamanho da tela do celular
                int tamanhoImagem = tamanhoGrid/3;
                gridViewPerfil.setColumnWidth(tamanhoImagem);

                List<String> urlFotos = new ArrayList<>();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Postagem postagem = ds.getValue(Postagem.class);
                    listaPostagens.add(postagem);
                    urlFotos.add(postagem.getCaminhoFoto());
                    Log.i("postagem", "url: "+postagem.getCaminhoFoto());
                }

                //Config Adapter
                adapterGrid = new AdapterGrid(getApplicationContext(),R.layout.grid_postagem,urlFotos);
                gridViewPerfil.setAdapter(adapterGrid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void salvarSeguidor(Usuario usuarioLogado, Usuario usuarioSeguir){
        HashMap<String, Object> dadosUsuarioLogado = new HashMap<>();
        dadosUsuarioLogado.put("nome", usuarioSeguir.getNome());
        dadosUsuarioLogado.put("caminhoFoto", usuarioSeguir.getCaminhoFoto());
        DatabaseReference seguidorRef = seguidoresRef
                .child(usuarioSeguir.getId())
                .child(usuarioLogado.getId());
        seguidorRef.setValue(dadosUsuarioLogado);

        //btnSeguir.setText("Seguindo");
        //btnSeguir.setOnClickListener(null);

        incrementarSeguidores (usuarioLogado, usuarioSeguir);
    }

    private void deixarDeSeguir(boolean seguindo){
        if (seguindo){
            verificaSegueUsuario();
            btnSeguir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();

                    DatabaseReference deixarDeSeguirRef = firebaseRef
                            .child("seguidores")
                            .child(usuarioRecebido.getId())
                            .child(usuarioLogado.getId());
                    deixarDeSeguirRef.removeValue();

                    decrementarSeguidores(usuarioLogado,usuarioRecebido);
                    removerFeedPostagem();
                }
            });
        }
    }

    private void removerFeedPostagem(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();

        DatabaseReference removerFeedRef = firebaseRef
                .child("feed")
                .child(usuarioLogado.getId())
                .child(usuarioRecebido.getId());
        removerFeedRef.removeValue();
    }

    private void decrementarSeguidores (Usuario usuarioLogado, Usuario usuarioSeguir){
        recuperarDadosUsuarioLogado();

        //Decrementar seguindo
        int qtdSeguindo = usuarioLogado.getSeguindo()-1;
        HashMap<String, Object> dadosSeguindo = new HashMap<>();
        dadosSeguindo.put("seguindo",qtdSeguindo);
        DatabaseReference usuarioSeguindo = usuarioRef.child(usuarioLogado.getId());
        usuarioSeguindo.updateChildren(dadosSeguindo);


        //Decrementar seguidores
        int qtdSeguidores = Integer.parseInt(seguidores)-1;
        HashMap<String, Object> dadosSeguidores = new HashMap<>();
        dadosSeguidores.put("seguidores",qtdSeguidores);
        DatabaseReference usuarioSeguidores = usuarioRecebidoRef.child(usuarioSeguir.getId());
        usuarioSeguidores.updateChildren(dadosSeguidores);
    }

    private void incrementarSeguidores (Usuario usuarioLogado, Usuario usuarioSeguir){
        recuperarDadosUsuarioLogado();

        //Incrementar seguindo
        int qtdSeguindo = usuarioLogado.getSeguindo()+1;
        HashMap<String, Object> dadosSeguindo = new HashMap<>();
        dadosSeguindo.put("seguindo",qtdSeguindo);
        DatabaseReference usuarioSeguindo = usuarioRef.child(usuarioLogado.getId());
        usuarioSeguindo.updateChildren(dadosSeguindo);

        //Incrementar seguidores
        int qtdSeguidores = Integer.parseInt(seguidores)+1;
        HashMap<String, Object> dadosSeguidores = new HashMap<>();
        dadosSeguidores.put("seguidores",qtdSeguidores);
        DatabaseReference usuarioSeguidores = usuarioRecebidoRef.child(usuarioSeguir.getId());
        usuarioSeguidores.updateChildren(dadosSeguidores);
    }

    private void habilitarBotaoSeguir(boolean segueUsuario){
        if (segueUsuario){
            btnSeguir.setText("Seguindo");
        }else {
            btnSeguir.setText("Seguir");
        }
    }

    //corrigindo btnVoltar
    @Override
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(PerfilPesquisadoActivity.this, PesquisarActivity.class));
        finish();
        return false;
    }
}