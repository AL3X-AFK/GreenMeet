package com.alenic.greenmeet.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.alenic.greenmeet.R;
import com.alenic.greenmeet.data.Act;
import com.bumptech.glide.Glide;

import java.util.List;

public class ActAdapter extends ListAdapter<Act, ActAdapter.ActViewHolder> {


    private final OnItemClickListener listener;
    private final int itemLayout;

    public ActAdapter(int itemLayout, OnItemClickListener listener) {
        super(DIFF_CALLBACK);
        this.itemLayout = itemLayout;
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Act> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Act>() {

                @Override
                public boolean areItemsTheSame(@NonNull Act oldItem, @NonNull Act newItem) {
                    return oldItem.getTitulo().equals(newItem.getTitulo());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Act oldItem, @NonNull Act newItem) {
                    return oldItem.equals(newItem);
                }
            };

    @NonNull
    @Override
    public ActViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(itemLayout, parent, false);
        return new ActViewHolder(view,itemLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull ActViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ActViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgAccion;
        private TextView txtTitulo;
        private TextView txtUbicacion;
        private TextView txtFecha;
        private TextView txtDescripcion;
        private Button btnVerMas;

        public ActViewHolder(@NonNull View itemView, int layout) {
            super(itemView);

            if (layout == R.layout.act_card) {
                imgAccion = itemView.findViewById(R.id.imgAccion);
                txtTitulo = itemView.findViewById(R.id.txtTitulo);
                txtUbicacion = itemView.findViewById(R.id.txtUbicacion);

                // Click en toda la tarjeta
                itemView.setOnClickListener(v -> {
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(getItem(position));
                    }
                });

            } else if (layout == R.layout.tarjeta_guardada) {
                imgAccion = itemView.findViewById(R.id.imgEvento);
                txtTitulo = itemView.findViewById(R.id.txtTitulo);
                txtFecha = itemView.findViewById(R.id.txtFecha);
                txtDescripcion = itemView.findViewById(R.id.txtDescripcion);
                btnVerMas = itemView.findViewById(R.id.btnVerMas);

                // Click solo en el botón
                if (btnVerMas != null) {
                    btnVerMas.setOnClickListener(v -> {
                        int position = getBindingAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(getItem(position));
                        }
                    });
                }
            }
        }

        void bind(Act act) {
            if (txtTitulo != null) txtTitulo.setText(act.getTitulo());
            if (txtUbicacion != null) txtUbicacion.setText(act.getUbicacion());
            if (txtFecha != null) txtFecha.setText(act.getFecha());
            if (txtDescripcion != null) txtDescripcion.setText(act.getDescripcion());

            if (imgAccion != null) {
                Glide.with(itemView.getContext())
                        .load(act.getImagenUrl())
                        .centerCrop()
                        .placeholder(R.drawable.arte)
                        .into(imgAccion);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Act act);
    }
}