package com.example.apptatuador.helper;

import android.os.AsyncTask;

import com.example.apptatuador.activity.BuscaCepActivity;
import com.example.apptatuador.model.Estudio;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class RequisicaoCEP extends AsyncTask<Void, Void, Void> {
    private WeakReference<BuscaCepActivity> activity;

    public RequisicaoCEP(BuscaCepActivity activity ){
        this.activity = new WeakReference<>( activity );
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if( activity.get() != null ){
            activity.get().bloquearCampos( true );
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            String jsonString = RequisicaoJson.requisicaoCEP( activity.get().getUriRequest() );

            Gson gson = new Gson();
            JSONArray jsonArray = new JSONArray(jsonString);
            activity.get().getEndereco().clear();

            for( int i = 0; i < jsonArray.length(); i++ ){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Estudio a = gson.fromJson( jsonObject.toString(), Estudio.class );
                activity.get().getEndereco().add( a );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void svoid) {
        super.onPostExecute(svoid);

        if( activity.get() != null ){
            activity.get().bloquearCampos( false );
            activity.get().updateListView();
        }
    }
}