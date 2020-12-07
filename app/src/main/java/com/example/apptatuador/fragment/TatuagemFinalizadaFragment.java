package com.example.apptatuador.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apptatuador.R;
import com.example.apptatuador.activity.VisualizaTatuagemFinalizadaActivity;
import com.example.apptatuador.adapter.AdapterTatuagemFinalizada;
import com.example.apptatuador.helper.ConfiguracaoFirebase;
import com.example.apptatuador.helper.RecyclerItemClickListener;
import com.example.apptatuador.helper.UsuarioFirebase;
import com.example.apptatuador.model.Evento;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TatuagemFinalizadaFragment extends Fragment {
    private RecyclerView recyclerViewTatuagemFinalizada;
    private TextView lblSemTatuagem;
    private DatabaseReference tatuagemFinalizadaRef;
    private List<Evento> listaTatuagemFinalizada;
    private ValueEventListener valueEventListenerTatuagemFinalizada;
    private AdapterTatuagemFinalizada adapterTatuagemFinalizada;
    private boolean existeTatuagem = false;

    public TatuagemFinalizadaFragment() {
        tatuagemFinalizadaRef = ConfiguracaoFirebase.getFirebase()
                .child("tatuagemFinalizada");

        listaTatuagemFinalizada = new ArrayList<>();
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarTatuagensFinalizadas();
    }

    @Override
    public void onStop() {
        super.onStop();
        tatuagemFinalizadaRef.removeEventListener(valueEventListenerTatuagemFinalizada);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tatuagem_finalizada, container, false);

        recyclerViewTatuagemFinalizada = view.findViewById(R.id.recyclerViewTatuagemFinalizada);
        lblSemTatuagem = view.findViewById(R.id.lblSemTatuagem);

        lblSemTatuagem.setVisibility(View.GONE);

        recyclerViewTatuagemFinalizada.addOnItemTouchListener(new RecyclerItemClickListener(
                getContext(), recyclerViewTatuagemFinalizada, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //Instanciando um objeto de Proposta
                Evento evento = listaTatuagemFinalizada.get(position);

                Intent i = new Intent(getContext(), VisualizaTatuagemFinalizadaActivity.class);
                i.putExtra("tatuagemSelecionada", evento);//enviando objeto instanciado via intent
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

        adapterTatuagemFinalizada = new AdapterTatuagemFinalizada(listaTatuagemFinalizada,getContext());
        recyclerViewTatuagemFinalizada.setHasFixedSize(true);
        recyclerViewTatuagemFinalizada.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewTatuagemFinalizada.setAdapter(adapterTatuagemFinalizada);
        return view;
    }

    private void recuperarTatuagensFinalizadas() {
        valueEventListenerTatuagemFinalizada = tatuagemFinalizadaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaTatuagemFinalizada.clear();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    if(snap.hasChild(UsuarioFirebase.getIdentificadorUsuario())){
                        for (DataSnapshot ds : snap.getChildren()) {
                            for (DataSnapshot ds1 : ds.getChildren()){
                                listaTatuagemFinalizada.add(ds1.getValue(Evento.class));
                                existeTatuagem = true;
                            }
                        }
                    }
                }
                adapterTatuagemFinalizada.notifyDataSetChanged();

                if (!existeTatuagem){
                    recyclerViewTatuagemFinalizada.setVisibility(View.GONE);
                    lblSemTatuagem.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}