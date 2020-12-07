package com.example.apptatuador.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.apptatuador.R;
import com.example.apptatuador.model.Evento;

import java.text.DecimalFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterTatuagemFinalizada extends RecyclerView.Adapter<AdapterTatuagemFinalizada.MyViewHolder> {

    private List<Evento> listaTatuagemFinalizada;
    private Context context;

    public AdapterTatuagemFinalizada(List<Evento> listaTatuagemFinalizada, Context context) {
        this.listaTatuagemFinalizada = listaTatuagemFinalizada;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_tatuagem_finalizada, parent, false);
        return new AdapterTatuagemFinalizada.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final Evento evento = listaTatuagemFinalizada.get(position);

        //Carrega dados para o recyclerview

        Uri uriFotoUsuario = Uri.parse(evento.getFotoUsuario());
        if (!evento.getFotoUsuario().equals("") && evento.getFotoUsuario() != null){
            Glide.with(context).load(uriFotoUsuario).into(holder.fotoPerfil);
        }
        else
            holder.fotoPerfil.setImageResource(R.drawable.avatar);

        holder.nome.setText("@"+evento.getNome());
        holder.valor.setText(evento.getValor());

        holder.data.setText(evento.getDia()+"/"+evento.getMes()+"/"+evento.getAno());
        DecimalFormat df = new DecimalFormat("00");
        holder.hora.setText(df.format(evento.getHora())+ "h" + ":" + df.format(evento.getMinuto()) + "m");
    }

    @Override
    public int getItemCount() {
        return listaTatuagemFinalizada.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        CircleImageView fotoPerfil;
        TextView nome, data, hora, valor;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            fotoPerfil = itemView.findViewById(R.id.imgFotoTatuadorFinalizado);
            nome = itemView.findViewById(R.id.lblNomeTatuadorFinalizado);
            data = itemView.findViewById(R.id.lblDataFinalizado);
            hora = itemView.findViewById(R.id.lblHoraInicioFinalizado);
            valor = itemView.findViewById(R.id.lblValorFinalizado);
        }
    }
}

