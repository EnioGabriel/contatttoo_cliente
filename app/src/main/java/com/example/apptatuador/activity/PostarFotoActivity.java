package com.example.apptatuador.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.apptatuador.R;
import com.example.apptatuador.helper.ConfiguracaoFirebase;
import com.example.apptatuador.helper.UsuarioFirebase;
import com.example.apptatuador.model.Postagem;
import com.example.apptatuador.model.Usuario;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class PostarFotoActivity extends AppCompatActivity {

    private ImageView imgFotoPostagem;
    private Bitmap imagem;
    private TextInputEditText txtDescricaoPostagem;

    private Usuario usuarioLogado;

    private AlertDialog dialog;

    private DatabaseReference usuarioLogadoRef;
    private DatabaseReference tatuadorRef;
    private DatabaseReference firebaseRef;

    private DataSnapshot seguidoresSnapshot;

    private int numPostagens;
    private String idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postar_foto);

        imgFotoPostagem = findViewById(R.id.imgFotoPostagem);
        txtDescricaoPostagem = findViewById(R.id.txtDescricaoPostagem);

        tatuadorRef = ConfiguracaoFirebase.getFirebase().child("tatuadores");

        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();

        firebaseRef = ConfiguracaoFirebase.getFirebase();

        recuperarDadosPostagem();

        //Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_perfil);
        setSupportActionBar(toolbar);
        //btnVoltar na toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            byte[] dadosImagem = bundle.getByteArray("fotoSelecionada");
            imagem = BitmapFactory.decodeByteArray(dadosImagem, 0, dadosImagem.length);
            imgFotoPostagem.setImageBitmap(imagem);
        }
    }

    private void publicarPostagem() {
        abrirDialogCarregamento("Salvando postagem");
        final Postagem postagem = new Postagem();
        postagem.setIdUsuario(idUsuarioLogado);
        postagem.setDescricao(txtDescricaoPostagem.getText().toString());

        //Recuperar dados da imagem para o firebase
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] dadosImagem = baos.toByteArray();

        //Salvar no firebase storage
        StorageReference storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        final StorageReference imagemRef = storageReference
                .child("imagens")
                .child("postagens")
                .child(postagem.getIdPostagem() + ".jpeg");

        UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Erro ao salvar postagem, tente novamente", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagemRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri url) {
                        postagem.setCaminhoFoto(url.toString());

                        //Atualizar num de postagens
                        numPostagens = usuarioLogado.getPublicacoes() + 1;
                        usuarioLogado.setPublicacoes(numPostagens);
                        usuarioLogado.atualizarQtdPostagem();

                        //salvando postagem
                        if (postagem.salvarPostagem(seguidoresSnapshot)) {
                            Toast.makeText(getApplicationContext(), "Sucesso ao salvar postagem", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                            finish();
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void abrirDialogCarregamento(String titulo) {

        AlertDialog.Builder alert = new AlertDialog.Builder(this,R.style.Dialog);
        alert.setTitle(titulo);
        alert.setCancelable(false);//bloqueia a tela
        alert.setView(R.layout.carregamento);

        dialog = alert.create();
        dialog.show();
    }

    private void recuperarDadosPostagem() {
        abrirDialogCarregamento("Carregando dados, aguarde!");
        usuarioLogadoRef = tatuadorRef.child(idUsuarioLogado);
        usuarioLogadoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Recupera dados do usuario logado
                usuarioLogado = dataSnapshot.getValue(Usuario.class);

                //recuperar seguidores
                DatabaseReference seguidoresRef = firebaseRef
                        .child("seguidores")
                        .child(idUsuarioLogado);
                seguidoresRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        seguidoresSnapshot = dataSnapshot;
                        dialog.cancel();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_postagem, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ic_salvar_postagem:
                publicarPostagem();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}