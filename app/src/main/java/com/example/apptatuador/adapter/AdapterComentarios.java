package com.example.apptatuador.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.apptatuador.R;
import com.example.apptatuador.model.Comentario;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterComentarios extends RecyclerView.Adapter<AdapterComentarios.MyViewHolder>{
    private List<Comentario> listaComentarios;
    private Context context;

    public AdapterComentarios(List<Comentario> listaComentarios, Context context) {
        this.listaComentarios = listaComentarios;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterComentarios.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_comentarios, parent, false);
        return new AdapterComentarios.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterComentarios.MyViewHolder holder, int position) {
        Comentario comentario = listaComentarios.get(position);
        Glide.with(context).load(comentario.getCaminhoFoto()).into(holder.fotoComentario);
        holder.nome.setText(comentario.getNomeUsuario());
        holder.comentario.setText(comentario.getComentario());
    }

    @Override
    public int getItemCount() {
        return listaComentarios.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        CircleImageView fotoComentario;
        TextView nome, comentario;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            fotoComentario = itemView.findViewById(R.id.imgPostagemComentario);
            nome = itemView.findViewById(R.id.lblNomeUsuarioComentario);
            comentario = itemView.findViewById(R.id.lblComentarioPostagem);
        }
    }
}
