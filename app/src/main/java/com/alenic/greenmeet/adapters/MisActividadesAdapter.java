package com.alenic.greenmeet.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.alenic.greenmeet.R;
import com.alenic.greenmeet.data.Act;
import com.alenic.greenmeet.utils.Utils;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import android.widget.TextView;

public class MisActividadesAdapter extends ListAdapter<Act, MisActividadesAdapter.ViewHolder> {

    private final OnItemClickListener listener;

    public MisActividadesAdapter(OnItemClickListener listener) {
        super(DIFF_CALLBACK);
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.myacts_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgEvento;
        TextView txtTitulo, txtFecha;
        Button btnVerMas;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgEvento = itemView.findViewById(R.id.imgEvento);
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtFecha = itemView.findViewById(R.id.txtFecha);
            btnVerMas = itemView.findViewById(R.id.btnVerMas);

            btnVerMas.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(position));
                }
            });
        }

        void bind(Act act) {
            txtTitulo.setText(act.getTitulo());
            txtFecha.setText(Utils.formatDate(act.getFecha()));

            Glide.with(itemView.getContext())
                    .load(act.getImagenUrl())
                    .centerCrop()
                    .placeholder(R.drawable.arte)
                    .into(imgEvento);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Act act);
    }
}
