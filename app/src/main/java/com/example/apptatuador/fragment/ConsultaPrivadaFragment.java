package com.example.apptatuador.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.apptatuador.R;
import com.example.apptatuador.activity.ConsultaTattooActivity;
import com.example.apptatuador.adapter.AdapterConsulta;
import com.example.apptatuador.helper.ConfiguracaoFirebase;
import com.example.apptatuador.helper.RecyclerItemClickListener;
import com.example.apptatuador.helper.UsuarioFirebase;
import com.example.apptatuador.model.Consulta;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConsultaPrivadaFragment extends Fragment {

    private RecyclerView recyclerViewMensagem;
    private TextView lblSemConsulta;

    private String idTatuadorLogado;
    private List<Consulta> listaConsulta;

    private DatabaseReference consultaPrivadaRef;

    private AdapterConsulta adapterConsulta;

    private ValueEventListener valueEventListenerConsultaPrivada;

    public ConsultaPrivadaFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        idTatuadorLogado = UsuarioFirebase.getIdentificadorUsuario();

        consultaPrivadaRef = ConfiguracaoFirebase.getFirebase()
                .child("consultaFechada")
                .child(idTatuadorLogado);

        listaConsulta = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_consulta_privada, container, false);

        recyclerViewMensagem = view.findViewById(R.id.recyclerViewConsultaPrivada);
        lblSemConsulta = view.findViewById(R.id.lblConsultaRecebidaPrivada);
        lblSemConsulta.setVisibility(View.GONE);

        adapterConsulta = new AdapterConsulta(listaConsulta, getContext());
        recyclerViewMensagem.setHasFixedSize(true);
        recyclerViewMensagem.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewMensagem.setAdapter(adapterConsulta);

        recyclerViewMensagem.addOnItemTouchListener(new RecyclerItemClickListener(
                getContext(),
                recyclerViewMensagem,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        //Instanciando um objeto de msg
                        Consulta consulta = listaConsulta.get(position);

                        Intent i = new Intent(getContext(), ConsultaTattooActivity.class);
                        i.putExtra("tipoConsulta", "consultaPrivada");
                        i.putExtra("consultaSelecionada", consulta);//enviando objeto instanciado via intent
                        startActivity(i);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarConsultaPrivada();
    }

    @Override
    public void onStop() {
        super.onStop();
        consultaPrivadaRef.removeEventListener(valueEventListenerConsultaPrivada);
    }

    private void recuperarConsultaPrivada(){
        valueEventListenerConsultaPrivada = consultaPrivadaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaConsulta.clear();
                DataSnapshot ds=null;
                for (DataSnapshot snap : dataSnapshot.getChildren()){
                    ds=snap;
                }
                if (ds!=null){
                    for (DataSnapshot ds1 : ds.getChildren()){
                        listaConsulta.add(ds1.getValue(Consulta.class));
                    }
                }
                Collections.reverse(listaConsulta);//Invertendo ordem da lista
                adapterConsulta.notifyDataSetChanged();

                if (!dataSnapshot.exists()){
                    recyclerViewMensagem.setVisibility(View.GONE);
                    lblSemConsulta.setVisibility(View.VISIBLE);
                    //txtApagaDps.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}