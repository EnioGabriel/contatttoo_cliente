package com.example.apptatuador.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.apptatuador.R;
import com.example.apptatuador.helper.ConfiguracaoFirebase;
import com.example.apptatuador.helper.UsuarioFirebase;
import com.example.apptatuador.model.Consulta;
import com.google.firebase.database.DatabaseReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConsultaTattooActivity extends AppCompatActivity {
    private Button btnAceitarConsulta, btnRecusarConsulta;
    private ImageView imgTattooConsulta;
    private CircleImageView imgFotoUsuarioConsulta;
    private TextView lblNomeUsuarioConsulta,lblParteCorpo,lblTamanhoTattoo,lblCorTattoo,lblDescricaoTattoo,txtDesc;

    private Consulta consultaRecebida;

    private DatabaseReference orcamentoRef;

    private String tipoConsulta, idTatuador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta_tattoo);

        idTatuador = UsuarioFirebase.getIdentificadorUsuario();

        //Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_perfil);
        setSupportActionBar(toolbar);
        //btnVoltar na toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);

        //Inicializando componentes
        btnAceitarConsulta = findViewById(R.id.btnAceitarConsulta);
        btnRecusarConsulta = findViewById(R.id.btnRecusarConsulta);

        imgTattooConsulta = findViewById(R.id.imgTattooConsulta);
        imgFotoUsuarioConsulta = findViewById(R.id.imgFotoUsuarioConsulta);

        lblNomeUsuarioConsulta = findViewById(R.id.lblNomeUsuarioConsulta);
        lblParteCorpo = findViewById(R.id.lblParteCorpo);
        lblTamanhoTattoo = findViewById(R.id.lblTamanhoTattoo);
        lblCorTattoo = findViewById(R.id.lblCorTattoo);
        lblDescricaoTattoo = findViewById(R.id.lblDescricaoTattoo);
        txtDesc = findViewById(R.id.txtDesc);

        txtDesc.setVisibility(View.GONE);

        //Recuperar dados do usuario passsados pelo recyclerClick
        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            consultaRecebida = (Consulta) bundle.getSerializable("consultaSelecionada");
            tipoConsulta = bundle.getString("tipoConsulta");
        }

        //SETANDO FOTO DE PERFIL
        String caminhoFoto = consultaRecebida.getFotoUsuario();
        if (caminhoFoto != null){
            Uri url = Uri.parse(caminhoFoto);
            Glide.with(ConsultaTattooActivity.this)
                    .load(url)
                    .into(imgFotoUsuarioConsulta);
        }else{
            imgFotoUsuarioConsulta.setImageResource(R.drawable.avatar);
        }

        //SETANDO FOTO DA TATTOO
        String fotoTattoo = consultaRecebida.getFotoConsulta();
        if (fotoTattoo != null){
            Uri url = Uri.parse(fotoTattoo);
            Glide.with(ConsultaTattooActivity.this)
                    .load(url)
                    .into(imgTattooConsulta);
        }

        //Setando os labels
        lblNomeUsuarioConsulta.setText(consultaRecebida.getNomeUsuario());
        lblParteCorpo.setText(consultaRecebida.getLocalCorpo());
        lblCorTattoo.setText(consultaRecebida.getCor());
        if (consultaRecebida.getLargura()==0){
            lblTamanhoTattoo.setText("Tatuador define tamanho adequado");
        }else {
            lblTamanhoTattoo.setText("Altura: "+consultaRecebida.getAltura() +"cm"+ " x " + "Largura: " + consultaRecebida.getLargura()+"cm");
        }

        if (!consultaRecebida.getObservacoes().equals("")){
            txtDesc.setVisibility(View.VISIBLE);
            lblDescricaoTattoo.setText(consultaRecebida.getObservacoes());
        }

        btnAceitarConsulta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConsultaTattooActivity.this, OrcamentoTattooActivity.class);
                intent.putExtra("tipoConsulta", tipoConsulta);
                intent.putExtra("consultaRecebida", consultaRecebida);//enviando objeto instanciado via intent
                startActivity(intent);
            }
        });
        btnRecusarConsulta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                excluirOrcamento();
                finish();
            }
        });
    }

    private void excluirOrcamento() {

        if(tipoConsulta.equals("consultaPrivada")){
            orcamentoRef = ConfiguracaoFirebase.getFirebase()
                    .child("consultaFechada")
                    .child(idTatuador)
                    .child(consultaRecebida.getIdCliente())
                    .child(consultaRecebida.getIdConsulta());
            orcamentoRef.removeValue();
        }
        else {
            orcamentoRef = ConfiguracaoFirebase.getFirebase()
                    .child("consultaAberta")
                    .child(consultaRecebida.getIdCliente())
                    .child(consultaRecebida.getIdConsulta())
                    .child(idTatuador);
            orcamentoRef.removeValue();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}