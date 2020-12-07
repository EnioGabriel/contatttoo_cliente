package com.example.apptatuador.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.apptatuador.R;
import com.example.apptatuador.helper.GerenciarDialogCarregamento;
import com.example.apptatuador.helper.ConfiguracaoFirebase;
import com.example.apptatuador.helper.CustomTextWatcher;
import com.example.apptatuador.helper.UsuarioFirebase;
import com.example.apptatuador.model.Usuario;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditarPerfilActivity extends AppCompatActivity {

    private static final int CODIGO_PERMISSAO = 123;//qlqr numero
    private static final int SELECAO_GALERIA = 200;

    private Button btnSalvarAlteracoes;
    private TextView txtAlterarFoto;
    private TextInputEditText txtEditNome, txtEditNomeUsuario, txtEditEmail, txtEditCelular;
    private CircleImageView imgAlterarFoto;

    private Usuario usuarioLogado;

    private StorageReference storageRef;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private DatabaseReference raizRef;

    private String identificarUsuario;
    private boolean repeteNome = true;
    private List<String> listaUsuarios;

    private String editNomeUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        listaUsuarios = new ArrayList<>();

        storageRef = ConfiguracaoFirebase.getFirebaseStorage();//Armazenamento de medias
        identificarUsuario = UsuarioFirebase.getIdentificadorUsuario();
        auth = ConfiguracaoFirebase.getFirebaseAutenticacao();

        raizRef = ConfiguracaoFirebase.getFirebase().child("nomesUsuarios");

        database = FirebaseDatabase.getInstance();
        ref = database.getReference().child("tatuadores").child(identificarUsuario);

        //Config iniciais
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        //Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_perfil);
        setSupportActionBar(toolbar);
        //btnVoltar na toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);

        btnSalvarAlteracoes = findViewById(R.id.btnSalvarAlteracoes);
        txtEditNome = findViewById(R.id.txtEditNome);
        txtEditNomeUsuario = findViewById(R.id.txtEditNomeUsuario);
        txtEditEmail = findViewById(R.id.txtEditEmail);
        txtEditCelular = findViewById(R.id.txtEditCelular);
        imgAlterarFoto = findViewById(R.id.imgAlterarFoto);
        txtAlterarFoto = findViewById(R.id.txtAlterarFoto);
        txtEditEmail.setFocusable(false);//impede usuario de digitar no campo

        recuperandoDadosDB();

        txtEditNomeUsuario.addTextChangedListener(new CustomTextWatcher(txtEditNomeUsuario,false,500) {
            @Override
            public void textWasChanged() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        repeteNome = verificaNomeUsuario(txtEditNomeUsuario.getText().toString());
                    }
                });
            }
        });

        //Formatando Campo
        SimpleMaskFormatter smfCelular  = new SimpleMaskFormatter("(NN)NNNNN-NNNN");
        MaskTextWatcher mtwCelular  = new MaskTextWatcher(txtEditCelular, smfCelular);

        txtEditCelular.addTextChangedListener(mtwCelular);

        //Recuperar dados Usuario
        final FirebaseUser usuarioPerfl = UsuarioFirebase.getUsuarioAtual();
        txtEditNome.setText(usuarioPerfl.getDisplayName());
        txtEditEmail.setText(usuarioPerfl.getEmail());
        //txtEditCelular.setText();
        Uri url = usuarioPerfl.getPhotoUrl();
        if (url != null) {
            Glide.with(EditarPerfilActivity.this)
                    .load(url)
                    .into(imgAlterarFoto);
        }else {
            imgAlterarFoto.setImageResource(R.drawable.avatar);
        }
        //Salvar alteracoes do nome
        btnSalvarAlteracoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!repeteNome){
                    String nomeAtualizado = txtEditNome.getText().toString();
                    String numeroAtualizado = txtEditCelular.getText().toString();
                    String nomeUsuarioAtualizado = txtEditNomeUsuario.getText().toString();
                    //atualizar nome no firbase
                    UsuarioFirebase.atualizarNome(nomeAtualizado);

                    //Atualizar dados no banco
                    usuarioLogado.setNome(nomeAtualizado);
                    usuarioLogado.setNomeUsuario(nomeUsuarioAtualizado);
                    usuarioLogado.setNomePesquisa(nomeAtualizado);
                    usuarioLogado.setCelular(numeroAtualizado);
                    usuarioLogado.atualizarDados();
                }
            }
        });

        imgAlterarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificandoPermissoes();
            }
        });

        txtAlterarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificandoPermissoes();
            }
        });
    }

    private boolean verificaNomeUsuario(String nome){
        for (int i=0; i<listaUsuarios.size(); i++){
            if (listaUsuarios.get(i).equals(nome)) {
                txtEditNomeUsuario.setError("Esse nome de usuário já esta em uso, tente outro!");
                return true;
            }
        }
        return false;
    }

    private void recuperarNomesCadastrados(){
        raizRef.orderByValue().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaUsuarios.clear();
                String[] nomeUsuario;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    nomeUsuario = snapshot.getValue().toString().split(",");
                    Log.i("USUARIOS","usuarios "+nomeUsuario.length);
                    for (int i = 0; i < nomeUsuario.length; i++) {
                        if (nomeUsuario.length >= 2) {
                            String texto[] = nomeUsuario[i].split("=");
                            int tamanhoString = texto[2].length() - 1;//definindo tamanho da String
                            String nomeUsuarioFormatado = texto[2].substring(0, tamanhoString);
                            //impedindo que o nome de usuario atual entre na lista
                            if (txtEditNomeUsuario.getText().toString().equals(nomeUsuarioFormatado)||editNomeUsuario.equals(nomeUsuarioFormatado)) {
                                continue;//volta para o inicio do for
                            }
                            Log.i("USUARIOS", "usuario formatado "+nomeUsuarioFormatado);
                            Log.i("USUARIOS", "usuario formatado "+editNomeUsuario);
                            listaUsuarios.add(nomeUsuarioFormatado.replace("}", ""));
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void recuperandoDadosDB(){
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);

                String celular = (usuario.getCelular());;
                txtEditCelular.setText(celular);

                String nomeUsuario = (usuario.getNomeUsuario());;
                txtEditNomeUsuario.setText(nomeUsuario);
                editNomeUsuario = txtEditNomeUsuario.getText().toString().concat("}");

                recuperarNomesCadastrados();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    public void deslogarUsuario(){
        try {
            auth.signOut();
            Intent i = new Intent(EditarPerfilActivity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);//Limpando pilha de activities
            startActivity(i);
            finish();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Erro ao deslogar usuário"+ e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_perfil, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_sair:
                new AlertDialog.Builder(this, R.style.AppTheme_Dialog)
                        .setTitle("Logout")
                        .setMessage("Você deseja sair dessa conta?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                deslogarUsuario();
                            }
                        })
                        .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void verificandoPermissoes(){
        String[] permissoes = {Manifest.permission.READ_EXTERNAL_STORAGE};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissoes[0]) == PackageManager.PERMISSION_GRANTED)
        {

            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            if (i.resolveActivity(getPackageManager())!=null){
                startActivityForResult(i, SELECAO_GALERIA);
            }

        }else {
            ActivityCompat.requestPermissions(EditarPerfilActivity.this, permissoes,CODIGO_PERMISSAO);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        verificandoPermissoes();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            GerenciarDialogCarregamento.mostrarDialog(EditarPerfilActivity.this,"Atualizando foto");
            Bitmap imagem = null;
            try {
                //Selecao apenas da galeria de fotos
                switch (requestCode){
                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);
                }
                //caso tenha sido escolhido uma imagem
                if (imagem!=null){
                    //mostra imagem na tela
                    imgAlterarFoto.setImageBitmap(imagem);

                    //Recuperar dados da imagem para o firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    //Salvar imagem no firebase
                    final StorageReference imagemRef = storageRef
                            .child("imagens")
                            .child("perfil")
                            .child(identificarUsuario+".jpeg");

                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            GerenciarDialogCarregamento.fecharDialog();
                            Toast.makeText(getApplicationContext(), "Erro ao fazer upload da imagem", Toast.LENGTH_LONG).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //preparando para salvar foto no DB
                            imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri url = task.getResult();
                                    atualizarFotoUsuario(url);
                                }
                            });
                        }
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void atualizarFotoUsuario(Uri url){
        //Atualizar foto no perfil
        UsuarioFirebase.atualizarFotoUsuario(url);

        //Atualizar foto no Firebase
        usuarioLogado.setCaminhoFoto(url.toString());
        usuarioLogado.setNomeUsuario(txtEditNomeUsuario.getText().toString());
        usuarioLogado.setCelular(txtEditCelular.getText().toString());//CORRIGIR ESSE BUG DPS
        usuarioLogado.atualizarDados();

        GerenciarDialogCarregamento.fecharDialog();
    }

    //corrigindo btnVoltar
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}