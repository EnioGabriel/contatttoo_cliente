package com.example.apptatuador.helper;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequisicaoJson {
    public static String requisicaoCEP( String uri ) throws Exception {

        URL url = new URL( uri );
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder dadosJson = new StringBuilder();
        String linha;
        while ((linha = bufferedReader.readLine()) != null) {
            dadosJson.append(linha);
        }

        urlConnection.disconnect();

        return dadosJson.toString();
    }
}
