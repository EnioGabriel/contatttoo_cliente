package com.example.apptatuador.model;

import com.example.apptatuador.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

public class Orcamento{

    private int qtdSessoes;
    private String tempoSessaoUnica, tempoSessoesMultiplas, valorTotalSessaoMultipla,
            valorTotalSessaoUnica, valorPorSessao,tipoOrcamento,nomeTatuador, fotoTatuador,
            fotoTatuagem, idTatuador, idConsulta, data, hora;

    private DatabaseReference orcamentosPrivadosRef, orcamentosAbertosRef;

    public Orcamento() {
    }

    public Orcamento(String idCliente, String idTatuador, String idConsulta) {

        this.idConsulta = idConsulta;
        //Setando local de referencia no DB
        orcamentosPrivadosRef = ConfiguracaoFirebase.getFirebase()
                .child("orcamentosPrivados")
                .child(idCliente)
                .child(idTatuador)
                .child(idConsulta);

        orcamentosAbertosRef = ConfiguracaoFirebase.getFirebase()
                .child("orcamentosAbertos")
                .child(idCliente)
                .child(idTatuador)
                .child(idConsulta);
    }


    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public boolean salvarConsultaPrivada(String tipoOrcamento){
        if (tipoOrcamento=="completo"){
            //Salvando todos os valores
            orcamentosPrivadosRef.setValue(this);
        }else if (tipoOrcamento=="sessaoUnica"){
            //Montar hashMap para salvar apenas os dados de sessao unica
            HashMap<String, Object> sessaoUnica = new HashMap<>();
            sessaoUnica.put("nomeTatuador", getNomeTatuador());
            sessaoUnica.put("idTatuador", getIdTatuador());
            sessaoUnica.put("fotoTatuador", getFotoTatuador());
            sessaoUnica.put("tempoSessaoUnica", getTempoSessaoUnica());
            sessaoUnica.put("valorTotalSessaoUnica", getValorTotalSessaoUnica());
            sessaoUnica.put("tipoOrcamento", getTipoOrcamento());
            sessaoUnica.put("fotoTatuagem", getFotoTatuagem());
            sessaoUnica.put("idConsulta", idConsulta);
            sessaoUnica.put("data", getData());
            sessaoUnica.put("hora", getHora());
            orcamentosPrivadosRef.setValue(sessaoUnica);
        }else {
            //Montar hashMap para salvar apenas os dados de sessao multipla
            HashMap<String, Object> sessaoMultipla = new HashMap<>();
            sessaoMultipla.put("idTatuador", getIdTatuador());
            sessaoMultipla.put("nomeTatuador", getNomeTatuador());
            sessaoMultipla.put("fotoTatuador", getFotoTatuador());
            sessaoMultipla.put("valorPorSessao", getValorPorSessao());
            sessaoMultipla.put("tempoSessoesMultiplas", getTempoSessoesMultiplas());
            sessaoMultipla.put("valorTotalSessaoMultipla", getValorTotalSessaoMultipla());
            sessaoMultipla.put("qtdSessoes", getQtdSessoes());
            sessaoMultipla.put("tipoOrcamento", getTipoOrcamento());
            sessaoMultipla.put("fotoTatuagem", getFotoTatuagem());
            sessaoMultipla.put("idConsulta", idConsulta);
            sessaoMultipla.put("data", getData());
            sessaoMultipla.put("hora", getHora());
            orcamentosPrivadosRef.setValue(sessaoMultipla);
        }
        return true;
    }

