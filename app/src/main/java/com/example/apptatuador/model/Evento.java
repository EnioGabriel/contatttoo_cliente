package com.example.apptatuador.model;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;

import com.example.apptatuador.helper.ConfiguracaoFirebase;
import com.example.apptatuador.helper.UsuarioFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Evento implements Serializable {
    @Nullable
    private  String nome, celular, valor, idEvento, fotoUsuario, fotoTatuagem, idCliente, fotoTatuador,
                    idConsulta, idTatuador, nomeTatuador, corEvento, horaTermino, horaInicio;
    private Long dataInMillis;
    private  int hora;
    private  int minuto;
    private  int duracao;
    private  int ano;
    private  int dia;
    private  int mes;
    @ColorRes
    private  int cor;
    private Usuario usuarioLogado;

    public  void geraIdHorario(){
        //gerando Id da postagem assim q o costrutor for chamado
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference horarioRef = firebaseRef.child("confirmarHorario");
        String idHorario = horarioRef.push().getKey();//push - gera um id único
        setIdEvento(idHorario);
    }

    public Evento (){
        geraIdHorario();
        //preparando dados do usuario logado
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
    }

    public void salvarEvento(String idTatuador){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference eventoManualRef = firebaseRef.child("evento");
        String idEventoManual = eventoManualRef.push().getKey();//push - gera um id único
        setCorEvento("Vermelho");
        DatabaseReference eventoRef = ConfiguracaoFirebase.getFirebase()
                .child("evento")
                .child(idTatuador)
                .child(idEventoManual);
        eventoRef.setValue(this);
    }

    public Evento(@Nullable String nomeCliente, @Nullable String celular, String valor, int hora, int minuto, int duracao, @ColorRes int cor, String horaInicio, String horaTermino, Long dataInMillis) {
        this.nome = nomeCliente;
        this.celular = celular;
        this.valor = valor;
        this.hora = hora;
        this.minuto = minuto;
        this.duracao = duracao;
        this.cor = cor;
        this.horaInicio = horaInicio;
        this.horaTermino = horaTermino;
        this.dataInMillis = dataInMillis;
    }

    public boolean eventoFinalizado(){
        setIdTatuador(usuarioLogado.getId());
        setNomeTatuador(usuarioLogado.getNomeUsuario());
        DatabaseReference tatuagemFinalizadaRef = ConfiguracaoFirebase.getFirebase()
                .child("tatuagemFinalizada")
                .child(idCliente)
                .child(usuarioLogado.getId())
                .child(idEvento);
        tatuagemFinalizadaRef.setValue(this);
        return true;
    }

    public Long getDataInMillis() {
        return dataInMillis;
    }

    public void setDataInMillis(Long dataInMillis) {
        this.dataInMillis = dataInMillis;
    }

    @Nullable
    public String getHoraTermino() {
        return horaTermino;
    }

    public void setHoraTermino(@Nullable String horaTermino) {
        this.horaTermino = horaTermino;
    }

    @Nullable
    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(@Nullable String horaInicio) {
        this.horaInicio = horaInicio;
    }

    @Nullable
    public String getCorEvento() {
        return corEvento;
    }

    public void setCorEvento(@Nullable String corEvento) {
        this.corEvento = corEvento;
    }

    @Nullable
    public String getIdTatuador() {
        return idTatuador;
    }

    public void setIdTatuador(@Nullable String idTatuador) {
        this.idTatuador = idTatuador;
    }

    public boolean salvarHorario(String idCliente, String idConsulta){
        geraIdHorario();

        Map objeto = new HashMap<>();

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();

        //Montar objeto para salvar
        HashMap<String, Object> dadosConsulta = new HashMap<>();
        dadosConsulta.put("hora", getHora());
        dadosConsulta.put("minuto", getMinuto());
        dadosConsulta.put("duracao", getDuracao());
        dadosConsulta.put("horaInicio", getHoraInicio());
        dadosConsulta.put("horaTermino", getHoraTermino());
        dadosConsulta.put("ano", getAno());
        dadosConsulta.put("mes", getMes());
        dadosConsulta.put("dia", getDia());
        dadosConsulta.put("dataInMillis", getDataInMillis());

        String idsAtualizaçao = "/"+ idCliente + "/" +usuarioLogado.getId()+ "/" +idConsulta+ "/" + getIdEvento();
        objeto.put("/confirmarHorario"+idsAtualizaçao,dadosConsulta);

        firebaseRef.updateChildren(objeto);
        return true;
    }

    @Nullable
    public String getNomeTatuador() {
        return nomeTatuador;
    }

    public void setNomeTatuador(@Nullable String nomeTatuador) {
        this.nomeTatuador = nomeTatuador;
    }

    @Nullable
    public String getIdConsulta() {
        return idConsulta;
    }

    public void setIdConsulta(@Nullable String idConsulta) {
        this.idConsulta = idConsulta;
    }

    @Nullable
    public String getFotoTatuador() {
        return fotoTatuador;
    }

    public void setFotoTatuador(@Nullable String fotoTatuador) {
        this.fotoTatuador = fotoTatuador;
    }

    @Nullable
    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(@Nullable String idCliente) {
        this.idCliente = idCliente;
    }

    @Nullable
    public String getFotoTatuagem() {
        return fotoTatuagem;
    }

    public void setFotoTatuagem(@Nullable String fotoTatuagem) {
        this.fotoTatuagem = fotoTatuagem;
    }

    @Nullable
    public String getFotoUsuario() {
        return fotoUsuario;
    }

    public void setFotoUsuario(@Nullable String fotoUsuario) {
        this.fotoUsuario = fotoUsuario;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    @Nullable
    public String getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(@Nullable String idEvento) {
        this.idEvento = idEvento;
    }

    @Nullable
    public String getNome() {
        return nome;
    }

    public void setNome(@Nullable String nome) {
        this.nome = nome;
    }

    @Nullable
    public String getCelular() {
        return celular;
    }

    public void setCelular(@Nullable String celular) {
        this.celular = celular;
    }

    @Nullable
    public String getValor() {
        return valor;
    }

    public void setValor(@Nullable String valor) {
        this.valor = valor;
    }

    public int getHora() {
        return hora;
    }

    public void setHora(int hora) {
        this.hora = hora;
    }

    public int getMinuto() {
        return minuto;
    }

    public void setMinuto(int minuto) {
        this.minuto = minuto;
    }

    public int getDuracao() {
        return duracao;
    }

    public void setDuracao(int duracao) {
        this.duracao = duracao;
    }

    public int getCor() {
        return cor;
    }

    public void setCor(int cor) {
        this.cor = cor;
    }
}
