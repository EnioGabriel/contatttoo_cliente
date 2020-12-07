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
import com.example.apptatuador.model.Estudio;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterPesquisaEstudio extends RecyclerView.Adapter<AdapterPesquisaEstudio.MyViewHolder> {
    private List<Estudio> listaEstudio;
    private Context context;

    public AdapterPesquisaEstudio(List<Estudio> l, Context c) {
        this.listaEstudio = l;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_adapter_pesquisa_estudio, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Estudio estudio = listaEstudio.get(position);
        holder.nome.setText(estudio.getNomeEstudio());
        if(estudio.getCaminhoFoto()!= null){
            Uri url = Uri.parse(estudio.getCaminhoFoto());
            Log.i("Context", "contexto: "+context );
            Glide.with(context).load(url).into(holder.foto);
        }else {
            Log.i("Context", "contexto: "+context );
            holder.foto.setImageResource(R.drawable.avatar);
        }

    }

    @Override
    public int getItemCount() {
        return listaEstudio.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        CircleImageView foto;
        TextView nome;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            foto = itemView.findViewById(R.id.imageViewPesquisaEstudio);
            nome = itemView.findViewById(R.id.lblNomeEstudio);
        }
    }
}