package com.example.apptatuador.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.apptatuador.R;
import com.example.apptatuador.activity.ComentariosActivity;
import com.example.apptatuador.helper.ConfiguracaoFirebase;
import com.example.apptatuador.helper.UsuarioFirebase;
import com.example.apptatuador.model.Feed;
import com.example.apptatuador.model.PostagemCurtida;
import com.example.apptatuador.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterFeed extends RecyclerView.Adapter<AdapterFeed.MyViewHolder> {

    private List<Feed> listaFeed;
    private Context context;

    public AdapterFeed(List<Feed> listaFeed, Context context) {
        this.listaFeed = listaFeed;
        this.context = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_feed, parent, false);
        return new AdapterFeed.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        final Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        final Feed  feed = listaFeed.get(position);

        //Carrega dados para o feed
        Uri uriFotoUsuario = Uri.parse(feed.getFotoUsuario());
        Uri uriFotoPostagem = Uri.parse(feed.getFotoPostagem());

        Glide.with(context).load(uriFotoUsuario).into(holder.fotoPerfil);
        Glide.with(context).load(uriFotoPostagem).into(holder.fotoPostagem);

        holder.descricao.setText(feed.getDescricao());
        holder.nome.setText(feed.getNomeUsuario());
        holder.visualizarComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ComentariosActivity.class);
                i.putExtra("idPostagem", feed.getId());
                context.startActivity(i);
            }
        });

        //Recuperar dados da postagem curtida
        DatabaseReference curtidasRef = ConfiguracaoFirebase.getFirebase()
                .child("postagens_curtidas").child(feed.getId());
        curtidasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int qtdLike = 0;
                if(dataSnapshot.hasChild("qtd_like")){
                    int likes = dataSnapshot.child("qtd_like").getValue(Integer.class);
                    qtdLike = likes;
                }
                //Verifica se ja foi clicado
                if (dataSnapshot.hasChild(usuarioLogado.getId())){
                    holder.btnCurtirFeed.setLiked(true);
                }else{
                    holder.btnCurtirFeed.setLiked(false);
                }

                //Monta objeto para curtida
                final PostagemCurtida curtida = new PostagemCurtida();
                curtida.setFeed(feed);
                curtida.setUsuario(usuarioLogado);
                curtida.setQtdLike(qtdLike);

                //Add evento de curtida
                holder.btnCurtirFeed.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {
                        curtida.salvarCurtida();
                        holder.qtdCurtidas.setText(curtida.getQtdLike()+" curtidas");
                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {
                        curtida.removerCurtida();
                        holder.qtdCurtidas.setText(curtida.getQtdLike()+" curtidas");
                    }
                });
                holder.qtdCurtidas.setText(curtida.getQtdLike()+" curtidas");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return listaFeed.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        CircleImageView fotoPerfil;
        TextView nome, descricao, qtdCurtidas;
        ImageView fotoPostagem, visualizarComentario;
        LikeButton btnCurtirFeed;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            fotoPostagem = itemView.findViewById(R.id.imgPostagemSelecionada);
            fotoPerfil = itemView.findViewById(R.id.imgPerfilPostagem);
            nome = itemView.findViewById(R.id.lblNomeUsuarioPostagem);
            qtdCurtidas = itemView.findViewById(R.id.lblQtdCurtidas);
            descricao = itemView.findViewById(R.id.lblDescricaoPostagemSelecionada);
            visualizarComentario = itemView.findViewById(R.id.imgComentarioFeed);
            btnCurtirFeed = itemView.findViewById(R.id.btnCurtirFeed);

        }
    }
}

