package com.example.apptatuador.model;

import com.example.apptatuador.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Usuario implements Serializable {
    private String id;
    private String nome;
    private String nomeUsuario;
    private String nomePesquisa;
    private String email;
    private String senha;
    private String celular;
    private String cpf;
    private String caminhoFoto;

    private int publicacoes = 0, seguidores = 0, seguindo = 0;

    public Usuario() {
        //Firebase necessita de um construtor
    }

    public void salvarDados(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference usuarioRef = firebaseRef.child("tatuadores").child(getId());//criando nó com ID
        usuarioRef.setValue(this);//adicionando valores
    }

    public void atualizarQtdPostagem(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference usuarioRef = firebaseRef.child("tatuadores").child(getId());

        HashMap<String, Object> dados = new HashMap<>();
        dados.put("publicacoes",getPublicacoes());

        usuarioRef.updateChildren(dados);
    }

    public void atualizarDados(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference usuarioRef = firebaseRef.child("tatuadores").child(getId());
        DatabaseReference nomesUsuariosRef = firebaseRef.child("nomesUsuarios").child("TATUADORES").child(getId());
        //Atualizando só o nó de nomesUsuarios
        Map<String, Object> nomeUsuario = converterParaMap(true);
        nomesUsuariosRef.updateChildren(nomeUsuario);
        //Atualizando nó de tatuadores
        Map<String, Object> valoresUsuarios = converterParaMap(false);
        usuarioRef.updateChildren(valoresUsuarios);

    }

    public Map<String, Object> converterParaMap(boolean isNomeUsuario){

        HashMap<String, Object> usuarioMap = new HashMap<>();
        if (isNomeUsuario){
            usuarioMap.put("nome", getNomeUsuario());
        }else{
            usuarioMap.put("id", getId());
            usuarioMap.put("nome", getNome());
            usuarioMap.put("nomeUsuario", getNomeUsuario());
            usuarioMap.put("nomePesquisa", getNomePesquisa());
            usuarioMap.put("celular", getCelular());
            usuarioMap.put("caminhoFoto", getCaminhoFoto());
        }
        return  usuarioMap;
    }

    public String getNomePesquisa() {
        return nomePesquisa;
    }

    public void setNomePesquisa(String nomePesquisa) {
        this.nomePesquisa = nomePesquisa.toLowerCase();
    }

    public int getPublicacoes() {
        return publicacoes;
    }

    public void setPublicacoes(int publicacoes) {
        this.publicacoes = publicacoes;
    }

    public int getSeguidores() {
        return seguidores;
    }

    public void setSeguidores(int seguidores) {
        this.seguidores = seguidores;
    }

    public int getSeguindo() {
        return seguindo;
    }

    public void setSeguindo(int seguindo) {
        this.seguindo = seguindo;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}