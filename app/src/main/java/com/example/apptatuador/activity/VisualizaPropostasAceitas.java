package com.example.apptatuador.activity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.apptatuador.R;
import com.example.apptatuador.helper.ConfiguracaoFirebase;
import com.example.apptatuador.helper.UsuarioFirebase;
import com.example.apptatuador.model.Evento;
import com.google.firebase.database.DatabaseReference;

public class VisualizaPropostasAceitas extends AppCompatActivity {

    private ImageView imgTattooAndamento;
    private EditText txtNomeTatuador, txtValor;
    private Button btnConfirmarTerminoTatuagem;

    private Evento tatuagemRecebida;
    private String idTatuador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualiza_propostas_aceitas);

        idTatuador = UsuarioFirebase.getIdentificadorUsuario();

        imgTattooAndamento = findViewById(R.id.imgFotoTatuagemAndamento);
        txtNomeTatuador = findViewById(R.id.txtNomeTatuadorEmAndamento);
        txtValor = findViewById(R.id.txtValorAndamento);
        btnConfirmarTerminoTatuagem = findViewById(R.id.btnFinalizarTatuagem);

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

        btnConfirmarTerminoTatuagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tatuagemRecebida.eventoFinalizado();//Cria um evento dizendo que tattoo foi finalizada
                excluirEvento();
                finish();
            }
        });
    }

    private void excluirEvento() {
        //Excluindo da reference do evento
        DatabaseReference eventoRef = ConfiguracaoFirebase.getFirebase()
                .child("evento")
                .child(idTatuador)
                .child(tatuagemRecebida.getIdEvento());
        eventoRef.removeValue();

        //Excluindo do reference de propostasAceitas (PARTE DO CLIENTE)
        DatabaseReference propostasAceitasRef = ConfiguracaoFirebase.getFirebase()
                .child("propostasAceitas")
                .child(tatuagemRecebida.getIdCliente())
                .child(idTatuador)
                .child(tatuagemRecebida.getIdConsulta());
        propostasAceitasRef.removeValue();

    }

    private void preenchendoDados(){
        Uri uriFotoUsuario = Uri.parse(tatuagemRecebida.getFotoTatuagem());
        Glide.with(getApplicationContext()).load(uriFotoUsuario).into(imgTattooAndamento);
        txtNomeTatuador.setText(tatuagemRecebida.getNome());
        txtValor.setText(tatuagemRecebida.getValor());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}