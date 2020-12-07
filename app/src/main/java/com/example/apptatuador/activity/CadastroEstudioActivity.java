package com.example.apptatuador.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.apptatuador.R;
import com.example.apptatuador.helper.GerenciarDialogCarregamento;
import com.example.apptatuador.helper.ConfiguracaoFirebase;
import com.example.apptatuador.helper.CustomTextWatcher;
import com.example.apptatuador.helper.RequisicaoEndereco;
import com.example.apptatuador.helper.UsuarioFirebase;
import com.example.apptatuador.helper.Util;
import com.example.apptatuador.model.Estudio;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class CadastroEstudioActivity extends AppCompatActivity {

    private static final int CODIGO_PERMISSAO = 321;//qlqr numero
    private static final int SELECAO_GALERIA = 200;

    private CircleImageView imgEstudio;
    private TextInputEditText txtCEP, txtBairro, txtNumero, txtNomeEstudio,txtRua,txtComplemento,txtCidade;
    private Spinner spinnerEstados;
    private Util util;
    private Button btnCEP, btnCadastroEstudio;

    private StorageReference storageRef;

    private String identificarUsuario, caminhoFoto;

    private Estudio estudio;

    private boolean fotoEstudio = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_estudio);

        storageRef = ConfiguracaoFirebase.getFirebaseStorage();
        identificarUsuario = UsuarioFirebase.getIdentificadorUsuario();

        //Inicializando componentes
        imgEstudio = findViewById(R.id.imgEstudio);

        txtNomeEstudio = findViewById(R.id.txtNomeEstudio);
        txtCEP = findViewById(R.id.txtCEP);
        txtNumero = findViewById(R.id.txtNumero);
        txtRua = findViewById(R.id.txtRua);
        txtComplemento = findViewById(R.id.txtComplemento);
        txtBairro = findViewById(R.id.txtBairro);
        txtCidade = findViewById(R.id.txtCidade);

        spinnerEstados = findViewById(R.id.spinnerEstado);

        btnCadastroEstudio = findViewById(R.id.btnCadastroEstudio);
        btnCEP = findViewById(R.id.btnCEP);

        imgEstudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificandoPermissoes();
            }
        });

        txtCEP.addTextChangedListener(new CustomTextWatcher(txtCEP, true,1) {
            @Override
            public void textWasChanged() {
                String CodigoCEP = txtCEP.getText().toString();

                if (CodigoCEP.length() == 8) {
                    new RequisicaoEndereco(CadastroEstudioActivity.this).execute();
                }
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(this,
                        R.array.estados,
                        android.R.layout.simple_spinner_item);
        spinnerEstados.setAdapter(adapter);

        util = new Util(this,
                R.id.txtRua,
                R.id.txtBairro,
                R.id.txtCidade,
                R.id.spinnerEstado);

        //desativando os campos de preenchimentos
        bloquearCampos(true);

        btnCadastroEstudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fotoEstudio){
                    salvarDados();
                    Intent i = new Intent(CadastroEstudioActivity.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);//Limpando pilha de activities
                    startActivity(i);
                    finish();
                }
                else
                    Toast.makeText(CadastroEstudioActivity.this, "Insira uma foto do estúdio", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Estudio.REQUEST_CEP_CODIGO && resultCode == RESULT_OK) {
            txtCEP.setText(data.getStringExtra(Estudio.CEP_KEY));
        }
        if (requestCode == SELECAO_GALERIA) {
            GerenciarDialogCarregamento.mostrarDialog(CadastroEstudioActivity.this,"Atualizando imagem");
            Bitmap imagem = null;

            try {
                Uri localImagemSelecionada = data.getData();
                imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);
                //caso tenha escolhido uma imagem
                if (imagem != null) {
                    //mostra imagem na tela
                    imgEstudio.setImageBitmap(imagem);

                    //Recuperar dados da imagem para o firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    //Salvar imagem no firebase
                    final StorageReference imagemRef = storageRef
                            .child("imagens")
                            .child("estudio")
                            .child(identificarUsuario + ".jpeg");

                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Erro ao fazer upload da imagem", Toast.LENGTH_LONG).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getApplicationContext(), "Sucesso ao fazer upload da imagem", Toast.LENGTH_LONG).show();

                            //preparando para salvar foto no DB
                            imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri url = task.getResult();
                                    caminhoFoto = url.toString();
                                    fotoEstudio = true;
                                    GerenciarDialogCarregamento.fecharDialog();
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

    public void verificandoPermissoes() {
        String[] permissoes = {Manifest.permission.READ_EXTERNAL_STORAGE};

        //Verificando se ja foi verificado ou nao
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissoes[0]) == PackageManager.PERMISSION_GRANTED)
        {

            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            if (i.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(i, SELECAO_GALERIA);
            }

        } else {
            ActivityCompat.requestPermissions(CadastroEstudioActivity.this, permissoes, CODIGO_PERMISSAO);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        verificandoPermissoes();
    }

    private String getCodigoCEP() {
        return txtCEP.getText().toString();
    }

    public String getUriRequest() {
        return "https://viacep.com.br/ws/" + getCodigoCEP() + "/json/";
    }

    public void bloquearCampos(boolean isToLock) {
        util.camposDesabilitados(isToLock);
    }

    public void setCamposDeEndereco(Estudio estudio) {
        setCampos(R.id.txtRua, estudio.getLogradouro());
        setCampos(R.id.txtBairro, estudio.getBairro());
        setCampos(R.id.txtCidade, estudio.getLocalidade());
        setSpinner(R.id.spinnerEstado, R.array.estados, estudio.getUf());
    }

    private void setCampos(int fieldId, String data) {
        ((TextInputEditText) findViewById(fieldId)).setText(data);
    }

    private void setSpinner(int fieldId, int arrayId, String uf) {
        spinnerEstados = findViewById(fieldId);
        String[] estados = getResources().getStringArray(arrayId);

        for (int i = 0; i < estados.length; i++) {
            if (estados[i].endsWith("(" + uf + ")")) {
                spinnerEstados.setSelection(i);
                break;
            }
            //caso nao encontre
            spinnerEstados.setSelection(0);
        }
    }

    public void buscarCEP(View view) {
        Intent intent = new Intent(this, BuscaCepActivity.class);
        startActivityForResult(intent, Estudio.REQUEST_CEP_CODIGO);
    }

    public void salvarDados(){
        //Instanciando objeto
        estudio = new Estudio();
        //Populando objeto
        estudio.setCaminhoFoto(caminhoFoto);

        String idEstudio = UsuarioFirebase.getIdentificadorUsuario();
        estudio.setId(idEstudio);

        estudio.setNomeEstudio(txtNomeEstudio.getText().toString());
        estudio.setNomePesquisaEstudio(txtNomeEstudio.getText().toString());
        estudio.setCep(txtCEP.getText().toString());
        estudio.setBairro(txtBairro.getText().toString());
        estudio.setLogradouro(txtRua.getText().toString());
        estudio.setLocalidade(txtCidade.getText().toString());
        estudio.setComplemento(txtComplemento.getText().toString());
        estudio.setUf(spinnerEstados.getSelectedItem().toString());
        estudio.setNumeroCasa(txtNumero.getText().toString());

        estudio.salvarEstudio();
        estudio.funcionarioEstudio(idEstudio,estudio);
    }
}