    public boolean salvarConsultaAberta(String tipoOrcamento){
        if (tipoOrcamento=="completo"){
            //Salvando todos os valores
            orcamentosAbertosRef.setValue(this);
        }else if (tipoOrcamento=="sessaoUnica"){
            //Montar hashMap para salvar apenas os dados de sessao unica
            HashMap<String, Object> sessaoUnica = new HashMap<>();
            sessaoUnica.put("nomeTatuador", getNomeTatuador());
            sessaoUnica.put("idTatuador", getIdTatuador());
            sessaoUnica.put("fotoTatuador", getFotoTatuador());
            sessaoUnica.put("tempoSessaoUnica", getTempoSessaoUnica());
            sessaoUnica.put("valorTotalSessaoUnica", getValorTotalSessaoUnica());
            sessaoUnica.put("tipoOrcamento", getTipoOrcamento());
            sessaoUnica.put("fotoTatuagem", getFotoTatuagem());
            sessaoUnica.put("idConsulta", idConsulta);
            sessaoUnica.put("data", getData());
            sessaoUnica.put("hora", getHora());
            orcamentosAbertosRef.setValue(sessaoUnica);
        }else {
            //Montar hashMap para salvar apenas os dados de sessao multipla
            HashMap<String, Object> sessaoMultipla = new HashMap<>();
            sessaoMultipla.put("idTatuador", getIdTatuador());
            sessaoMultipla.put("nomeTatuador", getNomeTatuador());
            sessaoMultipla.put("fotoTatuador", getFotoTatuador());
            sessaoMultipla.put("valorPorSessao", getValorPorSessao());
            sessaoMultipla.put("tempoSessoesMultiplas", getTempoSessoesMultiplas());
            sessaoMultipla.put("valorTotalSessaoMultipla", getValorTotalSessaoMultipla());
            sessaoMultipla.put("qtdSessoes", getQtdSessoes());
            sessaoMultipla.put("tipoOrcamento", getTipoOrcamento());
            sessaoMultipla.put("fotoTatuagem", getFotoTatuagem());
            sessaoMultipla.put("idConsulta", idConsulta);
            sessaoMultipla.put("data", getData());
            sessaoMultipla.put("hora", getHora());
            orcamentosAbertosRef.setValue(sessaoMultipla);
        }
        return true;
    }

    public String getIdTatuador() {
        return idTatuador;
    }

    public void setIdTatuador(String idTatuador) {
        this.idTatuador = idTatuador;
    }

    public String getValorTotalSessaoMultipla() {
        return valorTotalSessaoMultipla;
    }

    public void setValorTotalSessaoMultipla(String valorTotalSessaoMultipla) {
        this.valorTotalSessaoMultipla = valorTotalSessaoMultipla;
    }

    public String getValorTotalSessaoUnica() {
        return valorTotalSessaoUnica;
    }

    public void setValorTotalSessaoUnica(String valorTotalSessaoUnica) {
        this.valorTotalSessaoUnica = valorTotalSessaoUnica;
    }

    public String getFotoTatuagem() {
        return fotoTatuagem;
    }

    public void setFotoTatuagem(String fotoTatuagem) {
        this.fotoTatuagem = fotoTatuagem;
    }

    public String getValorPorSessao() {
        return valorPorSessao;
    }

    public void setValorPorSessao(String valorPorSessao) {
        this.valorPorSessao = valorPorSessao;
    }

    public int getQtdSessoes() {
        return qtdSessoes;
    }

    public void setQtdSessoes(int qtdSessoes) {
        this.qtdSessoes = qtdSessoes;
    }

    public String getTempoSessaoUnica() {
        return tempoSessaoUnica;
    }

    public void setTempoSessaoUnica(String tempoSessaoUnica) {
        this.tempoSessaoUnica = tempoSessaoUnica;
    }

    public String getTipoOrcamento() {
        return tipoOrcamento;
    }

    public void setTipoOrcamento(String tipoOrcamento) {
        this.tipoOrcamento = tipoOrcamento;
    }

    public String getTempoSessoesMultiplas() {
        return tempoSessoesMultiplas;
    }

    public void setTempoSessoesMultiplas(String tempoSessoesMultiplas) {
        this.tempoSessoesMultiplas = tempoSessoesMultiplas;
    }

    public String getNomeTatuador() {
        return nomeTatuador;
    }

    public void setNomeTatuador(String nomeTatuador) {
        this.nomeTatuador = nomeTatuador;
    }

    public String getFotoTatuador() {
        return fotoTatuador;
    }

    public void setFotoTatuador(String fotoTatuador) {
        this.fotoTatuador = fotoTatuador;
    }
}

