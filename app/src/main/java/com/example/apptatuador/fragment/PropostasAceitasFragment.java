package com.example.apptatuador.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.apptatuador.R;
import com.example.apptatuador.adapter.AdapterPropostasAceitas;
import com.example.apptatuador.helper.ConfiguracaoFirebase;
import com.example.apptatuador.helper.UsuarioFirebase;
import com.example.apptatuador.model.Evento;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PropostasAceitasFragment extends Fragment {

    private RecyclerView recyclerViewTatuagemAndamento;
    private TextView lblAgendaVazia;
    private DatabaseReference tatuagemAndamentoRef;
    private List<Evento> listaTatuagemAndamento;
    private ValueEventListener valueEventListenerTatuagemAndamento;
    private AdapterPropostasAceitas adapterTatuagemAndamento;

    public PropostasAceitasFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tatuagemAndamentoRef = ConfiguracaoFirebase.getFirebase()
                .child("evento")
                .child(UsuarioFirebase.getIdentificadorUsuario());

        listaTatuagemAndamento = new ArrayList<>();
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarPropostas();
    }

    @Override
    public void onStop() {
        super.onStop();
        tatuagemAndamentoRef.removeEventListener(valueEventListenerTatuagemAndamento);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_propostas_aceitas, container, false);

        recyclerViewTatuagemAndamento = view.findViewById(R.id.recyclerViewPropostasAceitas);
        lblAgendaVazia = view.findViewById(R.id.lblAgendaVazia);
        lblAgendaVazia.setVisibility(View.GONE);

        adapterTatuagemAndamento = new AdapterPropostasAceitas(listaTatuagemAndamento,getContext());
        recyclerViewTatuagemAndamento.setHasFixedSize(true);
        recyclerViewTatuagemAndamento.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewTatuagemAndamento.setAdapter(adapterTatuagemAndamento);

        return view;
    }

    private void recuperarPropostas() {
        valueEventListenerTatuagemAndamento = tatuagemAndamentoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaTatuagemAndamento.clear();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    if (snap.hasChild("fotoTatuagem")) {
                        listaTatuagemAndamento.add(snap.getValue(Evento.class));
                    }
                }
                adapterTatuagemAndamento.notifyDataSetChanged();

                if (!dataSnapshot.exists()){
                    recyclerViewTatuagemAndamento.setVisibility(View.GONE);
                    lblAgendaVazia.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}