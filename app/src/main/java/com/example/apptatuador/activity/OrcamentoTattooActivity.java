package com.example.apptatuador.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.apptatuador.R;
import com.example.apptatuador.helper.ConfiguracaoFirebase;
import com.example.apptatuador.helper.MarcadorDatas;
import com.example.apptatuador.helper.MarcadorDiaAtual;
import com.example.apptatuador.helper.UsuarioFirebase;
import com.example.apptatuador.model.Consulta;
import com.example.apptatuador.model.Evento;
import com.example.apptatuador.model.Orcamento;
import com.google.firebase.database.DatabaseReference;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.threeten.bp.LocalDate;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class OrcamentoTattooActivity extends AppCompatActivity {
    private EditText txtTempoSessaoUnica, txtTempoPorSessao, txtValorSessaoUnica, txtValorSessoes, txtValorPorSessao;
    private Spinner spinnerQtdSessoes;
    private Button btnEnviarOrcamento;
    private Switch switchSessaoUnica, switchSessoes;
    private MaterialCalendarView calendarViewDatasSessao;

    private final MarcadorDiaAtual marcadorDiaAtual = new MarcadorDiaAtual();

    private Consulta consultaRecebida;
    private Orcamento orcamento;
    private Evento evento;

    private DatabaseReference orcamentoRef;

    private CalendarDay dataCopia;

    private String tipoConsulta, idTatuador, valorFormatadoSessaoMultipla, tipoOrcamento;
    private int qtdSessoes, mHora1 = 0, mMinuto1 = 0, mHora2 = 0, mMinuto2 = 0;
    private Long dataInMillis;

    private boolean checkOkSessao = true, checkOkSessoes = false, isAgenda = false, definiuDia = false;

    private double valorConvertidoDouble = 0;

    boolean dataValida = false;

    private HashMap<String, Integer> definiuHorario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orcamento_tattoo);

        idTatuador = UsuarioFirebase.getIdentificadorUsuario();

        //Recuperar dados enviados para o orcamento
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            consultaRecebida = (Consulta) bundle.getSerializable("consultaRecebida");
            tipoConsulta = bundle.getString("tipoConsulta");
        }

        //instanciando objeto
        orcamento = new Orcamento(consultaRecebida.getIdCliente(), idTatuador, consultaRecebida.getIdConsulta());
        evento = new Evento();

        //Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_perfil);
        setSupportActionBar(toolbar);
        //btnVoltar na toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);

        txtTempoSessaoUnica = findViewById(R.id.txtTempoSessaoUnica);
        txtTempoPorSessao = findViewById(R.id.txtTempoPorSessao);
        txtValorSessaoUnica = findViewById(R.id.txtValorSessaoUnica);
        txtValorSessoes = findViewById(R.id.txtValorSessoes);
        txtValorPorSessao = findViewById(R.id.txtValorPorSessao);

        calendarViewDatasSessao = findViewById(R.id.calendarViewDatasSessao);

        calendarViewDatasSessao.setCurrentDate(CalendarDay.today());

        calendarViewDatasSessao.addDecorators(
                marcadorDiaAtual
        );

        calendarViewDatasSessao.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                txtValorSessaoUnica.clearFocus();
                txtValorPorSessao.clearFocus();
            }
        });

        calendarViewDatasSessao.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                txtValorSessaoUnica.clearFocus();
                txtValorPorSessao.clearFocus();
                dataValida = validaDataSelecionada(date.getYear(), date.getMonth() - 1, date.getDay());
                if (dataValida && verificaCampos()) {
                    isAgenda = true;

                    verificaTipoOrcamento(true);

                    iniciarActivity(date.getYear(), date.getMonth() - 1, date.getDay());

                    dataCopia = date;

                }
            }
        });

        switchSessoes = findViewById(R.id.switchSessoes);
        switchSessaoUnica = findViewById(R.id.switchSessaoUnica);

        //Habilitando inicialmente as sessoes como checked
        switchSessaoUnica.setChecked(true);
        switchSessoes.setChecked(false);

        btnEnviarOrcamento = findViewById(R.id.btnEnviarOrcamento);

        spinnerQtdSessoes = findViewById(R.id.spinnerQtdSessoes);
        spinnerQtdSessoes.setAdapter(ArrayAdapter.createFromResource(this, R.array.qtdSessoes, android.R.layout.simple_spinner_item));
        spinnerQtdSessoes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                calcularValorFinal();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        txtTempoSessaoUnica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtTempoSessaoUnica.setError(null);
                chamarTempo(txtTempoSessaoUnica, true);

            }
        });

        txtTempoPorSessao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtTempoPorSessao.setError(null);
                chamarTempo(txtTempoPorSessao, false);
            }
        });

        txtValorSessaoUnica.addTextChangedListener(new TextWatcher() {
            private String valor = "";

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().equals(valor) && checkOkSessao == true) {
                    txtValorSessaoUnica.removeTextChangedListener(this);

                    String limpandoValor = charSequence.toString().replaceAll("[R$,.]", "");

                    double valorConvertido = Double.parseDouble(limpandoValor);
                    String valorFormatado = NumberFormat.getCurrencyInstance().format((valorConvertido / 100));

                    valor = valorFormatado.replaceAll("[R$]", "");
                    txtValorSessaoUnica.setText("R$ " + valor);
                    txtValorSessaoUnica.setSelection(valor.length() + 3);//definindo cursor para a ultima posicao, fazendo o valor ser add de trás pra frente

                    txtValorSessaoUnica.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        txtValorPorSessao.addTextChangedListener(new TextWatcher() {
            private String valor = "";

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().equals(valor) && checkOkSessoes == true) {
                    txtValorPorSessao.removeTextChangedListener(this);

                    String limpandoValor = charSequence.toString().replaceAll("[R$,.]", "");

                    valorConvertidoDouble = Double.parseDouble(limpandoValor);
                    String valorFormatado = NumberFormat.getCurrencyInstance().format((valorConvertidoDouble / 100));

                    valor = valorFormatado.replaceAll("[R$]", "");
                    txtValorPorSessao.setText("R$ " + valor);
                    txtValorPorSessao.setSelection(valor.length() + 3);//definindo cursor para a ultima posicao, fazendo o valor ser add de trás pra frente

                    txtValorPorSessao.addTextChangedListener(this);

                    //Atualizando valor total
                    calcularValorFinal();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnEnviarOrcamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verificaCampos()&&isAgenda) {
                    calcularValorFinal();
                    verificaTipoOrcamento(false);
                }
            }
        });
        controlaCamposSessaoMultipla(false);

        switchSessaoUnica.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                controlaCamposSessaoUnica(isChecked);
                if (isChecked){
                    checkOkSessao = true;
                    checkOkSessoes = false;
                    switchSessoes.setChecked(false);
                }else {
                    switchSessoes.setChecked(true);
                }
            }
        });

        switchSessoes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                controlaCamposSessaoMultipla(isChecked);
                if (isChecked){
                    checkOkSessoes = true;
                    checkOkSessao = false;
                    switchSessaoUnica.setChecked(false);
                }else {
                    switchSessaoUnica.setChecked(true);
                }
            }
        });
    }

    private void iniciarActivity(int ano, int mes, int dia) {
        Intent intent = new Intent(OrcamentoTattooActivity.this, SelecionaHorarioAgendaActivity.class);
        intent.putExtra("ANO", ano);
        intent.putExtra("MES", mes);
        intent.putExtra("DIA", dia);
        intent.putExtra("telefoneCliente", consultaRecebida.getTelefoneUsuario());
        intent.putExtra("nomeCliente", consultaRecebida.getNomeUsuario());
        intent.putExtra("tipoOrcamento", tipoOrcamento);

        if (checkOkSessao) {
            intent.putExtra("valorSessaoUnica", txtValorSessaoUnica.getText().toString());
            float min = (mHora1 * 60) + mMinuto1;
            intent.putExtra("tempoSessaoUnica", min);
        }
        if (checkOkSessoes) {
            intent.putExtra("valorSessaoMultipla", txtValorPorSessao.getText().toString());
            float min = (mHora2 * 60) + mMinuto2;
            intent.putExtra("tempoSessaoMultipla", min);
        }
        startActivityForResult(intent, 1);
    }

    private void salvarHorario() {
        populandoHorario();
        evento.salvarHorario(consultaRecebida.getIdCliente(), consultaRecebida.getIdConsulta());
    }

    private void verificaTipoOrcamento(boolean isAgenda) {
        if (checkOkSessao && checkOkSessoes) {
            tipoOrcamento = "completo";
            if (!isAgenda)
                salvarOrcamentoCompleto();
        } else if (checkOkSessao) {
            tipoOrcamento = "sessaoUnica";
            if (!isAgenda)
                salvarOrcamentoSessaoUnica();
        } else {
            tipoOrcamento = "sessaoMultipla";
            if (!isAgenda)
                salvarOrcamentoSessaoMultipla();
        }
    }

    private void marcaDataPredefinida(CalendarDay date) {
        LocalDate temp = date.getDate();
        final ArrayList<CalendarDay> dataSelecionada = new ArrayList<>();

        final CalendarDay day = CalendarDay.from(temp);
        dataSelecionada.add(day);

        calendarViewDatasSessao.addDecorator(new MarcadorDatas(Color.RED, dataSelecionada));
    }

    private void populandoHorario() {
        if (definiuHorario.get("horaUnica")!=null){
            evento.setHora(definiuHorario.get("horaUnica"));
            evento.setMinuto(definiuHorario.get("minutoUnico"));

            String horaInicio = String.valueOf(definiuHorario.get("horaInicio"));
            String horaTermino = String.valueOf(definiuHorario.get("horaTermino"));

            evento.setHoraInicio(horaInicio);
            evento.setHoraTermino(horaTermino);
        }
        if (definiuHorario.get("horaMultipla")!=null){
            evento.setHora(definiuHorario.get("horaMultipla"));
            evento.setMinuto(definiuHorario.get("minutoMultiplo"));

            String horaInicio = String.valueOf(definiuHorario.get("horaInicio"));
            String horaTermino = String.valueOf(definiuHorario.get("horaTermino"));

            evento.setHoraInicio(horaInicio);
            evento.setHoraTermino(horaTermino);
        }
        evento.setDataInMillis(dataInMillis);
        evento.setAno(dataCopia.getYear());
        evento.setDia(dataCopia.getDay());
        evento.setMes(dataCopia.getMonth());
        if (checkOkSessao) {
            int min = (mHora1 * 60) + mMinuto1;
            evento.setDuracao(min);
        }
        if (checkOkSessoes) {
            int min = (mHora2 * 60) + mMinuto2;
            evento.setDuracao(min);
        }
    }

    private void populandoObjeto() {
        if (checkOkSessao) {
            //sessao unica
            orcamento.setIdTatuador(idTatuador);
            orcamento.setNomeTatuador(UsuarioFirebase.getNomeUsuario(idTatuador));
            orcamento.setFotoTatuador(UsuarioFirebase.getDadosUsuarioLogado().getCaminhoFoto());
            orcamento.setTempoSessaoUnica(txtTempoSessaoUnica.getText().toString());
            orcamento.setValorTotalSessaoUnica(txtValorSessaoUnica.getText().toString());
            orcamento.setTipoOrcamento(tipoOrcamento);
            orcamento.setFotoTatuagem(consultaRecebida.getFotoConsulta());
            orcamento.setData(String.valueOf(dataCopia.getDate()));
            orcamento.setHora(definiuHorario.get("horaUnica") + ":" + definiuHorario.get("minutoUnico"));
        }
        if (checkOkSessoes) {
            //Sessao multipla
            orcamento.setIdTatuador(idTatuador);
            orcamento.setNomeTatuador(UsuarioFirebase.getNomeUsuario(idTatuador));
            orcamento.setFotoTatuador(UsuarioFirebase.getDadosUsuarioLogado().getCaminhoFoto());
            orcamento.setValorPorSessao(txtValorPorSessao.getText().toString());
            orcamento.setTempoSessoesMultiplas(txtTempoPorSessao.getText().toString());
            orcamento.setQtdSessoes(qtdSessoes);
            orcamento.setTipoOrcamento(tipoOrcamento);
            orcamento.setValorTotalSessaoMultipla(valorFormatadoSessaoMultipla.replace("$", "$ "));
            orcamento.setFotoTatuagem(consultaRecebida.getFotoConsulta());
            orcamento.setData(String.valueOf(dataCopia.getDate()));
            orcamento.setHora(definiuHorario.get("horaMultipla") + ":" + definiuHorario.get("minutoMultiplo"));
        }
    }

    private boolean validaDataSelecionada(int ano, int mes, int dia) {

        Calendar dataSelecionada = Calendar.getInstance();
        dataSelecionada.set(ano, mes, dia);//setando a data passada por parametro

        Calendar dataAtual = Calendar.getInstance();//instanciando a dataAtual

        //Data selecionada nao pode ser menor que a data atual
        if (dataSelecionada.getTimeInMillis() < dataAtual.getTimeInMillis()) {
            Toast.makeText(getApplicationContext(), "Selecione uma data válida!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void salvarOrcamentoCompleto() {
        populandoObjeto();
        if (tipoConsulta.equals("consultaPrivada")) {
            if (orcamento.salvarConsultaPrivada(tipoOrcamento)) {
                excluirOrcamento();
                Toast.makeText(getApplicationContext(), "Sucesso ao enviar orçamento", Toast.LENGTH_SHORT).show();
                definiuDia = false;
                Intent intent = new Intent(OrcamentoTattooActivity.this, MensagensActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);//Limpando pilha de activities
                startActivity(intent);
                finish();
            } else {
                if (orcamento.salvarConsultaAberta(tipoOrcamento)) {
                    excluirOrcamento();
                    definiuDia = false;
                    Toast.makeText(getApplicationContext(), "Sucesso ao enviar orçamento", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(OrcamentoTattooActivity.this, MensagensActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);//Limpando pilha de activities
                    startActivity(intent);
                    finish();
                }
            }
        }
    }

    private void salvarOrcamentoSessaoUnica() {
        populandoObjeto();
        if (tipoConsulta.equals("consultaPrivada")) {
            if (orcamento.salvarConsultaPrivada(tipoOrcamento)) {
                excluirOrcamento();
                definiuDia = false;
                Toast.makeText(getApplicationContext(), "Sucesso ao enviar orçamento", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OrcamentoTattooActivity.this, MensagensActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);//Limpando pilha de activities
                startActivity(intent);
                finish();
            }
        } else {
            if (orcamento.salvarConsultaAberta(tipoOrcamento)) {
                excluirOrcamento();
                definiuDia = false;
                Toast.makeText(getApplicationContext(), "Sucesso ao enviar orçamento", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OrcamentoTattooActivity.this, MensagensActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);//Limpando pilha de activities
                startActivity(intent);
                finish();
            }
        }
    }

    private void salvarOrcamentoSessaoMultipla() {
        populandoObjeto();
        if (tipoConsulta.equals("consultaPrivada")) {
            if (orcamento.salvarConsultaPrivada(tipoOrcamento)) {
                excluirOrcamento();
                definiuDia = false;
                Toast.makeText(getApplicationContext(), "Sucesso ao enviar orçamento", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OrcamentoTattooActivity.this, MensagensActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);//Limpando pilha de activities
                startActivity(intent);
                finish();
            }
        } else {
            if (orcamento.salvarConsultaAberta(tipoOrcamento)) {
                excluirOrcamento();
                definiuDia = false;
                Toast.makeText(getApplicationContext(), "Sucesso ao enviar orçamento", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OrcamentoTattooActivity.this, MensagensActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);//Limpando pilha de activities
                startActivity(intent);
                finish();
            }
        }
    }

    private void excluirOrcamento() {

        if(tipoConsulta.equals("consultaPrivada")){
            orcamentoRef = ConfiguracaoFirebase.getFirebase()
                    .child("consultaFechada")
                    .child(idTatuador)
                    .child(consultaRecebida.getIdCliente())
                    .child(consultaRecebida.getIdConsulta());
            orcamentoRef.removeValue();
        }
        else {
            orcamentoRef = ConfiguracaoFirebase.getFirebase()
                    .child("consultaAberta")
                    .child(consultaRecebida.getIdCliente())
                    .child(consultaRecebida.getIdConsulta())
                    .child(idTatuador);
            orcamentoRef.removeValue();
        }
    }

    private void limparCamposSessaoUnica() {
        checkOkSessao = false;
        txtTempoSessaoUnica.setText("");
        txtValorSessaoUnica.setText("");
        txtTempoSessaoUnica.setError(null);
        txtValorSessaoUnica.setError(null);
    }

    private void limparCamposSessaoMultipla() {
        checkOkSessoes = false;
        txtValorPorSessao.setText("");
        txtTempoPorSessao.setText("");
        txtValorPorSessao.setError(null);
        txtTempoPorSessao.setError(null);
        spinnerQtdSessoes.setSelection(0);
        txtValorSessoes.setText("");
    }

    private void controlaCamposSessaoUnica(boolean habilitado) {
        limparCamposSessaoUnica();
        txtTempoSessaoUnica.setEnabled(habilitado);
        txtValorSessaoUnica.setEnabled(habilitado);
    }

    private void controlaCamposSessaoMultipla(boolean habilitado) {
        limparCamposSessaoMultipla();
        txtValorPorSessao.setEnabled(habilitado);
        txtTempoPorSessao.setEnabled(habilitado);
        spinnerQtdSessoes.setEnabled(habilitado);
        txtValorSessoes.setEnabled(habilitado);
    }

    private void calcularValorFinal() {
        if (!spinnerQtdSessoes.getSelectedItem().equals("") && !txtValorPorSessao.getText().toString().equals("")) {
            qtdSessoes = Integer.parseInt(getQtdSessoes());
            double valorSessao = valorConvertidoDouble, total;
            total = valorSessao * qtdSessoes;
            valorFormatadoSessaoMultipla = NumberFormat.getCurrencyInstance().format((total / 100));
            txtValorSessoes.setText("Valor total: " + valorFormatadoSessaoMultipla);
        }
    }

    private boolean verificaCampos() {
        if (checkOkSessao) {
            if (txtTempoSessaoUnica.getText().toString().equals("")) {
                txtTempoSessaoUnica.setError("Preencha esse campo!");
                return false;
            }
            if (txtValorSessaoUnica.getText().toString().equals("")) {
                txtValorSessaoUnica.setError("Preencha esse campo!");
                return false;
            }
        }
        if (checkOkSessoes) {
            if (txtValorPorSessao.getText().toString().equals("")) {
                txtValorPorSessao.setError("Preencha esse campo!");
                return false;
            }
            if (txtTempoPorSessao.getText().toString().equals("")) {
                txtTempoPorSessao.setError("Preencha esse campo!");
                return false;
            }
            if (spinnerQtdSessoes.getSelectedItem().equals("")) {
                Toast.makeText(getApplicationContext(), "Selecione a Quantidade de sessões!", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (txtValorSessoes.getText().toString().equals("")) {
                txtValorSessoes.setError("Preencha esse campo!");
                return false;
            }
        }
        return true;
    }

    private String getQtdSessoes() {
        String qtdSessoes = (String) spinnerQtdSessoes.getSelectedItem();
        return qtdSessoes;
    }

    private void chamarTempo(final EditText editText, boolean ehUnica) {
        TimePickerDialog timePickerDialog;

        if (ehUnica) {
            timePickerDialog = new TimePickerDialog(this, 2, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hora, int minuto) {

                    DecimalFormat df = new DecimalFormat("00");//formatando para sempre ter 2 digitos em hora e minuto
                    editText.setText(df.format(hora) + "h " + ":" + df.format(minuto) + "m");

                    //Setando o ultimo valor clicado como padrao caso o usuario clique novamente
                    mHora1 = hora;
                    mMinuto1 = minuto;
                }
            }, mHora1, mMinuto1, true);
            timePickerDialog.show();

        } else {
            timePickerDialog = new TimePickerDialog(this, 2, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hora, int minuto) {

                    DecimalFormat df = new DecimalFormat("00");//formatando para sempre ter 2 digitos em hora e minuto
                    editText.setText(df.format(hora) + "h " + ":" + df.format(minuto) + "m");

                    //Setando o ultimo valor clicado como padrao caso o usuario clique novamente
                    mHora2 = hora;
                    mMinuto2 = minuto;
                }
            }, mHora2, mMinuto2, true);
            timePickerDialog.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {

            definiuHorario = (HashMap<String, Integer>) data.getSerializableExtra("definiuHorario");
            definiuDia = data.getBooleanExtra("definiuDia",false);
            dataInMillis = data.getLongExtra("dataInMillis",0);

            if (definiuDia) {
                marcaDataPredefinida(dataCopia);
                salvarHorario();
            }
            else
                isAgenda=false;
        }
    }

    //corrigindo btnVoltar
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}