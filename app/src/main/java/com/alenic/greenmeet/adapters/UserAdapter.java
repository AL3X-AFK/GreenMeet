package com.alenic.greenmeet.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alenic.greenmeet.R;
import com.alenic.greenmeet.data.User;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> users = new ArrayList<>();

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.tvName.setText(user.getNombre());

        // CARGAR IMAGEN DEL PARTICIPANTE
        // Si tiene URL la carga, si no, pone el icono por defecto
        if (user.getImagenProfileURL() != null && !user.getImagenProfileURL().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(user.getImagenProfileURL())
                    .placeholder(R.drawable.profile_icon)
                    .error(R.drawable.profile_icon)
                    .circleCrop() // Esto lo hace redondo por código
                    .into(holder.ivPhoto);
        } else {
            holder.ivPhoto.setImageResource(R.drawable.profile_icon);
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView ivPhoto;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvUserName);
            ivPhoto = itemView.findViewById(R.id.ivUserPhoto);
        }
    }
}