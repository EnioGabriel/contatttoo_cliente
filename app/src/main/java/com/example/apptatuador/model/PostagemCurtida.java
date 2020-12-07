package com.example.apptatuador.model;

import com.example.apptatuador.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

public class PostagemCurtida {

    private int qtdLike = 0;

    private Feed feed;

    private Usuario usuario;

    public PostagemCurtida() {
    }

    public void atualizarQtdCurtida(int valor){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();

        DatabaseReference postagensCurtidasRef = firebaseRef
                .child("postagens_curtidas")
                .child(feed.getId())
                .child("qtd_like");
        setQtdLike(getQtdLike()+valor);
        postagensCurtidasRef.setValue(getQtdLike());

    }

    public void salvarCurtida(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();

        HashMap<String, Object> dadosUsuario = new HashMap<>();
        dadosUsuario.put("nomeUsuario", usuario.getNomeUsuario());
        dadosUsuario.put("caminhoFoto", usuario.getCaminhoFoto());

        DatabaseReference postagensCurtidasRef = firebaseRef
                .child("postagens_curtidas")
                .child(feed.getId())
                .child(usuario.getId());
        postagensCurtidasRef.setValue(dadosUsuario);

        //Incrementando curtida
        atualizarQtdCurtida(1);
    }

    public void removerCurtida(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();

        DatabaseReference postagensCurtidasRef = firebaseRef
                .child("postagens_curtidas")
                .child(feed.getId())
                .child(usuario.getId());
        postagensCurtidasRef.removeValue();

        //decrementando curtida
        atualizarQtdCurtida(-1);
    }

    public Feed getFeed() {
        return feed;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public int getQtdLike() {
        return qtdLike;
    }

    public void setQtdLike(int qtdLike) {
        this.qtdLike = qtdLike;
    }
}
