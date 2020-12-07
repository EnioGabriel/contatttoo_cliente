package com.example.apptatuador.helper;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Timer;
import java.util.TimerTask;

public abstract class CustomTextWatcher implements TextWatcher { //Notice abstract class so we leave abstract method textWasChanged() for implementing class to define it

    private Timer timer;
    private final TextInputEditText meuTxt;
    private boolean CEP = false;
    private long tempoEspera;
    private Context contexto;

    protected CustomTextWatcher(TextInputEditText tView, boolean isCep, long delay) {
        meuTxt = tView;
        CEP = isCep;
        tempoEspera = delay;
        //contexto = context;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // resetando tempo enquanto usuario digita
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (!CEP) {
            // ao digitar inicia o timer
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    textWasChanged();
                }
            }, tempoEspera);
        }else{
            textWasChanged();
        }
    }

    public abstract void textWasChanged(); //metodo para ser implementado na classe que chamar

}