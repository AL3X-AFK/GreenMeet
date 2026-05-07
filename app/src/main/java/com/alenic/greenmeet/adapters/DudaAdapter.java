package com.alenic.greenmeet.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import com.alenic.greenmeet.R;
import com.alenic.greenmeet.data.Duda;
import com.bumptech.glide.Glide;

import android.text.format.DateUtils;

public class DudaAdapter extends RecyclerView.Adapter<DudaAdapter.DudaViewHolder> {

    private List<Duda> listaDudas = new ArrayList<>();

    public void setDudas(List<Duda> dudas) {
        this.listaDudas = dudas;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DudaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_duda, parent, false);
        return new DudaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DudaViewHolder holder, int position) {
        Duda duda = listaDudas.get(position);

        holder.itemAutor.setText(duda.getNombreAutor());
        holder.itemPregunta.setText(duda.getPregunta());

        Glide.with(holder.itemView.getContext())
                .load(duda.getUrlFotoAutor())
                .placeholder(R.drawable.profile_icon) // Imagen mientras carga
                .error(R.drawable.profile_icon)       // Imagen si falla o no tiene
                .circleCrop()                        // Recorte circular
                .into(holder.imgAutorDuda);

        long ahora = System.currentTimeMillis();
        long tiempoDuda = duda.getFechaCreacion();

        CharSequence tiempoRelativo = DateUtils.getRelativeTimeSpanString(
                tiempoDuda,
                ahora,
                DateUtils.MINUTE_IN_MILLIS
        );
        holder.itemFecha.setText(tiempoRelativo);

        //Si no está respondida, escondemos el layout de respuesta
        if (duda.isRespondida()) {
            holder.layoutRespuesta.setVisibility(View.VISIBLE);
            holder.itemRespuesta.setText(duda.getRespuesta());
        } else {
            holder.layoutRespuesta.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return listaDudas.size();
    }

    public static class DudaViewHolder extends RecyclerView.ViewHolder {
        TextView itemAutor, itemPregunta, itemRespuesta,itemFecha;
        LinearLayout layoutRespuesta;
        ImageView imgAutorDuda;

        public DudaViewHolder(@NonNull View itemView) {
            super(itemView);
            itemAutor = itemView.findViewById(R.id.itemAutor);
            itemPregunta = itemView.findViewById(R.id.itemPregunta);
            itemRespuesta = itemView.findViewById(R.id.itemRespuesta);
            itemFecha = itemView.findViewById(R.id.itemFecha);
            layoutRespuesta = itemView.findViewById(R.id.layoutRespuesta);
            imgAutorDuda = itemView.findViewById(R.id.imgAutorDuda);
        }
    }
}