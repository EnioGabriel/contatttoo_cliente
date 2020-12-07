package com.example.apptatuador.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.apptatuador.R;
import com.example.apptatuador.model.Usuario;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterPesquisa extends RecyclerView.Adapter<AdapterPesquisa.MyViewHolder> {
    private List<Usuario> listaUsuario;
    private Context context;

    public AdapterPesquisa(List<Usuario> l, Context c) {
        this.listaUsuario = l;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_adapter_pesquisa, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Usuario usuario = listaUsuario.get(position);
        holder.nome.setText(usuario.getNome());
        holder.nomeUsuario.setText("@"+usuario.getNomeUsuario());
        if(usuario.getCaminhoFoto()!= null){
            Uri url = Uri.parse(usuario.getCaminhoFoto());
            Glide.with(context).load(url).into(holder.foto);
        }else {
            holder.foto.setImageResource(R.drawable.avatar);
        }

    }

    @Override
    public int getItemCount() {
        return listaUsuario.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        CircleImageView foto;
        TextView nome;
        TextView nomeUsuario;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            foto = itemView.findViewById(R.id.imageViewPesquisa);
            nome = itemView.findViewById(R.id.lblNome);
            nomeUsuario = itemView.findViewById(R.id.lblNomePerfil);
        }
    }
}