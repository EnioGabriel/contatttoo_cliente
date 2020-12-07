package com.example.apptatuador.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.apptatuador.R;
import com.example.apptatuador.model.Estudio;

import java.util.List;

public class AdapterEndereco extends BaseAdapter {
    private List<Estudio> estudio_local;
    private LayoutInflater inflater;

    public AdapterEndereco(Context context, List<Estudio> estudioEndereco){
        inflater = LayoutInflater.from(context);
        estudio_local = estudioEndereco;
    }

    private static class ViewHolder{
        TextView lblCEP;
        TextView lblRua;
        TextView lblBairro;

        void setViews( View view ){
            lblCEP =  view.findViewById(R.id.lblCEP);
            lblRua =   view.findViewById(R.id.lblRua);
            lblBairro = view.findViewById(R.id.lblBairro);
        }

        void setData( Estudio estudio ){
            lblCEP.setText( "CEP: "+estudio.getCep());
            lblCEP.setTag(estudio.getCep());
            lblRua.setText( "Rua: "+estudio.getLogradouro());
            lblBairro.setText( "Bairro: "+estudio.getBairro());
        }
    }

    @Override
    public int getCount() {
        return estudio_local.size();
    }

    @Override
    public Object getItem(int i) {
        return estudio_local.get( i );
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if( view == null ){
            view = inflater.inflate(R.layout.activity_adapter_estudio, null);
            holder = new ViewHolder();
            view.setTag( holder );
            holder.setViews( view );
        }
        else{
            holder = (ViewHolder) view.getTag();
        }
        holder.setData( estudio_local.get( i ) );

        return view;
    }
}
