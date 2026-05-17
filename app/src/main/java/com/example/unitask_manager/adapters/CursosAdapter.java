package com.example.unitask_manager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unitask_manager.R;
import com.example.unitask_manager.models.Curso;

import java.util.List;

public class CursosAdapter extends RecyclerView.Adapter<CursosAdapter.ViewHolder> {

    private List<Curso> cursos;
    private OnCursoClickListener listener;

    public interface OnCursoClickListener {
        void onCursoClick(Curso curso);
    }

    public CursosAdapter(List<Curso> cursos, OnCursoClickListener listener) {
        this.cursos = cursos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_curso, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(cursos.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return cursos != null ? cursos.size() : 0;
    }

    public void actualizarDatos(List<Curso> nuevosCursos) {
        this.cursos = nuevosCursos;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardRoot;
        private View viewColorBar;
        private TextView tvNombre;
        private TextView tvHorario;
        private TextView tvPendientes;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardRoot = itemView.findViewById(R.id.card_curso_root);
            viewColorBar = itemView.findViewById(R.id.view_color_bar);
            tvNombre = itemView.findViewById(R.id.tv_curso_nombre);
            tvHorario = itemView.findViewById(R.id.tv_curso_horario);
            tvPendientes = itemView.findViewById(R.id.tv_curso_pendientes);
        }

        void bind(final Curso curso, final OnCursoClickListener listener) {
            tvNombre.setText(curso.getNombre());
            String horario = curso.getHorario();
            if (horario != null && !horario.isEmpty()) {
                tvHorario.setText(horario);
                tvHorario.setVisibility(View.VISIBLE);
            } else {
                tvHorario.setVisibility(View.GONE);
            }
            tvPendientes.setText(curso.getPendientes() + " pendientes");

            try {
                viewColorBar.setBackgroundColor(
                        android.graphics.Color.parseColor(curso.getColor()));
            } catch (Exception e) {
                viewColorBar.setBackgroundColor(
                        ContextCompat.getColor(itemView.getContext(), R.color.primary_purple));
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onCursoClick(curso);
            });
        }
    }
}
