package com.alenic.greenmeet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alenic.greenmeet.data.Act;
import com.bumptech.glide.Glide;

import java.util.List;

public class ActAdapter extends RecyclerView.Adapter<ActAdapter.ActViewHolder> {

    private List<Act> lista;
    private final OnItemClickListener listener;

    public ActAdapter(List<Act> lista, OnItemClickListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    public void setActs(List<Act> nuevaLista) {
        this.lista = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ActViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.action_card, parent, false);
        return new ActViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActViewHolder holder, int position) {
        Act act = lista.get(position);

        holder.txtTitulo.setText(act.getTitulo());
        holder.txtUbicacion.setText(act.getUbicacion());

        // imagen desde URL (Supabase)
        Glide.with(holder.itemView.getContext())
                .load(act.getImagenUrl())
                .placeholder(R.drawable.arte)
                .centerCrop()
                .into(holder.imgAccion);

        holder.itemView.setOnClickListener(v ->
                listener.onItemClick(act)
        );
    }

    @Override
    public int getItemCount() {
        return lista == null ? 0 : lista.size();
    }

    static class ActViewHolder extends RecyclerView.ViewHolder {

        ImageView imgAccion;
        TextView txtTitulo, txtUbicacion;

        public ActViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAccion = itemView.findViewById(R.id.imgAccion);
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtUbicacion = itemView.findViewById(R.id.txtUbicacion);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Act act);
    }
}
