package com.example.unitask_manager.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unitask_manager.R;
import com.example.unitask_manager.models.Curso;

import java.util.List;

public class CursosAdapter extends RecyclerView.Adapter<CursosAdapter.ViewHolder> {

    private List<Curso> cursos;
    private final OnCursoClickListener clickListener;
    private final OnCursoDeleteListener deleteListener;

    public interface OnCursoClickListener {
        void onCursoClick(Curso curso);
    }

    public interface OnCursoDeleteListener {
        void onCursoDelete(Curso curso);
    }

    public CursosAdapter(List<Curso> cursos, OnCursoClickListener clickListener, OnCursoDeleteListener deleteListener) {
        this.cursos = cursos;
        this.clickListener = clickListener;
        this.deleteListener = deleteListener;
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
        Curso curso = cursos.get(position);
        holder.bind(curso, clickListener, deleteListener);
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

        private View viewColorBar;
        private TextView tvNombre;
        private TextView tvProfesor;
        private TextView tvActividadesCount;
        private ImageView ivEliminar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            viewColorBar = itemView.findViewById(R.id.view_color_bar);
            tvNombre = itemView.findViewById(R.id.tv_curso_nombre);
            tvProfesor = itemView.findViewById(R.id.tv_curso_profesor);
            tvActividadesCount = itemView.findViewById(R.id.tv_curso_actividades_count);
            ivEliminar = itemView.findViewById(R.id.iv_curso_eliminar);
        }

        void bind(final Curso curso, final OnCursoClickListener clickListener,
                  final OnCursoDeleteListener deleteListener) {
            tvNombre.setText(curso.getNombre());
            if (curso.getProfesor() != null && !curso.getProfesor().isEmpty()) {
                tvProfesor.setVisibility(View.VISIBLE);
                tvProfesor.setText(curso.getProfesor());
            } else {
                tvProfesor.setVisibility(View.GONE);
            }

            tvActividadesCount.setText(curso.getPendientes() + " pendiente" + (curso.getPendientes() != 1 ? "s" : ""));

            try {
                viewColorBar.setBackgroundColor(Color.parseColor(curso.getColor()));
            } catch (Exception e) {
                viewColorBar.setBackgroundColor(Color.parseColor("#7C3AED"));
            }

            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onCursoClick(curso);
                }
            });

            ivEliminar.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onCursoDelete(curso);
                }
            });
        }
    }
}
