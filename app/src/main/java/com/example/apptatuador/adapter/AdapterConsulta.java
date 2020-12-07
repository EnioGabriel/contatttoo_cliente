package com.example.apptatuador.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.apptatuador.R;
import com.example.apptatuador.model.Consulta;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterConsulta extends RecyclerView.Adapter<AdapterConsulta.MyViewHolder> {

    private List<Consulta> listaConsulta;
    private Context context;

    public AdapterConsulta(List<Consulta> listaConsulta, Context context) {
        this.listaConsulta = listaConsulta;
        this.context = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_mensagem, parent, false);
        return new AdapterConsulta.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        final Consulta msg = listaConsulta.get(position);

        //Carrega dados para o recyclerview
        Uri uriFotoUsuario = Uri.parse(msg.getFotoUsuario());
        if (!msg.getFotoUsuario().equals("") && msg.getFotoUsuario() != null){
            Glide.with(context).load(uriFotoUsuario).into(holder.fotoPerfil);
        }
        else
            holder.fotoPerfil.setImageResource(R.drawable.avatar);

        holder.nome.setText("@"+msg.getNomeUsuario());
    }

    @Override
    public int getItemCount() {
        return listaConsulta.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        CircleImageView fotoPerfil;
        TextView nome;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            fotoPerfil = itemView.findViewById(R.id.imgFotoUsuarioConsulta);
            nome = itemView.findViewById(R.id.lblNomeUsuarioConsulta);
        }
    }
}
