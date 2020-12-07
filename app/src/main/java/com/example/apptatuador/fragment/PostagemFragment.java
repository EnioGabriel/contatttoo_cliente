package com.example.apptatuador.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.example.apptatuador.R;
import com.example.apptatuador.activity.PostarFotoActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PostagemFragment extends Fragment {

    private static final int CODIGO_PERMISSAO = 1;//qlqr numero
    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;
    private Context contexto;
    private FloatingActionButton fabAddPostagem;
    final CharSequence[] opcoes = {"Abrir câmera", "Abrir galeria", "Cancelar"};
    private boolean abreCamera=false, abreGaleria=false;

    public PostagemFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        fabAddPostagem = view.findViewById(R.id.fabAddPostagem);
        fabAddPostagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirDialog();
            }
        });

        return view;
    }

    private void abrirDialog(){
        new MaterialDialog.Builder(getContext())
                .theme(Theme.DARK)
                .items(opcoes)
                .backgroundColor(getResources().getColor(R.color.cor_fundo))
                .itemsColor(getResources().getColor(R.color.cor_botoes))
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        if (opcoes[position].equals("Abrir câmera")) {
                            abreCamera = true;
                            abreGaleria = false;
                            verificaPermissaoCamera();

                        } else if (opcoes[position].equals("Abrir galeria")) {
                            abreGaleria = true;
                            abreCamera = false;
                            verificaPermissaoGaleria();
                        } else if (opcoes[position].equals("Cancelar")) {
                            dialog.dismiss();
                        }
                    }
                })
                .titleGravity(GravityEnum.CENTER)
                .itemsGravity(GravityEnum.CENTER)
                .show();
    }

    /*
    private void abrirDialog(){
        new AlertDialog.Builder(getContext(), R.style.AppCompatAlertDialogStyle)
                .setTitle("Adicionar postagem")
                .setItems(opcoes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (opcoes[item].equals("Abrir câmera")) {
                            abreCamera = true;
                            abreGaleria = false;
                            verificandoPermissoes();

                        } else if (opcoes[item].equals("Abrir galeria")) {
                            abreGaleria = true;
                            abreCamera = false;
                            verificandoPermissoes();
                        } else if (opcoes[item].equals("Cancelar")) {
                            dialog.dismiss();
                        }
                    }
                })
                .show();
    }
     */

    public void abrirCamera(){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getActivity().getPackageManager())!= null){
                startActivityForResult(intent,SELECAO_CAMERA);
        }
    }
    public void abrirGaleria(){
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            if (intent.resolveActivity(getActivity().getPackageManager())!=null){
                startActivityForResult(intent,SELECAO_GALERIA);
        }
    }

    public boolean verificaPermissaoCamera(){
        String[] permissao = new String[]{Manifest.permission.CAMERA};

        if (ContextCompat.checkSelfPermission(getContext(),
                permissao[0]) == PackageManager.PERMISSION_GRANTED&&abreCamera){
            abrirCamera();
            return true;
        }
        else {
            requestPermissions(permissao, CODIGO_PERMISSAO);
            return false;
        }
    }

    public boolean verificaPermissaoGaleria(){
        String[] permissao = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};

        if (ContextCompat.checkSelfPermission(getContext(),
                permissao[0]) == PackageManager.PERMISSION_GRANTED&&abreGaleria){
            abrirGaleria();
            return true;
        }
        else {
            requestPermissions(permissao, CODIGO_PERMISSAO);
            return false;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        contexto = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        contexto = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Caso as permissoes sejam aceitas
        if ( grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (abreGaleria){
                abrirGaleria();
            }else {
                abrirCamera();
            }
        //Caso as permissoes estejam negadas
        } else {
            if (abreGaleria){
                verificaPermissaoGaleria();
            }else {
                verificaPermissaoCamera();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            Bitmap imagem = null;

            try {
                switch (requestCode) {
                    case SELECAO_CAMERA:
                        imagem = (Bitmap) data.getExtras().get("data");
                        break;
                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), localImagemSelecionada);
                        break;
                }
                //Valida imagem selecionada
                if (imagem != null) {
                    //Converter imagem em byte array
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    //Enviar imagem para aplicaçao de filtro
                     Intent i = new Intent(getActivity(), PostarFotoActivity.class);
                     i.putExtra("fotoSelecionada", dadosImagem);
                     startActivity(i);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}