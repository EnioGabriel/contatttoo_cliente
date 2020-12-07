package com.example.apptatuador.model;

import android.util.Log;
import android.widget.Toast;

import com.example.apptatuador.helper.ConfiguracaoFirebase;
import com.example.apptatuador.helper.UsuarioFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Estudio implements Serializable {
    public static final int REQUEST_CEP_CODIGO = 556; /* NÚMERO ALEATÓRIO */
    public static final String CEP_KEY = "cep_key";
    private String id;
    private String bairro;
    private String cep;
    private String logradouro;
    private String localidade;
    private String uf;
    private String numeroCasa;
    private String complemento;
    private String caminhoFoto;
    private String nomeEstudio;
    private String nomePesquisaEstudio;
    private String idEstudio;
    private String nomeFuncionario;

    public void salvarEstudio(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference estudioRef = firebaseRef.child("estudio").child(UsuarioFirebase.getIdentificadorUsuario());//criando nó com ID
        estudioRef.setValue(this);//adicionando valores
    }

    public void funcionarioEstudio(String idUsuario, Estudio estudioTrabalha){
        DatabaseReference funcionarioEstudio;
        funcionarioEstudio = ConfiguracaoFirebase.getFirebase().child("funcionariosEstudio");

        HashMap<String, Object> dadosSeguir = new HashMap<>();
        dadosSeguir.put("nome", estudioTrabalha.getNomeEstudio());
        dadosSeguir.put("caminhoFoto", estudioTrabalha.getCaminhoFoto());
        dadosSeguir.put("nomeEstudio", estudioTrabalha.getNomeEstudio());
        dadosSeguir.put("bairro", estudioTrabalha.getBairro());
        dadosSeguir.put("cep", estudioTrabalha.getCep());
        dadosSeguir.put("complemento", estudioTrabalha.getComplemento());
        dadosSeguir.put("localidade", estudioTrabalha.getLocalidade());
        dadosSeguir.put("numeroCasa", estudioTrabalha.getNumeroCasa());
        Log.i("TAG","LOGRA" +estudioTrabalha.getLogradouro());
        dadosSeguir.put("logradouro", estudioTrabalha.getLogradouro());
        dadosSeguir.put("uf", estudioTrabalha.getUf());

        DatabaseReference seguidorRef = funcionarioEstudio
                .child(idUsuario)
                .child(estudioTrabalha.getId());
        seguidorRef.setValue(dadosSeguir);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNomePesquisaEstudio() {
        return nomePesquisaEstudio;
    }

    public void setNomePesquisaEstudio(String nomePesquisaEstudio) {
        this.nomePesquisaEstudio = nomePesquisaEstudio.toLowerCase();
    }

    public String getNumeroCasa() {
        return numeroCasa;
    }

    public void setNumeroCasa(String numeroCasa) {
        this.numeroCasa = numeroCasa;
    }

    public String getIdEstudio() {
        return idEstudio;
    }

    public void setIdEstudio(String idEstudio) {
        this.idEstudio = idEstudio;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }

    public String getNomeEstudio() {
        return nomeEstudio;
    }

    public void setNomeEstudio(String nomeEstudio) {
        this.nomeEstudio = nomeEstudio;
    }

    public String getNomeFuncionario() {
        return nomeFuncionario;
    }

    public void setNomeFuncionario(String nomeFuncionario) {
        this.nomeFuncionario = nomeFuncionario;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getLocalidade() {
        return localidade;
    }

    public void setLocalidade(String localidade) {
        this.localidade = localidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }
}
