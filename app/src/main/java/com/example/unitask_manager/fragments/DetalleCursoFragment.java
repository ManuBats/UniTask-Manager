package com.example.unitask_manager.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unitask_manager.R;
import com.example.unitask_manager.adapters.ActividadesAdapter;
import com.example.unitask_manager.models.Actividad;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class DetalleCursoFragment extends Fragment {

    private TextView tvNombre, tvProfesor, tvPendientes;
    private TextView tvHorario, tvProgresoTexto, tvDescripcion;
    private ImageView ivIcono;
    private ProgressBar progressBar;
    private View viewColor;
    private View btnDelete, btnBack;
    private RecyclerView rvActividades;
    private FloatingActionButton fabAdd;

    private List<Actividad> listaActividades;
    private ActividadesAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detalle_curso, container, false);
        initViews(view);
        cargarInfoCurso();
        configurarRecycler();
        cargarActividadesEjemplo();
        configurarListeners(view);
        return view;
    }

    private void initViews(View view) {
        tvNombre = view.findViewById(R.id.tv_detalle_nombre);
        tvProfesor = view.findViewById(R.id.tv_detalle_profesor);
        tvPendientes = view.findViewById(R.id.tv_detalle_pendientes);
        tvHorario = view.findViewById(R.id.tv_detalle_horario);
        tvProgresoTexto = view.findViewById(R.id.tv_detalle_progreso_texto);
        progressBar = view.findViewById(R.id.progress_detalle_curso);
        tvDescripcion = view.findViewById(R.id.tv_detalle_descripcion);
        ivIcono = view.findViewById(R.id.iv_detalle_icono);
        viewColor = view.findViewById(R.id.view_header_color);
        btnDelete = view.findViewById(R.id.btn_delete_curso_detail);
        btnBack = view.findViewById(R.id.btn_back_curso);
        rvActividades = view.findViewById(R.id.rv_detalle_actividades);
        fabAdd = view.findViewById(R.id.fab_add_actividad);
    }

    private void cargarInfoCurso() {
        Bundle args = getArguments();
        if (args != null) {
            tvNombre.setText(args.getString("curso_nombre", "Curso"));
            String profesor = args.getString("curso_profesor", "");
            if (!profesor.isEmpty()) {
                tvProfesor.setText(profesor);
                tvProfesor.setVisibility(View.VISIBLE);
            } else {
                tvProfesor.setVisibility(View.GONE);
            }
            int pendientes = args.getInt("curso_pendientes", 0);
            tvPendientes.setText(pendientes + " actividad" + (pendientes != 1 ? "es" : "") + " pendiente" + (pendientes != 1 ? "s" : ""));

            String horario = args.getString("curso_horario", "");
            if (!horario.isEmpty()) {
                tvHorario.setText(horario);
                tvHorario.setVisibility(View.VISIBLE);
            } else {
                tvHorario.setVisibility(View.GONE);
            }

            int progreso = args.getInt("curso_progreso", 0);
            progressBar.setProgress(progreso);
            tvProgresoTexto.setText(progreso + "%");

            String descripcion = args.getString("curso_descripcion", "");
            if (!descripcion.isEmpty()) {
                tvDescripcion.setText(descripcion);
                tvDescripcion.setVisibility(View.VISIBLE);
            } else {
                tvDescripcion.setVisibility(View.GONE);
            }

            int iconResId = args.getInt("curso_icon", 0);
            if (iconResId != 0) {
                ivIcono.setImageResource(iconResId);
            }

            try {
                viewColor.setBackgroundColor(
                        android.graphics.Color.parseColor(args.getString("curso_color", "#7C3AED")));
            } catch (Exception e) {
                viewColor.setBackgroundColor(
                        ContextCompat.getColor(requireContext(), R.color.primary_purple));
            }
        }
    }

    private void configurarRecycler() {
        listaActividades = new ArrayList<>();
        rvActividades.setLayoutManager(new LinearLayoutManager(getContext()));
        rvActividades.setNestedScrollingEnabled(false);
        adapter = new ActividadesAdapter(listaActividades, actividad ->
                Toast.makeText(getContext(), actividad.getTitulo(), Toast.LENGTH_SHORT).show());
        rvActividades.setAdapter(adapter);
    }

    private void cargarActividadesEjemplo() {
        long cursoId = getArguments() != null ? getArguments().getLong("curso_id", 0) : 0;
        listaActividades.addAll(obtenerActividadesPorCurso(cursoId));
        adapter.notifyDataSetChanged();
    }

    private void configurarListeners(View view) {
        fabAdd.setOnClickListener(v ->
                Toast.makeText(getContext(), "Agregar actividad — Próximamente", Toast.LENGTH_SHORT).show());

        btnBack.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());

        btnDelete.setOnClickListener(v -> {
            String nombreCurso = getArguments() != null ? getArguments().getString("curso_nombre", "este curso") : "este curso";
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Eliminar curso")
                    .setMessage("¿Eliminar \"" + nombreCurso + "\" y sus actividades?")
                    .setPositiveButton("Eliminar", (dialog, which) -> {
                        long cursoId = getArguments() != null ? getArguments().getLong("curso_id", 0) : 0;
                        Bundle result = new Bundle();
                        result.putLong("curso_id", cursoId);
                        requireActivity().getSupportFragmentManager().setFragmentResult("curso_delete", result);
                        requireActivity().getSupportFragmentManager().popBackStack();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    private List<Actividad> obtenerActividadesPorCurso(long cursoId) {
        List<Actividad> lista = new ArrayList<>();
        int idCurso = (int) cursoId;
        switch (idCurso) {
            case 1:
                lista.add(new Actividad("Entregar proyecto final", "Tarea", "18 mayo 2026", "11:59 PM", Actividad.PRIORIDAD_ALTA, "Entrega del proyecto integrador", 1));
                lista.add(new Actividad("Examen parcial", "Examen", "20 mayo 2026", "10:00 AM", Actividad.PRIORIDAD_ALTA, "Temas: POO, herencia, polimorfismo", 1));
                lista.add(new Actividad("Ejercicios de arrays", "Tarea", "22 mayo 2026", "11:59 PM", Actividad.PRIORIDAD_MEDIA, "Arrays multidimensionales", 1));
                lista.add(new Actividad("Práctica de POO", "Tarea", "25 mayo 2026", "11:59 PM", Actividad.PRIORIDAD_MEDIA, "Diagrama de clases", 1));
                break;
            case 2:
                lista.add(new Actividad("Derivadas parciales", "Tarea", "19 mayo 2026", "11:59 PM", Actividad.PRIORIDAD_ALTA, "Ejercicios del capítulo 14", 2));
                lista.add(new Actividad("Tarea de integrales", "Tarea", "23 mayo 2026", "11:59 PM", Actividad.PRIORIDAD_MEDIA, "Integrales definidas", 2));
                break;
            case 3:
                lista.add(new Actividad("Laboratorio de péndulo", "Tarea", "17 mayo 2026", "8:00 AM", Actividad.PRIORIDAD_ALTA, "Reporte de laboratorio", 3));
                lista.add(new Actividad("Problemas de cinemática", "Tarea", "21 mayo 2026", "11:59 PM", Actividad.PRIORIDAD_MEDIA, "Ejercicios MRU y MRUV", 3));
                lista.add(new Actividad("Examen unidad 3", "Examen", "28 mayo 2026", "10:00 AM", Actividad.PRIORIDAD_ALTA, "Dinámica y energía", 3));
                break;
            case 4:
                lista.add(new Actividad("Diagrama ER", "Tarea", "16 mayo 2026", "11:59 PM", Actividad.PRIORIDAD_ALTA, "Diagrama entidad-relación del proyecto", 4));
                lista.add(new Actividad("Consultas SQL", "Tarea", "18 mayo 2026", "11:59 PM", Actividad.PRIORIDAD_MEDIA, "Consultas con JOIN", 4));
                lista.add(new Actividad("Normalización", "Tarea", "24 mayo 2026", "11:59 PM", Actividad.PRIORIDAD_MEDIA, "Formas normales 1FN-3FN", 4));
                lista.add(new Actividad("Proyecto BD", "Proyecto", "30 mayo 2026", "11:59 PM", Actividad.PRIORIDAD_ALTA, "Base de datos completa", 4));
                break;
            case 5:
                lista.add(new Actividad("Ensayo final", "Tarea", "26 mayo 2026", "11:59 PM", Actividad.PRIORIDAD_MEDIA, "Ensayo sobre tecnología", 5));
                break;
            default:
                lista.add(new Actividad("No hay actividades para este curso", "Tarea", "—", "", Actividad.PRIORIDAD_BAJA, "", cursoId));
        }
        return lista;
    }
}
