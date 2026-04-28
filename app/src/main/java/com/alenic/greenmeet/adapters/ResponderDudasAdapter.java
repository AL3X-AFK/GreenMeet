package com.alenic.greenmeet.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alenic.greenmeet.R;
import com.alenic.greenmeet.data.Duda;

import java.util.ArrayList;
import java.util.List;

public class ResponderDudasAdapter extends RecyclerView.Adapter<ResponderDudasAdapter.ViewHolder> {

    private List<Duda> dudas = new ArrayList<>();
    private final OnDudaClickListener listener;

    public interface OnDudaClickListener {
        void onDudaClick(Duda duda);
    }

    public ResponderDudasAdapter(OnDudaClickListener listener) {
        this.listener = listener;
    }

    public void setDudas(List<Duda> dudas) {
        this.dudas = dudas;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_responder_duda, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Duda duda = dudas.get(position);

        if (!duda.isRespondida()) {
            // Eres el creador y tienes que responder
            holder.tvInfoActividad.setText("🔔 Duda en: " + duda.getTituloActividad());
            holder.tvUsuarioPregunta.setText("De: " + duda.getNombreAutor());
            holder.tvPreguntaTexto.setText(duda.getPregunta());
            holder.tvAccionDuda.setText("PULSA PARA RESPONDER");
        } else {
            // Eres el participante y te han respondido
            holder.tvInfoActividad.setText("🔔 Te han respondido en: " + duda.getTituloActividad());
            holder.tvUsuarioPregunta.setText("Organizador");
            holder.tvPreguntaTexto.setText(duda.getRespuesta()); // Mostramos un adelanto de la respuesta
            holder.tvAccionDuda.setText("PULSA PARA VER");
        }

        holder.itemView.setOnClickListener(v -> listener.onDudaClick(duda));
    }

    @Override
    public int getItemCount() { return dudas.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvInfoActividad, tvUsuarioPregunta, tvPreguntaTexto,tvAccionDuda;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInfoActividad = itemView.findViewById(R.id.tvInfoActividad);
            tvUsuarioPregunta = itemView.findViewById(R.id.tvUsuarioPregunta);
            tvPreguntaTexto = itemView.findViewById(R.id.tvPreguntaTexto);
            tvAccionDuda = itemView.findViewById(R.id.tvAccionDuda);
        }
    }
}