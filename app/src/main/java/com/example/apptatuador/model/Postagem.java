package com.example.apptatuador.model;

import com.example.apptatuador.helper.ConfiguracaoFirebase;
import com.example.apptatuador.helper.UsuarioFirebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Postagem implements Serializable {
    private String idPostagem;
    private String idUsuario;
    private String descricao;
    private String caminhoFoto;

    public Postagem() {
        //gerando Id da postagem assim q o costrutor for chamado
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference postagemRef = firebaseRef.child("postagens");
        String idPostagem = postagemRef.push().getKey();//push - gera um id unico
        setIdPostagem(idPostagem);
    }

    public boolean salvarPostagem(DataSnapshot seguidoresSnapshot){

        Map objeto = new HashMap<>();
        Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();

        //Referencia postagem para feed
        String combinacaoId = "/"+getIdUsuario() + "/" +getIdPostagem();
        objeto.put("/postagens"+ combinacaoId,this );

        for (DataSnapshot seguidores : seguidoresSnapshot.getChildren()){

            String idSeguidor = seguidores.getKey();

            //Montar objeto para salvar
            HashMap<String, Object> dadosSeguidor = new HashMap<>();
            dadosSeguidor.put("fotoPostagem", getCaminhoFoto());
            dadosSeguidor.put("descricao", getDescricao());
            dadosSeguidor.put("id", getIdPostagem());
            dadosSeguidor.put("nomeUsuario", usuarioLogado.getNomeUsuario());
            dadosSeguidor.put("fotoUsuario", usuarioLogado.getCaminhoFoto());

            String idsAtualizaçao = "/"+ idSeguidor + "/" +getIdUsuario()+"/"+getIdPostagem();
            objeto.put("/feed"+idsAtualizaçao,dadosSeguidor);
        }
        firebaseRef.updateChildren(objeto);
        return true;
    }

    public String getIdPostagem() {
        return idPostagem;
    }

    public void setIdPostagem(String idPostagem) {
        this.idPostagem = idPostagem;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }
}
