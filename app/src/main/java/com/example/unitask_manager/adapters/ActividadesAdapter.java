package com.example.unitask_manager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unitask_manager.R;
import com.example.unitask_manager.models.Actividad;

import java.util.List;

public class ActividadesAdapter extends RecyclerView.Adapter<ActividadesAdapter.ViewHolder> {

    private List<Actividad> actividades;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Actividad actividad);
    }

    public ActividadesAdapter(List<Actividad> actividades, OnItemClickListener listener) {
        this.actividades = actividades;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_actividad, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Actividad actividad = actividades.get(position);
        holder.bind(actividad, listener);
    }

    @Override
    public int getItemCount() {
        return actividades != null ? actividades.size() : 0;
    }

    public void actualizarDatos(List<Actividad> nuevasActividades) {
        this.actividades = nuevasActividades;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardRoot;
        private ImageView ivIcono;
        private TextView tvTitulo;
        private TextView tvFecha;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardRoot = itemView.findViewById(R.id.card_actividad_root);
            ivIcono = itemView.findViewById(R.id.iv_actividad_icono);
            tvTitulo = itemView.findViewById(R.id.tv_actividad_titulo);
            tvFecha = itemView.findViewById(R.id.tv_actividad_fecha);
        }

        void bind(final Actividad actividad, final OnItemClickListener listener) {
            tvTitulo.setText(actividad.getTitulo());
            tvFecha.setText(actividad.getFecha() + " · " + actividad.getHora());
            ivIcono.setImageResource(actividad.getIconoResId());
            cardRoot.setCardBackgroundColor(itemView.getContext().getColor(actividad.getColorFondoPrioridad()));

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(actividad);
                }
            });
        }
    }
}
