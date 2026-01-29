package com.alenic.greenmeet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alenic.greenmeet.objects.Accion;

import java.util.List;

public class AccionAdapter extends RecyclerView.Adapter<AccionAdapter.AccionViewHolder> {

    private List<Accion> lista;
    private OnItemClickListener listener;
    public AccionAdapter(List<Accion> lista, OnItemClickListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AccionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.action_card, parent, false);
        return new AccionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccionViewHolder holder, int position) {
        Accion accion = lista.get(position);
        holder.txtTitulo.setText(accion.titulo);
        holder.txtUbicacion.setText(accion.ubicacion);
        holder.imgAccion.setImageResource(accion.imagen);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(accion));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class AccionViewHolder extends RecyclerView.ViewHolder {

        ImageView imgAccion;
        TextView txtTitulo, txtUbicacion;

        public AccionViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAccion = itemView.findViewById(R.id.imgAccion);
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtUbicacion = itemView.findViewById(R.id.txtUbicacion);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Accion accion);
    }
}
