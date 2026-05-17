package com.example.unitask_manager.fragments;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unitask_manager.R;
import com.example.unitask_manager.activities.MainActivity;
import com.example.unitask_manager.adapters.CursosAdapter;
import com.example.unitask_manager.models.Curso;
import com.example.unitask_manager.utils.Constants;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class CursosFragment extends Fragment {

    private RecyclerView rvCursos;
    private LinearLayout layoutEmpty;
    private CursosAdapter adapter;

    private List<Curso> listaCursos;
    private long nextId = 100;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cursos, container, false);
        initViews(view);
        configurarRecycler();
        cargarDatosEjemplo();
        configurarListeners(view);
        return view;
    }

    private void initViews(View view) {
        rvCursos = view.findViewById(R.id.rv_cursos);
        layoutEmpty = view.findViewById(R.id.layout_empty);
    }

    private void configurarRecycler() {
        listaCursos = new ArrayList<>();
        rvCursos.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCursos.setNestedScrollingEnabled(false);
        adapter = new CursosAdapter(listaCursos, curso -> {
            Bundle args = new Bundle();
            args.putLong("curso_id", curso.getId());
            args.putString("curso_nombre", curso.getNombre());
            args.putString("curso_profesor", curso.getProfesor());
            args.putString("curso_color", curso.getColor());
            args.putInt("curso_pendientes", curso.getPendientes());
            args.putString("curso_horario", curso.getHorario());
            args.putInt("curso_progreso", curso.getProgreso());
            args.putString("curso_descripcion", curso.getDescripcion());
            args.putInt("curso_icon", curso.getIconResId());
            DetalleCursoFragment detalle = new DetalleCursoFragment();
            detalle.setArguments(args);
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).cargarFragmento(detalle, true);
            }
        });
        rvCursos.setAdapter(adapter);

        requireActivity().getSupportFragmentManager().setFragmentResultListener("curso_delete", this, (requestKey, bundle) -> {
            long deletedId = bundle.getLong("curso_id");
            for (int i = 0; i < listaCursos.size(); i++) {
                if (listaCursos.get(i).getId() == deletedId) {
                    listaCursos.remove(i);
                    break;
                }
            }
            adapter.notifyDataSetChanged();
            actualizarEmptyState();
        });
    }

    private void cargarDatosEjemplo() {
        Curso c1 = new Curso(1, "Programación II", "Ing. López", "#7C3AED", 4, "Lun y Mié 10:00-12:00", 45, "Programación orientada a objetos en Java. Se cubren herencia, polimorfismo, interfaces y manejo de excepciones.");
        c1.setIconResId(R.drawable.ic_tarea);
        listaCursos.add(c1);
        Curso c2 = new Curso(2, "Cálculo Diferencial", "Ing. Martínez", "#3B82F6", 2, "Mar y Jue 08:00-10:00", 60, "Estudio de límites, derivadas y sus aplicaciones en problemas de ingeniería.");
        c2.setIconResId(R.drawable.ic_stats);
        listaCursos.add(c2);
        Curso c3 = new Curso(3, "Física Mecánica", "Ing. García", "#10B981", 3, "Lun y Mié 14:00-16:00", 30, "Principios de mecánica clásica: cinemática, dinámica, trabajo y energía.");
        c3.setIconResId(R.drawable.ic_urgent);
        listaCursos.add(c3);
        Curso c4 = new Curso(4, "Base de Datos", "Ing. Rodríguez", "#F59E0B", 5, "Mar y Jue 14:00-16:00", 20, "Diseño de bases de datos relacionales, modelado ER, SQL y normalización.");
        c4.setIconResId(R.drawable.ic_agenda);
        listaCursos.add(c4);
        Curso c5 = new Curso(5, "Inglés Técnico", "Lic. Hernández", "#EC4899", 1, "Vie 10:00-12:00", 70, "Vocabulario técnico para ingeniería, redacción de informes y presentaciones.");
        c5.setIconResId(R.drawable.ic_exposicion);
        listaCursos.add(c5);
        nextId = 6;
        adapter.notifyDataSetChanged();
        actualizarEmptyState();
    }

    private void actualizarEmptyState() {
        if (listaCursos.isEmpty()) {
            rvCursos.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            rvCursos.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }

    private void configurarListeners(View view) {
        view.findViewById(R.id.btn_add_curso_header).setOnClickListener(v -> mostrarDialogoAgregarCurso());
    }

    private void mostrarDialogoAgregarCurso() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_curso, null);
        TextInputEditText etNombre = dialogView.findViewById(R.id.et_curso_nombre);
        TextInputEditText etHorario = dialogView.findViewById(R.id.et_curso_horario);
        TextInputEditText etDocente = dialogView.findViewById(R.id.et_curso_docente);
        TextInputEditText etDescripcion = dialogView.findViewById(R.id.et_curso_descripcion);
        LinearLayout layoutColores = dialogView.findViewById(R.id.layout_colores);

        final String[] colorSeleccionado = {Constants.COLORES_CURSO[0]};

        int size = getResources().getDimensionPixelSize(R.dimen.icon_lg);
        int margin = getResources().getDimensionPixelSize(R.dimen.spacing_sm);

        for (int i = 0; i < Constants.COLORES_CURSO.length; i++) {
            ImageView circle = new ImageView(requireContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.setMargins(margin, 0, margin, 0);
            circle.setLayoutParams(params);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.OVAL);
            drawable.setColor(android.graphics.Color.parseColor(Constants.COLORES_CURSO[i]));
            if (i == 0) drawable.setStroke(4, android.graphics.Color.WHITE);
            circle.setBackground(drawable);
            circle.setElevation(i == 0 ? 6f : 2f);
            circle.setContentDescription("Color " + i);

            final int index = i;
            circle.setOnClickListener(v -> {
                colorSeleccionado[0] = Constants.COLORES_CURSO[index];
                for (int j = 0; j < layoutColores.getChildCount(); j++) {
                    View child = layoutColores.getChildAt(j);
                    GradientDrawable d = (GradientDrawable) child.getBackground();
                    if (j == index) {
                        d.setStroke(4, android.graphics.Color.WHITE);
                        child.setElevation(6f);
                    } else {
                        d.setStroke(0, android.graphics.Color.TRANSPARENT);
                        child.setElevation(2f);
                    }
                }
            });

            layoutColores.addView(circle);
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Nuevo Curso")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nombre = etNombre.getText().toString().trim();
                    if (nombre.isEmpty()) {
                        Toast.makeText(getContext(), "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String docente = etDocente.getText().toString().trim();
                    if (docente.isEmpty()) {
                        Toast.makeText(getContext(), "El docente es obligatorio", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String horario = etHorario.getText().toString().trim();
                    String descripcion = etDescripcion.getText().toString().trim();
                    long id = nextId++;
                    listaCursos.add(new Curso(id, nombre, docente, colorSeleccionado[0], 0, horario, 0, descripcion));
                    adapter.notifyDataSetChanged();
                    actualizarEmptyState();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

}
