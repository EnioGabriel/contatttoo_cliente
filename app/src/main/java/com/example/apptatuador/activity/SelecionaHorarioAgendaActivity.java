package com.example.apptatuador.activity;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.collection.LongSparseArray;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.apptatuador.R;
import com.example.apptatuador.helper.ConfiguracaoFirebase;
import com.example.apptatuador.helper.UsuarioFirebase;
import com.example.apptatuador.model.Evento;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.linkedin.android.tachyon.DayView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class SelecionaHorarioAgendaActivity extends AppCompatActivity {

    private Evento evento;
    private Calendar diaHoje;
    private DateFormat dataFormat;
    private DateFormat horarioFormat;
    private Calendar editDataEvento;
    private static Calendar editHorarioInicialEvento;
    private Calendar editHorarioTerminoEvento;
    private Evento editEvento;
    private LongSparseArray<List<Evento>> todosEventos;

    private ViewGroup conteudoLayout;
    private TextView txtData;
    private DayView dayView;
    private Button btnSessaoUnica, btnSessaoMultipla, btnAddEvento;

    private int anoOrcamento = 0, mesOrcamento = 0, diaOrcamento = 0, preenchido = 0;
    private float tempoSessaoUnica = 0, tempoSessaoMultipla = 0;
    private String telefoneCliente, nomeCliente, valorSessaoUnica, valorSessaoMultipla, tipoOrcamento, tituloDialog;
    boolean isSessaoUnica = false;
    boolean isEventoUnico = false;
    static boolean enviou = false;
    private DatabaseReference eventoRef;

    private List<Evento> listaEventosCarregados = new ArrayList<>();
    private List<Evento> listaEventosRepetidos = new ArrayList<>();
    private int corEvento;

    private HashMap hashMapHorarios = new HashMap<String, Integer>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Adicionando horario inicial de trabalho
        diaHoje = Calendar.getInstance();
        diaHoje.set(Calendar.HOUR_OF_DAY, 0);
        diaHoje.set(Calendar.MINUTE, 0);
        diaHoje.set(Calendar.SECOND, 0);
        diaHoje.set(Calendar.MILLISECOND, 0);


        dataFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
        horarioFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());

        horarioFormat = new SimpleDateFormat("HH:mm");

        enviou = false;

        setContentView(R.layout.activity_seleciona_horario_agenda);

        //Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_perfil);
        setSupportActionBar(toolbar);
        //btnVoltar na toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);

        eventoRef = ConfiguracaoFirebase.getFirebase()
                .child("evento")
                .child(UsuarioFirebase.getIdentificadorUsuario());

        recuperarEventos();

        conteudoLayout = findViewById(R.id.conteudoSelecionaAgenda);
        txtData = findViewById(R.id.lblDataHojeSelecionaHorarioAgenda);
        dayView = findViewById(R.id.dayViewSelecionaAgenda);

        btnAddEvento = findViewById(R.id.btnAdicionaSessaoAgenda);
        btnSessaoUnica = findViewById(R.id.btnSessaoUnicaSelecionaAgenda);
        btnSessaoMultipla = findViewById(R.id.btnSessaoMultiplaSelecionaAgenda);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            telefoneCliente = bundle.getString("telefoneCliente");
            nomeCliente = bundle.getString("nomeCliente");
            valorSessaoUnica = bundle.getString("valorSessaoUnica");
            valorSessaoMultipla = bundle.getString("valorSessaoMultipla");
            anoOrcamento = bundle.getInt("ANO");
            mesOrcamento = bundle.getInt("MES");
            diaOrcamento = bundle.getInt("DIA");
            tempoSessaoUnica = bundle.getFloat("tempoSessaoUnica");
            tempoSessaoMultipla = bundle.getFloat("tempoSessaoMultipla");
            tipoOrcamento = bundle.getString("tipoOrcamento");
        }

        //Define a data inicial com a clicada no calendário
        diaHoje.set(Calendar.YEAR, anoOrcamento);
        diaHoje.set(Calendar.MONTH, mesOrcamento);
        diaHoje.set(Calendar.DAY_OF_MONTH, diaOrcamento);
        diaHoje.set(Calendar.HOUR_OF_DAY, 0);
        diaHoje.set(Calendar.MINUTE, 0);
        diaHoje.set(Calendar.SECOND, 0);
        diaHoje.set(Calendar.MILLISECOND, 0);
        reformulaView();

        btnSessaoUnica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSessaoUnica = true;
                tituloDialog = "Definir horário sessão única";
                editDataEvento = (Calendar) diaHoje.clone();

                editHorarioInicialEvento = (Calendar) diaHoje.clone();

                editHorarioTerminoEvento = (Calendar) diaHoje.clone();
                editHorarioTerminoEvento.add(Calendar.MINUTE, 60);

                mostrarDialogEditEvento(null, null, null, R.color.cor_botoes);
            }
        });

        btnSessaoMultipla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSessaoUnica = false;
                tituloDialog = "Definir horário sessão múltipla";
                editDataEvento = (Calendar) diaHoje.clone();

                editHorarioInicialEvento = (Calendar) diaHoje.clone();

                editHorarioTerminoEvento = (Calendar) diaHoje.clone();
                editHorarioTerminoEvento.add(Calendar.MINUTE, 60);

                mostrarDialogEditEvento(null, null, null, R.color.cor_botoes);
            }
        });

        btnAddEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEventoUnico = true;
                tituloDialog = "Definir horário da sessão";
                editDataEvento = (Calendar) diaHoje.clone();

                editHorarioInicialEvento = (Calendar) diaHoje.clone();

                editHorarioTerminoEvento = (Calendar) diaHoje.clone();
                editHorarioTerminoEvento.add(Calendar.MINUTE, 60);

                mostrarDialogEditEvento(null, null, null, R.color.cor_botoes);
            }
        });

        // criando linha dos horarios
        Calendar hora = (Calendar) diaHoje.clone();
        List<View> listaHorarioViews = new ArrayList<>();
        for (int i = dayView.getStartHour(); i <= dayView.getEndHour(); i++) {
            hora.set(Calendar.HOUR_OF_DAY, i);

            TextView txtLabelHorario = (TextView) getLayoutInflater().inflate(R.layout.linha_horario, dayView, false);
            txtLabelHorario.setText(horarioFormat.format(hora.getTime()));

            listaHorarioViews.add(txtLabelHorario);
        }
        dayView.setHourLabelViews(listaHorarioViews);

    }

    private void recuperarEventos() {
        eventoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaEventosCarregados.clear();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    listaEventosCarregados.add(snap.getValue(Evento.class));
                }
                adicionarEventos();
                mudancaDeDia();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void populandoLista() {

        for (int i = 0; i < listaEventosCarregados.size(); i++) {
            evento = listaEventosCarregados.get(i);

            if (evento.getCorEvento().equals("Vermelho")) {
                corEvento = android.R.color.holo_red_dark;
            } else {
                corEvento = R.color.cor_botoes;
            }
            listaEventosRepetidos.add(new Evento(evento.getNome(), evento.getCelular(), evento.getValor(), evento.getHora(),
                    evento.getMinuto(), evento.getDuracao(), corEvento, evento.getHoraInicio(), evento.getHoraTermino(), evento.getDataInMillis()));
        }
    }

    private void adicionarEventos() {
        populandoLista();

        evento = new Evento();
        todosEventos = new LongSparseArray<>();

        Map<Long, List<Evento>> EventosMap = new HashMap<>();

        //Adicionando todos os Eventos ao map
        for (Evento eventos : listaEventosRepetidos) {
            Long key = eventos.getDataInMillis();
            if (EventosMap.get(key) == null) {
                EventosMap.put(key, new ArrayList<Evento>());
            }
            EventosMap.get(key).add(eventos);
        }

        Set<Long> conjuntoDeChavesEventos = EventosMap.keySet();
        for (Long id : conjuntoDeChavesEventos) {
            List<Evento> listaEventosRepetidosCopy = new ArrayList<>();//Cria um novo arrayList para cada dia
            //Passando todos os Eventos do enesimo id do Map para uma List
            List<Evento> listaEventosCopy = EventosMap.get(id);
            for (Evento evento : listaEventosCopy) {//Atribuindo todos os valores dessa lista à um evento
                listaEventosRepetidosCopy.add(new Evento(evento.getNome(),
                        evento.getCelular(), evento.getValor(),
                        evento.getHora(), evento.getMinuto(),
                        evento.getDuracao(), corEvento, evento.getHoraInicio(),
                        evento.getHoraTermino(), evento.getDataInMillis()));
            }
            todosEventos.put(id, listaEventosRepetidosCopy);
        }


        //Define a data inicial com a clicada no calendário
        diaHoje.set(Calendar.YEAR, anoOrcamento);
        diaHoje.set(Calendar.MONTH, mesOrcamento);
        diaHoje.set(Calendar.DAY_OF_MONTH, diaOrcamento);
        diaHoje.set(Calendar.HOUR_OF_DAY, 0);
        diaHoje.set(Calendar.MINUTE, 0);
        diaHoje.set(Calendar.SECOND, 0);
        diaHoje.set(Calendar.MILLISECOND, 0);
    }

    private void mudancaDeDia() {
        txtData.setText(dataFormat.format(diaHoje.getTime()));
        mudancaDeEvento();
    }

    private void reformulaView() {
        if (tipoOrcamento.equals("sessaoUnica") || tipoOrcamento.equals("sessaoMultipla")) {
            btnSessaoUnica.setVisibility(View.GONE);
            btnSessaoMultipla.setVisibility(View.GONE);
        } else {
            btnAddEvento.setVisibility(View.GONE);
        }
    }

    private void mudancaDeEvento() {
        List<View> listaEventoViews = null;
        List<DayView.EventTimeRange> listaIntervaloTempoEvento = null;
        List<Evento> listaEventos = todosEventos.get(diaHoje.getTimeInMillis());

        if (listaEventos != null) {
            //Ordena os eventos por hora de início para que o layout mostre na ordem correta
            Collections.sort(listaEventos, new Comparator<Evento>() {
                @Override
                public int compare(Evento evento1, Evento evento2) {
                    if (evento1.getHora() < evento2.getHora()) {
                        return -1;
                    } else {
                        if (evento1.getHora() == evento2.getHora()) {
                            if (evento1.getMinuto() < evento2.getMinuto()) {
                                return -1;
                            } else {
                                if (evento1.getMinuto() == evento2.getMinuto()) {
                                    return 0;
                                } else {
                                    return 1;
                                }
                            }
                        } else {
                            return 1;
                        }
                    }
                }
            });

            listaEventoViews = new ArrayList<>();
            listaIntervaloTempoEvento = new ArrayList<>();

            //Recuperando todas as visualizações de eventos existentes para que possa ser reutilizados se necessário
            List<View> listaReutilizados = dayView.removeEventViews();
            int restante;

            if (listaReutilizados != null) {
                restante = listaReutilizados.size();
            } else {
                restante = 0;
            }

            for (final Evento evento : listaEventos) {
                //Tentando reutilizar uma visualização de evento existente se houver, caso contrário, cria uma nova
                View viewEventoCriado;

                if (restante > 0) {
                    viewEventoCriado = listaReutilizados.get(--restante);
                } else {
                    viewEventoCriado = getLayoutInflater().inflate(R.layout.evento, dayView, false);
                }

                ((TextView) viewEventoCriado.findViewById(R.id.txtNomeUsuarioAgenda)).setText(evento.getNome());
                ((TextView) viewEventoCriado.findViewById(R.id.txtTelefoneAgenda)).setText(evento.getCelular());
                ((TextView) viewEventoCriado.findViewById(R.id.txtValorAgenda)).setText(evento.getValor());
                viewEventoCriado.setBackgroundColor(getResources().getColor(evento.getCor()));

                listaEventoViews.add(viewEventoCriado);

                //calculando intervalo de tempo (minuto) inicial e final
                int minutoInicial = 60 * evento.getHora() + evento.getMinuto();
                int minutoFinal = minutoInicial + evento.getDuracao();
                listaIntervaloTempoEvento.add(new DayView.EventTimeRange(minutoInicial, minutoFinal));
            }
        }

        // Atualizando a visualização do dia com os novos eventos
        dayView.setEventViews(listaEventoViews, listaIntervaloTempoEvento);
    }

    private void setandoDadosOrcamento(TextView nome, TextView telefone, TextView valorUnica, TextView valorMultipla) {
        nome.setText(nomeCliente);
        telefone.setText(telefoneCliente);
        valorUnica.setText(valorSessaoUnica);
        valorMultipla.setText(valorSessaoMultipla);
    }

    private void mostrarDialogEditEvento(@Nullable final String nome, @Nullable final String telefone, @Nullable final String valor, @ColorRes final int corEvento) {
        View view = getLayoutInflater().inflate(R.layout.edit_event_dialog, conteudoLayout, false);
        final TextView txtNomeUsuario = view.findViewById(R.id.txtNomeClienteEvento);
        final TextView txtTelefone = view.findViewById(R.id.txtTelefoneEvento);
        final TextView txtValorSessaoUnica = view.findViewById(R.id.txtValorSessaoUnicaEvento);
        final TextView txtValorSessaoMultiplaEvento = view.findViewById(R.id.txtValorSessaoMultiplaEvento);
        final TextView lblSessaoUnicaEvento = view.findViewById(R.id.lblSessaoUnicaEvento);
        final TextView lblSessaoMultiplaEvento = view.findViewById(R.id.lblSessaoMultiplaEvento);
        final Button btnData = view.findViewById(R.id.btnDataEvento);
        final Button btnHorarioInicial = view.findViewById(R.id.btnHorarioInicialEvento);
        final Button btnHorarioFinal = view.findViewById(R.id.btnHorarioFinalEvento);

        txtNomeUsuario.setText(nome);
        txtTelefone.setText(telefone);
        txtValorSessaoUnica.setText(valor);

        if (tipoOrcamento.equals("completo")) {
            if (isSessaoUnica) {
                setandoDadosOrcamento(txtNomeUsuario, txtTelefone, txtValorSessaoUnica, txtValorSessaoMultiplaEvento);
                txtNomeUsuario.setEnabled(false);
                txtTelefone.setEnabled(false);
                txtValorSessaoUnica.setEnabled(false);
                lblSessaoMultiplaEvento.setVisibility(View.GONE);
                txtValorSessaoMultiplaEvento.setVisibility(View.GONE);
                btnData.setEnabled(false);
                btnHorarioFinal.setEnabled(false);
            } else {
                setandoDadosOrcamento(txtNomeUsuario, txtTelefone, txtValorSessaoUnica, txtValorSessaoMultiplaEvento);
                txtNomeUsuario.setEnabled(false);
                txtTelefone.setEnabled(false);
                txtValorSessaoUnica.setEnabled(false);
                btnData.setEnabled(false);
                btnHorarioFinal.setEnabled(false);
                lblSessaoUnicaEvento.setVisibility(View.GONE);
                txtValorSessaoUnica.setVisibility(View.GONE);
            }

        } else if (tipoOrcamento.equals("sessaoUnica")) {
            setandoDadosOrcamento(txtNomeUsuario, txtTelefone, txtValorSessaoUnica, txtValorSessaoMultiplaEvento);
            txtNomeUsuario.setEnabled(false);
            txtTelefone.setEnabled(false);
            txtValorSessaoUnica.setEnabled(false);
            btnData.setEnabled(false);
            btnHorarioFinal.setEnabled(false);
            lblSessaoMultiplaEvento.setVisibility(View.GONE);
            txtValorSessaoMultiplaEvento.setVisibility(View.GONE);
        } else {
            setandoDadosOrcamento(txtNomeUsuario, txtTelefone, txtValorSessaoUnica, txtValorSessaoMultiplaEvento);
            txtNomeUsuario.setEnabled(false);
            txtTelefone.setEnabled(false);
            txtValorSessaoMultiplaEvento.setEnabled(false);
            btnData.setEnabled(false);
            btnHorarioFinal.setEnabled(false);
            lblSessaoUnicaEvento.setVisibility(View.GONE);
            txtValorSessaoUnica.setVisibility(View.GONE);
        }

        btnData.setText(dataFormat.format(editDataEvento.getTime()));
        btnData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editDataEvento.set(Calendar.YEAR, year);
                        editDataEvento.set(Calendar.MONTH, month);
                        editDataEvento.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        btnData.setText(dataFormat.format(editDataEvento.getTime()));
                    }
                };

                new DatePickerDialog(getApplicationContext(), listener, diaHoje.get(Calendar.YEAR), diaHoje.get(Calendar.MONTH), diaHoje.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        btnHorarioInicial.setText(horarioFormat.format(editHorarioInicialEvento.getTime()));
        btnHorarioInicial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hora, int minuto) {
                        editHorarioInicialEvento.set(Calendar.HOUR_OF_DAY, hora);
                        editHorarioInicialEvento.set(Calendar.MINUTE, minuto);

                        btnHorarioInicial.setText(horarioFormat.format(editHorarioInicialEvento.getTime()));

                        editHorarioTerminoEvento = (Calendar) editHorarioInicialEvento.clone();
                        if (tipoOrcamento.equals("sessaoUnica"))
                            editHorarioTerminoEvento.add(Calendar.MINUTE, (int) tempoSessaoUnica);
                        else
                            editHorarioTerminoEvento.add(Calendar.MINUTE, (int) tempoSessaoMultipla);
                        btnHorarioFinal.setText(horarioFormat.format(editHorarioTerminoEvento.getTime()));
                    }
                };
                new TimePickerDialog(SelecionaHorarioAgendaActivity.this, 2, listener, editHorarioInicialEvento.get(Calendar.HOUR_OF_DAY), editHorarioInicialEvento.get(Calendar.MINUTE), true).show();
            }
        });
        btnHorarioFinal.setText(horarioFormat.format(editHorarioTerminoEvento.getTime()));

        AlertDialog.Builder builder = new AlertDialog.Builder(SelecionaHorarioAgendaActivity.this, R.style.AppTheme_Dialog);

        //verifica se o evento ja foi criado e seta o titulo para tal
        builder.setTitle(tituloDialog);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enviou = true;

                List<Evento> listaEventos = todosEventos.get(editDataEvento.getTimeInMillis());

                if (listaEventos == null) {
                    listaEventos = new ArrayList<>();
                    todosEventos.put(editDataEvento.getTimeInMillis(), listaEventos);
                }

                String nome = txtNomeUsuario.getText().toString();
                String telefone = txtTelefone.getText().toString();
                String valorUnico = txtValorSessaoUnica.getText().toString();
                String valorMultiplo = txtValorSessaoMultiplaEvento.getText().toString();
                int hora = editHorarioInicialEvento.get(Calendar.HOUR_OF_DAY);
                int minuto = editHorarioInicialEvento.get(Calendar.MINUTE);
                int duracao = (int) (editHorarioTerminoEvento.getTimeInMillis() - editHorarioInicialEvento.getTimeInMillis()) / 60000;
                String hInicio = horarioFormat.format(editHorarioInicialEvento.getTime());
                String hTermino = horarioFormat.format(editHorarioTerminoEvento.getTime());
                Long dataInmillis = editDataEvento.getTimeInMillis();


                String[] dadosManuais = {nome, telefone, valor};

                String horarioInicialSelecionado = btnHorarioInicial.getText().toString() + ":" + "00";
                String horarioFinalSelecionado = btnHorarioFinal.getText().toString() + ":" + "00";
                boolean temConflito = false;

                for (int i = 0; i < listaEventos.size(); i++) {
                    try {

                        String horaReservaInicio = listaEventos.get(i).getHoraInicio() + ":" + "00";
                        Date time1 = new SimpleDateFormat("HH:mm:ss").parse(horaReservaInicio);
                        Calendar dataBancoInicio = Calendar.getInstance();
                        dataBancoInicio.setTime(time1);
                        dataBancoInicio.add(Calendar.DATE, 1);

                        String horaReservaFinal = listaEventos.get(i).getHoraTermino() + ":" + "00";
                        Date time2 = new SimpleDateFormat("HH:mm:ss").parse(horaReservaFinal);
                        Calendar dataBancotermino = Calendar.getInstance();
                        dataBancotermino.setTime(time2);
                        dataBancotermino.add(Calendar.DATE, 1);

                        Date time3 = new SimpleDateFormat("HH:mm:ss").parse(horarioInicialSelecionado);
                        Calendar calendar3 = Calendar.getInstance();
                        calendar3.setTime(time3);
                        calendar3.add(Calendar.DATE, 1);

                        Date time4 = new SimpleDateFormat("HH:mm:ss").parse(horarioFinalSelecionado);
                        Calendar calendar4 = Calendar.getInstance();
                        calendar4.setTime(time4);
                        calendar4.add(Calendar.DATE, 1);

                        Date dataInicial = calendar3.getTime();
                        Date dataFinal = calendar4.getTime();

                        if (dataInicial.equals(dataBancoInicio.getTime())) {
                            temConflito = true;
                            break;
                        } else if (dataInicial.after(dataBancoInicio.getTime()) == true && dataInicial.before(dataBancotermino.getTime()) == false
                                && (dataFinal.after(dataBancoInicio.getTime()) == true && dataFinal.before(dataBancotermino.getTime()) == false)) {
                        } else if (dataInicial.after(dataBancoInicio.getTime()) == false && dataInicial.before(dataBancotermino.getTime()) == true
                                && (dataFinal.after(dataBancoInicio.getTime()) == false && dataFinal.before(dataBancotermino.getTime())) == true) {
                        } else {
                            temConflito = true;
                            break;
                        }
                    } catch (Exception e) {
                    }
                }

                if (tipoOrcamento.equals("sessaoUnica") && isEventoUnico) {
                    if (temConflito) {
                        Toast.makeText(SelecionaHorarioAgendaActivity.this, "Esse horário já está preenchido, tente outro!", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            listaEventos.add(new Evento(nome, telefone, valorUnico, hora, minuto, duracao, corEvento, hInicio, hTermino, dataInmillis));
                        } catch (Exception e) {

                        }

                        hashMapHorarios.put("horaUnica", hora);
                        hashMapHorarios.put("minutoUnico", minuto);
                        hashMapHorarios.put("horaInicio", hInicio);
                        hashMapHorarios.put("horaTermino", hTermino);

                        Intent intent = new Intent();
                        intent.putExtra("definiuHorario", hashMapHorarios);
                        intent.putExtra("definiuDia", enviou);
                        intent.putExtra("dataInMillis", dataInmillis);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                } else if (tipoOrcamento.equals("sessaoMultipla") && isEventoUnico) {
                    if (temConflito) {
                    } else {
                        try {
                            listaEventos.add(new Evento(nome, telefone, valorMultiplo, hora, minuto, duracao, corEvento, hInicio, hTermino, dataInmillis));
                        } catch (Exception e) {
                        }

                        hashMapHorarios.put("horaMultipla", hora);
                        hashMapHorarios.put("minutoMultiplo", minuto);
                        hashMapHorarios.put("horaInicio", hInicio);
                        hashMapHorarios.put("horaTermino", hTermino);

                        Intent intent = new Intent();
                        intent.putExtra("definiuHorario", hashMapHorarios);
                        intent.putExtra("definiuDia", enviou);
                        intent.putExtra("dataInMillis", dataInmillis);

                        setResult(RESULT_OK, intent);
                        finish();
                    }
                } else if (isSessaoUnica) {
                    if (temConflito) {
                        Toast.makeText(SelecionaHorarioAgendaActivity.this, "Esse horário já está preenchido, tente outro!", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            listaEventos.add(new Evento(nome, telefone, valorUnico, hora, minuto, duracao, corEvento, hInicio, hTermino, dataInmillis));
                        } catch (Exception e) {
                        }

                        hashMapHorarios.put("horaUnica", hora);
                        hashMapHorarios.put("minutoUnico", minuto);
                        hashMapHorarios.put("horaInicio", hInicio);
                        hashMapHorarios.put("horaTermino", hTermino);
                        preenchido++;
                    }
                } else {
                    if (temConflito) {
                        Toast.makeText(SelecionaHorarioAgendaActivity.this, "Esse horário já está preenchido, tente outro!", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            listaEventos.add(new Evento(nome, telefone, valorMultiplo, hora, minuto, duracao, corEvento, hInicio, hTermino, dataInmillis));
                        } catch (Exception e) {
                        }
                        hashMapHorarios.put("horaMultipla", hora);
                        hashMapHorarios.put("minutoMultiplo", minuto);
                        preenchido++;
                    }
                }

                if (preenchido > 1) {
                    finish();
                }

                dispensarEditEvento(true);
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dispensarEditEvento(false);
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dispensarEditEvento(false);
            }
        });
        builder.setView(view);
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        enviou = false;
        hashMapHorarios.clear();
    }

    private void dispensarEditEvento(boolean alterouEvento) {
        if (alterouEvento && editEvento != null) {
            List<Evento> events = todosEventos.get(diaHoje.getTimeInMillis());
            if (events != null) {
                events.remove(editEvento);
            }
        }
        editEvento = null;

        mudancaDeEvento();
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent();
        if (preenchido != 0) {
            //caso defina apenas um horario de sessao nesse dia
            intent.putExtra("definiuHorario", hashMapHorarios);
            intent.putExtra("definiuDia", true);
        } else {
            intent.putExtra("definiuDia", false);
        }
        setResult(RESULT_OK, intent);
        finish();
        return false;
    }
}