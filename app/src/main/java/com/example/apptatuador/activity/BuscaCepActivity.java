package com.example.apptatuador.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.apptatuador.R;
import com.example.apptatuador.adapter.AdapterEndereco;
import com.example.apptatuador.helper.RequisicaoCEP;
import com.example.apptatuador.helper.Util;
import com.example.apptatuador.model.Estudio;

import java.util.ArrayList;
import java.util.List;

public class BuscaCepActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private Spinner spEstados;
    private ListView listViewEnderecos;
    private List<Estudio> estudio_endereco;
    private Util util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busca_cep);

        estudio_endereco = new ArrayList<>();
        listViewEnderecos = findViewById(R.id.listViewEnderecos);
        AdapterEndereco adapter = new AdapterEndereco( this, estudio_endereco );
        listViewEnderecos.setAdapter( adapter );
        listViewEnderecos.setOnItemClickListener(this);

        spEstados = findViewById(R.id.spinnerPesqEstado);
        spEstados.setAdapter( ArrayAdapter.createFromResource(this, R.array.estados, android.R.layout.simple_spinner_item) );

        util = new Util(this,
                R.id.txtPesqRua,
                R.id.txtPesqCidade,
                R.id.spinnerPesqEstado);
    }

    private String getEstado(){
        String estado = (String) spEstados.getSelectedItem();
        String[] partes = estado.split("\\(");
        partes = partes[ partes.length - 1 ].split("\\)");
        estado = partes[0];

        return estado;
    }

    private String getCidade(){
        return ((EditText) findViewById(R.id.txtPesqCidade)).getText().toString();
    }

    private String getRua(){
        return ((EditText) findViewById(R.id.txtPesqRua)).getText().toString();
    }

    public String getUriRequest(){
        String uri = getEstado()+"/";
        uri += getCidade()+"/";
        uri += getRua()+"/";
        uri += "json/";

        return "https://viacep.com.br/ws/"+uri;
    }

    public void buscarEndereco( View view ){
        new RequisicaoCEP( BuscaCepActivity.this ).execute();
    }

    public void updateListView(){
        ((AdapterEndereco) listViewEnderecos.getAdapter()).notifyDataSetChanged();
    }

    public List<Estudio> getEndereco(){
        return estudio_endereco;
    }

    public void bloquearCampos( boolean isToLock ){
        util.camposDesabilitados( isToLock );
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String[] cepArray = estudio_endereco.get( i ).getCep().split("-");
        String cep = cepArray[0] + cepArray[1];

        Intent intent = new Intent();
        intent.putExtra( Estudio.CEP_KEY, cep );
        setResult(RESULT_OK, intent);
        finish();
    }
}