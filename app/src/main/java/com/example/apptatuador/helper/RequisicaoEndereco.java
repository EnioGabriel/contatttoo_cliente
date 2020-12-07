package com.example.apptatuador.helper;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.apptatuador.activity.CadastroEstudioActivity;
import com.example.apptatuador.model.Estudio;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;

public class RequisicaoEndereco extends AsyncTask<Void, Void, Estudio> {
    private WeakReference<CadastroEstudioActivity> activity;

    public RequisicaoEndereco(CadastroEstudioActivity activity) {
        this.activity = new WeakReference<>(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.i("PRE", "preExecute");
        if (activity.get() != null) {
            activity.get().bloquearCampos(true);
        }
    }

    @Override
    protected Estudio doInBackground(Void... voids) {

        try {
            String jsonString = RequisicaoJson.requisicaoCEP(activity.get().getUriRequest());

            Gson gson = new Gson();
            return gson.fromJson(jsonString, Estudio.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Estudio estudio) {
        super.onPostExecute(estudio);

        if (activity.get() != null) {
            activity.get().bloquearCampos(false);

            if (estudio != null) {
                activity.get().setCamposDeEndereco(estudio);
            }
        }
    }
}