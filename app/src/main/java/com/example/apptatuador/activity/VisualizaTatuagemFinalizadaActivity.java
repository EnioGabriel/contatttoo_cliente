package com.example.apptatuador.activity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.apptatuador.R;
import com.example.apptatuador.model.Evento;


public class VisualizaTatuagemFinalizadaActivity extends AppCompatActivity {

    private ImageView imgTattooFinalizada;
    private EditText txtNomeTatuador, txtValor;

    private Evento tatuagemRecebida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualiza_tatuagem_finalizada);

        imgTattooFinalizada = findViewById(R.id.imgFotoTatuagemFinalizada);
        txtNomeTatuador = findViewById(R.id.txtNomeTatuadorFinalizado);
        txtValor = findViewById(R.id.txtValorFinalizado);

        //Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_perfil);
        setSupportActionBar(toolbar);
        //btnVoltar na toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            tatuagemRecebida = (Evento) bundle.getSerializable("tatuagemSelecionada");
        }
        preenchendoDados();
    }

    private void preenchendoDados() {

        Uri uriFotoUsuario = Uri.parse(tatuagemRecebida.getFotoTatuagem());
        Glide.with(getApplicationContext()).load(uriFotoUsuario).into(imgTattooFinalizada);

        txtNomeTatuador.setText("@" + tatuagemRecebida.getNome());
        txtValor.setText(tatuagemRecebida.getValor());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}