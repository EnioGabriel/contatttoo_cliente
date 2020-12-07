package com.example.apptatuador.helper;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.example.apptatuador.R;

public class GerenciarDialogCarregamento {

    static AlertDialog dialog;

    public static void mostrarDialog(Context context, String mensagem) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.Dialog);
        alertDialog.setTitle(mensagem);
        alertDialog.setCancelable(false);//bloqueia a tela
        alertDialog.setView(R.layout.carregamento);

        dialog = alertDialog.create();
        dialog.show();
    }

    public static void fecharDialog() {
        dialog.cancel();
    }
}
