package com.example.unitask_manager.adapters;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unitask_manager.R;
import com.example.unitask_manager.models.Actividad;

import java.util.List;

public class ActividadesAdapter extends RecyclerView.Adapter<ActividadesAdapter.ViewHolder> {

    private List<Actividad> actividades;

    public ActividadesAdapter(List<Actividad> actividades, OnItemClickListener listener) {
        this.actividades = actividades;
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
        holder.bind(actividad);
    }

    @Override
    public int getItemCount() {
        return actividades != null ? actividades.size() : 0;
    }

    public void actualizarDatos(List<Actividad> nuevasActividades) {
        this.actividades = nuevasActividades;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(Actividad actividad);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardRoot;
        private View viewPriorityBar;
        private FrameLayout iconContainer;
        private ImageView ivIcono;
        private TextView tvTitulo;
        private TextView tvFecha;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardRoot = itemView.findViewById(R.id.card_actividad_root);
            viewPriorityBar = itemView.findViewById(R.id.view_priority_bar);
            iconContainer = itemView.findViewById(R.id.icon_container);
            ivIcono = itemView.findViewById(R.id.iv_actividad_icono);
            tvTitulo = itemView.findViewById(R.id.tv_actividad_titulo);
            tvFecha = itemView.findViewById(R.id.tv_actividad_fecha);
        }

        void bind(Actividad actividad) {
            int priority = actividad.getPrioridad();

            int priorityColor = getPriorityColor(priority);
            int priorityBgColor = getPriorityBgColor(priority);
            int priorityIconBg = getPriorityIconBg(priority);
            int typeIconBg = getTypeIconBg(actividad.getTipo());

            viewPriorityBar.setBackgroundColor(
                    ContextCompat.getColor(itemView.getContext(), priorityColor));

            GradientDrawable iconBg = (GradientDrawable) iconContainer.getBackground();
            iconBg.setColor(ContextCompat.getColor(itemView.getContext(), typeIconBg));

            cardRoot.setCardBackgroundColor(
                    ContextCompat.getColor(itemView.getContext(), priorityBgColor));

            tvTitulo.setText(actividad.getTitulo());

            String hora = actividad.getHora();
            if (hora != null && !hora.isEmpty()) {
                tvFecha.setText(hora);
                tvFecha.setVisibility(View.VISIBLE);
            } else {
                tvFecha.setVisibility(View.GONE);
            }

            ivIcono.setImageResource(actividad.getIconoResId());
        }

        private int getPriorityColor(int priority) {
            switch (priority) {
                case Actividad.PRIORIDAD_ALTA: return R.color.priority_high;
                case Actividad.PRIORIDAD_MEDIA: return R.color.priority_medium;
                default: return R.color.priority_low;
            }
        }

        private int getPriorityBgColor(int priority) {
            switch (priority) {
                case Actividad.PRIORIDAD_ALTA: return R.color.priority_high_bg;
                case Actividad.PRIORIDAD_MEDIA: return R.color.priority_medium_bg;
                default: return R.color.priority_low_bg;
            }
        }

        private int getPriorityIconBg(int priority) {
            switch (priority) {
                case Actividad.PRIORIDAD_ALTA: return R.color.priority_high_icon_bg;
                case Actividad.PRIORIDAD_MEDIA: return R.color.priority_medium_icon_bg;
                default: return R.color.priority_low_icon_bg;
            }
        }

        private int getTypeIconBg(String tipo) {
            if (tipo == null) return R.color.priority_low_icon_bg;
            switch (tipo.toLowerCase()) {
                case "examen":
                    return R.color.card_type_exam_bg;
                case "exposición":
                case "exposicion":
                    return R.color.card_type_presentation_bg;
                default:
                    return R.color.card_type_task_bg;
            }
        }
    }
}
