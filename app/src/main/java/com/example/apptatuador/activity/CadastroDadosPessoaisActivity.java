package com.example.apptatuador.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.example.apptatuador.R;
import com.example.apptatuador.helper.CustomTextWatcher;
import com.example.apptatuador.helper.ValidarCPF;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.material.textfield.TextInputEditText;

public class CadastroDadosPessoaisActivity extends AppCompatActivity {

    private TextInputEditText txtCadNome, txtCadCPF, txtCadCelular;
    private Button btnProximo;
    private String CPF;
    boolean cpfValido = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_dados_pessoais);

        txtCadNome = findViewById(R.id.txtCadNome);
        txtCadCPF = findViewById(R.id.txtCadCPF);
        txtCadCelular = findViewById(R.id.txtCadCelular);
        btnProximo = findViewById(R.id.btnProximo);

        //Formatando Campos
        SimpleMaskFormatter smfCPF      = new SimpleMaskFormatter("NNN.NNN.NNN-NN");
        SimpleMaskFormatter smfCelular  = new SimpleMaskFormatter("(NN)NNNNN-NNNN");

        MaskTextWatcher mtwCPF      = new MaskTextWatcher(txtCadCPF, smfCPF);
        MaskTextWatcher mtwCelular  = new MaskTextWatcher(txtCadCelular, smfCelular);

        txtCadCPF.addTextChangedListener(mtwCPF);
        txtCadCelular.addTextChangedListener(mtwCelular);

        //ValidandoCampos
        listeners();

        btnProximo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //verifica se os campos estão vazios
                isVazio();

                //Enviando dados para uma nova intent
                if (!isVazio()&&cpfValido) {
                    Intent intent = new Intent(CadastroDadosPessoaisActivity.this, CadastroDadosContaActivity.class);
                    intent.putExtra("nome", txtCadNome.getText().toString());
                    intent.putExtra("cpf", CPF);
                    intent.putExtra("celular", txtCadCelular.getText().toString());
                    startActivityForResult(intent,1000);
                }
            }
        });
    }//Fim onCreate

    private boolean isVazio(){
        if (txtCadNome.length()==0){
            txtCadNome.setError("Preencha seu nome!");
            return true;
        }
        if (txtCadCPF.length()==0){
            txtCadCPF.setError("Preencha este campo!");
            return true;
        }
        if (txtCadCelular.length()==0){
            txtCadCelular.setError("Preencha este campo!");
            return true;
        }
        return false;
    }

    private void listeners(){

        txtCadCPF.addTextChangedListener(new CustomTextWatcher(txtCadCPF,false,1500) {
            @Override
            public void textWasChanged() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Pega dados "limpos" para validacao
                        CPF = txtCadCPF.getText().toString().replace(".","").replace("-","");

                        if (CPF.length()>0) {
                            //Checa CPF
                            cpfValido = ValidarCPF.isCPF(CPF);

                            if (!cpfValido) {
                                txtCadCPF.setError("CPF inválido");
                            }
                        }
                    }
                });
            }
        });

        /*
        txtCadCPF.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                //Pega dados "limpos" para validacao
                CPF = txtCadCPF.getText().toString().replace(".","").replace("-","");

                if (CPF.length()>0) {
                    //Checa CPF
                    cpfValido = ValidarCPF.isCPF(CPF);

                    if (cpfValido) {
                        Toast.makeText(getApplicationContext(), "Teste " + CPF, Toast.LENGTH_LONG).show();
                    } else
                        txtCadCPF.setError("CPF inválido");
                }
            }
        });//Fim CPF
         */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==1000){
            finish();
        }
    }
}