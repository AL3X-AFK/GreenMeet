package com.alenic.greenmeet.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alenic.greenmeet.R;
import com.alenic.greenmeet.data.Act;
import com.alenic.greenmeet.utils.Utils;
import com.bumptech.glide.Glide;

import java.util.List;

public class GuardadosAdapter extends RecyclerView.Adapter<GuardadosAdapter.ViewHolder> {

    private List<Act> lista;
    private final OnItemClickListener listener;

    // Constructor
    public GuardadosAdapter(List<Act> lista, OnItemClickListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    // Actualiza la lista de actividades y refresca el RecyclerView.
    public void setActs(List<Act> nuevaLista) {
        this.lista = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflamos el layout de la tarjeta individual
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tarjeta_guardada, parent, false); // usar tarejta_guardada.xml
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Act act = lista.get(position);

        holder.txtTitulo.setText(act.getTitulo());

        holder.txtFecha.setText(Utils.formatDate(act.getFecha()));
        holder.txtDescripcion.setText(act.getDescripcion());

        // Cargar imagen con Glide
        Glide.with(holder.itemView.getContext())
                .load(act.getImagenUrl())
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .into(holder.imgEvento);

        // Botón "Ver más"
        holder.btnVerMas.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Ver más: " + act.getTitulo(), Toast.LENGTH_SHORT).show();
            listener.onItemClick(act); // notificar click
        });
    }

    @Override
    public int getItemCount() {
        return lista == null ? 0 : lista.size();
    }

    //ViewHolder
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgEvento;
        TextView txtTitulo, txtFecha, txtDescripcion;
        Button btnVerMas;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Referencias a los elementos del layout de la tarjeta
            imgEvento = itemView.findViewById(R.id.imgEvento);
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtFecha = itemView.findViewById(R.id.txtFecha);
            txtDescripcion = itemView.findViewById(R.id.txtDescripcion);
            btnVerMas = itemView.findViewById(R.id.btnVerMas);
        }
    }

    //  Interfaz de click
    public interface OnItemClickListener {
        void onItemClick(Act act);
    }
}
