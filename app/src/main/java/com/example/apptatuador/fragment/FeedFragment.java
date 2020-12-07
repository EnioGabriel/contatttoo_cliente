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
import com.example.apptatuador.adapter.AdapterFeed;
import com.example.apptatuador.helper.ConfiguracaoFirebase;
import com.example.apptatuador.helper.UsuarioFirebase;
import com.example.apptatuador.model.Feed;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class FeedFragment extends Fragment {

    private TextView lblMsgSeguir,txtApagaDps;
    private RecyclerView recyclerFeed;

    private AdapterFeed adapterFeed;
    private List<Feed> listaFeed;

    private String idUsuarioLogado;

    private ValueEventListener valueEventListenerFeed;

    private DatabaseReference feedRef;

    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();
        feedRef = ConfiguracaoFirebase.getFirebase()
                .child("feed")
                .child(idUsuarioLogado);

        recyclerFeed = view.findViewById(R.id.recyclerFeed);
        lblMsgSeguir = view.findViewById(R.id.lblMsgSeguir);
        //txtApagaDps = view.findViewById(R.id.txtApagaDps);

        listaFeed = new ArrayList<>();

        adapterFeed = new AdapterFeed(listaFeed, getActivity());
        recyclerFeed.setHasFixedSize(true);
        recyclerFeed.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerFeed.setAdapter(adapterFeed);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarFeed();
    }

    @Override
    public void onStop() {
        super.onStop();
        feedRef.removeEventListener(valueEventListenerFeed);
    }

    private void recuperarFeed(){
        valueEventListenerFeed = feedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaFeed.clear();
                DataSnapshot ds=null;
                for (DataSnapshot snap : dataSnapshot.getChildren()){
                    ds=snap;
                }
                if (ds!=null&&ds.getKey()!=idUsuarioLogado){
                    for (DataSnapshot ds1 : ds.getChildren()){
                        listaFeed.add(ds1.getValue(Feed.class));
                    }
                }
                Collections.reverse(listaFeed);//Invertendo ordem da lista
                adapterFeed.notifyDataSetChanged();

                if (!dataSnapshot.exists()){
                    recyclerFeed.setVisibility(View.GONE);
                    lblMsgSeguir.setVisibility(View.VISIBLE);
                    //txtApagaDps.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}