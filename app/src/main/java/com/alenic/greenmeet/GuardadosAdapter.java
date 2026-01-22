package com.alenic.greenmeet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GuardadosAdapter extends RecyclerView.Adapter<GuardadosAdapter.ViewHolder> {

    private List<String> lista;

    public GuardadosAdapter(List<String> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tarjeta_guardada, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String titulo = lista.get(position);
        holder.txtTitulo.setText(titulo);

        // Datos de ejemplo
        holder.txtFecha.setText("14 Feb - 18 Feb");
        holder.imgEvento.setImageResource(R.drawable.slidebg2);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgEvento;
        TextView txtTitulo, txtFecha, txtVerMas;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgEvento = itemView.findViewById(R.id.imgEvento);
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtFecha = itemView.findViewById(R.id.txtFecha);
            txtVerMas = itemView.findViewById(R.id.txtVerMas);
        }
    }
}
