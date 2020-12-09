package com.example.apptatuador.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.collection.LongSparseArray;
import androidx.fragment.app.Fragment;

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

public class AgendaFragment extends Fragment {

    private Evento evento;

    private Calendar diaHoje;
    private DateFormat dataFormat;
    private DateFormat horarioFormat;
    private Calendar editDataEvento;
    private Calendar editHorarioInicialEvento;
    private Calendar editHorarioTerminoEvento;
    private Evento editEvento;
    private LongSparseArray<List<Evento>> todosEventos;
    private ValueEventListener valueEventListener;

    private ViewGroup content;
    private TextView txtData;
    private DayView dayView;
    private Button btnProximoDia, btnDiaAnterior, btnAddEvento;

    private List<Evento> listaEventosCarregados;
    private List<Evento> listaEventosRepetidos = new ArrayList<>();
    private int corEvento;

    private Context contexto;

    private DatabaseReference eventoRef;

    public AgendaFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listaEventosCarregados = new ArrayList<>();

        // Adicionando horario inicial de trabalho
        diaHoje = Calendar.getInstance();
        diaHoje.set(Calendar.HOUR_OF_DAY, 0);
        diaHoje.set(Calendar.MINUTE, 0);
        diaHoje.set(Calendar.SECOND, 0);
        diaHoje.set(Calendar.MILLISECOND, 0);

        dataFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
        horarioFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());

        horarioFormat = new SimpleDateFormat("HH:mm");
    }

    @Override
    public void onStop() {
        super.onStop();
        eventoRef.removeEventListener(valueEventListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_agenda, container, false);

        eventoRef = ConfiguracaoFirebase.getFirebase()
                .child("evento")
                .child(UsuarioFirebase.getIdentificadorUsuario());

        recuperarEventos();

        content = view.findViewById(R.id.sample_content);
        txtData = view.findViewById(R.id.lblDataHojeAgenda);
        dayView = view.findViewById(R.id.dayViewAgenda);

        btnAddEvento = view.findViewById(R.id.btnDefinirHorarioSessao);
        btnDiaAnterior = view.findViewById(R.id.btnDiaAnterior);
        btnProximoDia = view.findViewById(R.id.btnProximoDia);

        btnProximoDia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                diaHoje.add(Calendar.DAY_OF_YEAR, 1);
                mudancaDeDia();
            }
        });

        btnDiaAnterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                diaHoje.add(Calendar.DAY_OF_YEAR, -1);
                mudancaDeDia();
            }
        });

        btnAddEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "EM desenvolvimento", Toast.LENGTH_SHORT).show();
                /**
                 editDataEvento = (Calendar) diaHoje.clone();

                 editHorarioInicialEvento = (Calendar) diaHoje.clone();

                 editHorarioTerminoEvento = (Calendar) diaHoje.clone();
                 editHorarioTerminoEvento.add(Calendar.MINUTE, 60);

                 mostrarDialogEditEvento(false, null, null, null, android.R.color.holo_red_dark);
                 */
            }
        });

        // criando linha dos horarios
        Calendar hour = (Calendar) diaHoje.clone();
        List<View> listaHorarioViews = new ArrayList<>();
        for (int i = dayView.getStartHour(); i <= dayView.getEndHour(); i++) {
            hour.set(Calendar.HOUR_OF_DAY, i);

            TextView txtLabelHorario = (TextView) getLayoutInflater().inflate(R.layout.linha_horario, dayView, false);
            txtLabelHorario.setText(horarioFormat.format(hour.getTime()));
            listaHorarioViews.add(txtLabelHorario);
        }
        dayView.setHourLabelViews(listaHorarioViews);

        return view;
    }

    private void recuperarEventos() {
        valueEventListener = eventoRef.addValueEventListener(new ValueEventListener() {
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
            System.out.println("Key : " + eventos.getDataInMillis());
        }

        Set<Long> conjuntoDeChavesEventos = EventosMap.keySet();
        for (Long id : conjuntoDeChavesEventos) {
            List<Evento> listaEventosRepetidosCopy = new ArrayList<>();
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

        //Iniciando Agenda com o dia atual
        diaHoje = Calendar.getInstance();
        diaHoje.set(Calendar.HOUR_OF_DAY, 0);
        diaHoje.set(Calendar.MINUTE, 0);
        diaHoje.set(Calendar.SECOND, 0);
        diaHoje.set(Calendar.MILLISECOND, 0);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        contexto = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        contexto = null;
    }

    private void mudancaDeDia() {
        Log.i("TAG", "ENTREI MUDANCA DIA");
        txtData.setText(dataFormat.format(diaHoje.getTime()));
        mudancaDeEvento();
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

                /**
                 viewEventoCriado.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                editEvento = evento;

                editDataEvento = (Calendar) diaHoje.clone();

                editHorarioInicialEvento = Calendar.getInstance();
                editHorarioInicialEvento.set(Calendar.HOUR_OF_DAY, editEvento.getHora());
                editHorarioInicialEvento.set(Calendar.MINUTE, editEvento.getMinuto());
                editHorarioInicialEvento.set(Calendar.SECOND, 0);
                editHorarioInicialEvento.set(Calendar.MILLISECOND, 0);

                editHorarioTerminoEvento = (Calendar) editHorarioInicialEvento.clone();
                editHorarioTerminoEvento.add(Calendar.MINUTE, editEvento.getDuracao());

                mostrarDialogEditEvento(true, editEvento.getNome(), editEvento.getCelular(), editEvento.getValor(), editEvento.getCor());
                }
                });
                 */

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

    private void populandoEventoManual(String[] dadosManuais) {
        evento.setAno(editDataEvento.get(Calendar.YEAR));
        evento.setMes(editDataEvento.get(Calendar.MONTH) + 1);
        evento.setDia(editDataEvento.get(Calendar.DAY_OF_MONTH));
        evento.setHora(editHorarioInicialEvento.get(Calendar.HOUR));
        evento.setMinuto(editHorarioInicialEvento.get(Calendar.MINUTE));
        int duracao = (int) (editHorarioTerminoEvento.getTimeInMillis() - editHorarioInicialEvento.getTimeInMillis()) / 60000;
        evento.setDuracao(duracao);
        evento.setNome(dadosManuais[0]);
        evento.setCelular(dadosManuais[1]);
        evento.setValor(dadosManuais[2]);
        evento.setHoraTermino(horarioFormat.format(editHorarioTerminoEvento.getTime()));
        evento.setHoraInicio(horarioFormat.format(editHorarioInicialEvento.getTime()));

        evento.salvarEvento(UsuarioFirebase.getIdentificadorUsuario());
    }

    private void mostrarDialogEditEvento(boolean existeEvento, @Nullable final String nome, @Nullable final String telefone, @Nullable final String valor, @ColorRes final int corEvento) {
        View view = getLayoutInflater().inflate(R.layout.edit_event_dialog, content, false);
        final TextView txtNomeUsuario = view.findViewById(R.id.txtNomeClienteEvento);
        final TextView txtTelefone = view.findViewById(R.id.txtTelefoneEvento);
        final TextView txtValorSessaoUnica = view.findViewById(R.id.txtValorSessaoUnicaEvento);
        final TextView txtValorSessaoMultiplaEvento = view.findViewById(R.id.txtValorSessaoMultiplaEvento);
        final TextView lblSessaoUnicaEvento = view.findViewById(R.id.lblSessaoUnicaEvento);
        final TextView lblSessaoMultiplaEvento = view.findViewById(R.id.lblSessaoMultiplaEvento);
        final Button btnData = view.findViewById(R.id.btnDataEvento);
        final Button btnHorarioInicial = view.findViewById(R.id.btnHorarioInicialEvento);
        final Button btnHorarioFinal = view.findViewById(R.id.btnHorarioFinalEvento);

        lblSessaoUnicaEvento.setText("Valor da sessão");

        lblSessaoMultiplaEvento.setVisibility(View.GONE);
        txtValorSessaoMultiplaEvento.setVisibility(View.GONE);

        txtNomeUsuario.setText(nome);
        txtTelefone.setText(telefone);
        txtValorSessaoUnica.setText(valor);

        if (corEvento == R.color.cor_botoes) {
            txtNomeUsuario.setEnabled(false);
            txtTelefone.setEnabled(false);
            txtValorSessaoUnica.setEnabled(false);
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

                new DatePickerDialog(getContext(), listener, diaHoje.get(Calendar.YEAR), diaHoje.get(Calendar.MONTH), diaHoje.get(Calendar.DAY_OF_MONTH)).show();

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
                        editHorarioTerminoEvento.add(Calendar.MINUTE, 60);

                        btnHorarioFinal.setText(horarioFormat.format(editHorarioTerminoEvento.getTime()));
                    }
                };

                new TimePickerDialog(getContext(), 2, listener, editHorarioInicialEvento.get(Calendar.HOUR_OF_DAY), editHorarioInicialEvento.get(Calendar.MINUTE), true).show();
            }
        });

        btnHorarioFinal.setText(horarioFormat.format(editHorarioTerminoEvento.getTime()));
        btnHorarioFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        editHorarioTerminoEvento.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        editHorarioTerminoEvento.set(Calendar.MINUTE, minute);

                        if (!editHorarioTerminoEvento.after(editHorarioInicialEvento)) {
                            editHorarioTerminoEvento = (Calendar) editHorarioInicialEvento.clone();
                            editHorarioTerminoEvento.add(Calendar.MINUTE, 60);
                        }

                        btnHorarioFinal.setText(horarioFormat.format(editHorarioTerminoEvento.getTime()));
                    }
                };

                new TimePickerDialog(getContext(), 2, listener, editHorarioTerminoEvento.get(Calendar.HOUR_OF_DAY), editHorarioTerminoEvento.get(Calendar.MINUTE), true).show();

            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AppTheme_Dialog);

        //verifica se o evento ja foi criado e seta o titulo para tal
        builder.setTitle(existeEvento ? "Editar Evento" : "Adicionar Evento");

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                List<Evento> listaEventos = todosEventos.get(editDataEvento.getTimeInMillis());

                //Verifica se ja tem algum evento agendado no dia
                if (listaEventos == null) {
                    listaEventos = new ArrayList<>();
                    todosEventos.put(editDataEvento.getTimeInMillis(), listaEventos);
                }

                String nome = txtNomeUsuario.getText().toString();
                String telefone = txtTelefone.getText().toString();
                String valor = txtValorSessaoUnica.getText().toString();
                int hora = editHorarioInicialEvento.get(Calendar.HOUR_OF_DAY);
                int minuto = editHorarioInicialEvento.get(Calendar.MINUTE);
                int duracao = (int) (editHorarioTerminoEvento.getTimeInMillis() - editHorarioInicialEvento.getTimeInMillis()) / 60000;

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
                            Toast.makeText(getContext(), "Data conflitante, selecione outra", Toast.LENGTH_SHORT).show();
                            temConflito = true;
                            break;
                        } else if (dataInicial.after(dataBancoInicio.getTime()) == true && dataInicial.before(dataBancotermino.getTime()) == false
                                && (dataFinal.after(dataBancoInicio.getTime()) == true && dataFinal.before(dataBancotermino.getTime()) == false)) {
                        } else if (dataInicial.after(dataBancoInicio.getTime()) == false && dataInicial.before(dataBancotermino.getTime()) == true
                                && (dataFinal.after(dataBancoInicio.getTime()) == false && dataFinal.before(dataBancotermino.getTime())) == true) {
                        } else {
                            temConflito = true;
                            Toast.makeText(getContext(), "Data conflitante, selecione outra", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "ERRO" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                if (!temConflito) {

                    try {
                        listaEventos.add(new Evento(nome, telefone, valor, hora, minuto, duracao, corEvento, btnHorarioInicial.getText().toString(), btnHorarioFinal.getText().toString(), evento.getDataInMillis()));
                    } catch (Exception e) {
                    }

                    todosEventos.get(editDataEvento.getTimeInMillis(), listaEventos);
                    Toast.makeText(getContext(), "Sucesso ao criar horário", Toast.LENGTH_SHORT).show();
                    populandoEventoManual(dadosManuais);
                    dispensarEditEvento(true);
                } else {
                    dispensarEditEvento(false);
                }
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dispensarEditEvento(false);
            }
        });

        // Se exite evento, habilita a opçao de remover
        if (existeEvento) {
            builder.setNeutralButton("Excluir evento", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dispensarEditEvento(true);
                }
            });
        }

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dispensarEditEvento(false);
            }
        });
        builder.setView(view);
        builder.show();
    }

    private void dispensarEditEvento(boolean modificouEvento) {
        if (modificouEvento && editEvento != null) {
            List<Evento> eventos = todosEventos.get(diaHoje.getTimeInMillis());
            if (eventos != null) {
                eventos.remove(editEvento);
            }
        }
        editEvento = null;

        mudancaDeEvento();
    }
}