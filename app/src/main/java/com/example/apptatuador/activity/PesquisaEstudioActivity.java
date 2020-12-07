package com.example.apptatuador.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apptatuador.R;
import com.example.apptatuador.adapter.AdapterPesquisaEstudio;
import com.example.apptatuador.helper.ConfiguracaoFirebase;
import com.example.apptatuador.helper.RecyclerItemClickListener;
import com.example.apptatuador.helper.UsuarioFirebase;
import com.example.apptatuador.model.Estudio;
import com.example.apptatuador.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PesquisaEstudioActivity extends AppCompatActivity {

    private RecyclerView recyclerViewPesquisa;
    private SearchView searchViewPesquisaEstudio;
    private TextView lblCadEstudio;
    private View viewPesquisaEstudio;

    private List<Estudio> listaEstudios;
    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioLogadoRef;
    private DatabaseReference usuarioRef;

    private AdapterPesquisaEstudio adapterPesquisa;

    private String idUsuarioLogado;

    private Estudio estudioSelecionado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesquisa_estudio);


        usuarioRef = ConfiguracaoFirebase.getFirebase().child("tatuadores");

        viewPesquisaEstudio = findViewById(R.id.viewPesquisaEstudio);
        lblCadEstudio = findViewById(R.id.lblCadEstudio);
        recyclerViewPesquisa = findViewById(R.id.recyclerViewPesquisa);
        searchViewPesquisaEstudio = findViewById(R.id.searchViewPesquisaEstudio);
        searchViewPesquisaEstudio.setQueryHint("Pesquisar");
        searchViewPesquisaEstudio.onActionViewExpanded();

        listaEstudios = new ArrayList<>();
        //Referenciando o nó para pesquisa
        usuariosRef = ConfiguracaoFirebase.getFirebase().child("estudio");

        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();

        //Configurar Recycler
        recyclerViewPesquisa.setHasFixedSize(true);
        recyclerViewPesquisa.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        adapterPesquisa = new AdapterPesquisaEstudio(listaEstudios, PesquisaEstudioActivity.this);
        recyclerViewPesquisa.setAdapter(adapterPesquisa);

        //configurar evento de clique
        recyclerViewPesquisa.addOnItemTouchListener(new RecyclerItemClickListener(
                getApplicationContext(),
                recyclerViewPesquisa,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        estudioSelecionado = listaEstudios.get(position);
                        abrirDialog();
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));

        //Configurar pesquisa
        searchViewPesquisaEstudio.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String novoTexto) {
                String textoDigitado = novoTexto.toLowerCase();
                pesquisarUsuarios(textoDigitado);
                return true;
            }
        });

        lblCadEstudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PesquisaEstudioActivity.this, CadastroEstudioActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        listarTodosEstudios();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //impedindo SearchView de ser iniciada com clique
        viewPesquisaEstudio.requestFocus();
        searchViewPesquisaEstudio.setQuery("",false);
    }


    private void abrirDialog(){
        new AlertDialog.Builder(this, R.style.AppTheme_Dialog)
                .setTitle("Local de trabalho")
                .setMessage("Deseja adicionar "+estudioSelecionado.getNomeEstudio()+" como seu local de trabalho?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        estudioSelecionado.funcionarioEstudio(idUsuarioLogado,estudioSelecionado);
                        Intent i = new Intent(PesquisaEstudioActivity.this, MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);//Limpando pilha de activities
                        startActivity(i);
                        finish();
                    }
                })
                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    private void listarTodosEstudios(){
        Query query = usuariosRef.orderByChild("nomePesquisaEstudio");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaEstudios.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Estudio estudio = snapshot.getValue(Estudio.class);
                    listaEstudios.add(estudio);
                }
                adapterPesquisa.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void pesquisarUsuarios(final String texto){
        //limpar lista
        listaEstudios.clear();
        if (texto.length()>0){
            Query query = usuariosRef.orderByChild("nomePesquisaEstudio")
                    .startAt(texto)
                    .endAt(texto+"\uf8ff");// \uf8ff espaco em branco
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    listaEstudios.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Estudio estudio = snapshot.getValue(Estudio.class);
                        listaEstudios.add(estudio);
                    }
                    adapterPesquisa.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }else {
            listarTodosEstudios();
        }
    }
}