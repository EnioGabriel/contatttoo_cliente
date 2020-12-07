package com.example.apptatuador.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.apptatuador.R;
import com.example.apptatuador.activity.VisualizaPropostasAceitas;
import com.example.apptatuador.model.Evento;

import java.text.DecimalFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterPropostasAceitas extends RecyclerView.Adapter<AdapterPropostasAceitas.MyViewHolder> {

    private List<Evento> listaPropostasAceitas;
    private Context context;

    public AdapterPropostasAceitas(List<Evento> listaPropostasAceitas, Context context) {
        this.listaPropostasAceitas = listaPropostasAceitas;
        this.context = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_propostas_aceitas, parent, false);
        return new AdapterPropostasAceitas.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final Evento evento = listaPropostasAceitas.get(position);

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

        holder.btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Instanciando um objeto de Proposta
                Intent i = new Intent(context, VisualizaPropostasAceitas.class);
                i.putExtra("tatuagemSelecionada", evento);//enviando objeto instanciado via intent
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaPropostasAceitas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        CircleImageView fotoPerfil;
        TextView nome, data, hora, valor;
        Button btnConfirmar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            fotoPerfil = itemView.findViewById(R.id.imgFotoTatuadorAndamento);
            nome = itemView.findViewById(R.id.lblNomeTatuadorAndamento);
            data = itemView.findViewById(R.id.lblDataAndamento);
            hora = itemView.findViewById(R.id.lblHoraInicioAndamento);
            valor = itemView.findViewById(R.id.lblValorAndamento);
            btnConfirmar = itemView.findViewById(R.id.btnVisualizarInformacoesTatuagem);
        }
    }
}
