package com.example.unitask_manager.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unitask_manager.R;
import com.example.unitask_manager.activities.MainActivity;
import com.example.unitask_manager.adapters.CursosAdapter;
import com.example.unitask_manager.data.local.TokenManager;
import com.example.unitask_manager.data.repository.CursoRepository;
import com.example.unitask_manager.models.Curso;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class CursosFragment extends Fragment {

    private RecyclerView rvCursos;
    private TextView tvSinCursos, tvNumCursos;
    private FloatingActionButton fabAdd;

    private CursosAdapter adapter;
    private final List<Curso> cursosList = new ArrayList<>();
    private CursoRepository cursoRepository;

    private final String[] coloresDisponibles = {
            "#7C3AED", "#3B82F6", "#10B981", "#F59E0B", "#EF4444", "#EC4899",
            "#8B5CF6", "#06B6D4", "#84CC16", "#F97316", "#E11D48", "#A855F7"
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cursos, container, false);
        initViews(view);

        TokenManager tokenManager = new TokenManager(requireContext());
        cursoRepository = new CursoRepository(tokenManager);

        setupRecyclerView();
        setupListeners();
        cargarCursos();
        return view;
    }

    private void initViews(View view) {
        rvCursos = view.findViewById(R.id.rv_cursos);
        tvSinCursos = view.findViewById(R.id.tv_sin_cursos);
        tvNumCursos = view.findViewById(R.id.tv_num_cursos);
        fabAdd = view.findViewById(R.id.fab_add_curso);
    }

    private void setupRecyclerView() {
        rvCursos.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new CursosAdapter(cursosList,
                this::abrirDetalleCurso,
                this::confirmarEliminarCurso);
        rvCursos.setAdapter(adapter);
    }

    private void setupListeners() {
        fabAdd.setOnClickListener(v -> mostrarDialogoCrearCurso());
    }

    private void abrirDetalleCurso(Curso curso) {
        DetalleCursoFragment fragment = new DetalleCursoFragment();
        Bundle args = new Bundle();
        args.putLong("curso_id", curso.getId());
        args.putString("curso_nombre", curso.getNombre());
        args.putString("curso_profesor", curso.getProfesor());
        args.putString("curso_color", curso.getColor());
        args.putInt("curso_pendientes", curso.getPendientes());
        args.putString("curso_horario", curso.getHorario());
        args.putString("curso_descripcion", curso.getDescripcion());
        fragment.setArguments(args);
        ((MainActivity) requireActivity()).cargarFragmento(fragment, true);
    }

    private void cargarCursos() {
        cursoRepository.getCursos(new CursoRepository.CursosCallback() {
            @Override
            public void onSuccess(List<Curso> cursos) {
                cursosList.clear();
                cursosList.addAll(cursos);
                adapter.notifyDataSetChanged();
                actualizarVistas();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                actualizarVistas();
            }
        });
    }

    private void actualizarVistas() {
        tvNumCursos.setText(cursosList.size() + " curso" + (cursosList.size() != 1 ? "s" : ""));
        tvSinCursos.setVisibility(cursosList.isEmpty() ? View.VISIBLE : View.GONE);
        rvCursos.setVisibility(cursosList.isEmpty() ? View.GONE : View.VISIBLE);
    }

    // ===================== DIÁLOGO CREAR/EDITAR CURSO =====================

    private void mostrarDialogoCrearCurso() {
        mostrarDialogoCurso(null);
    }

    private void mostrarDialogoEditarCurso(Curso curso) {
        mostrarDialogoCurso(curso);
    }

    private void mostrarDialogoCurso(final Curso cursoExistente) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_curso, null);
        builder.setView(dialogView);

        TextInputEditText etNombre = dialogView.findViewById(R.id.et_curso_nombre);
        TextInputEditText etProfesor = dialogView.findViewById(R.id.et_curso_docente);
        TextInputEditText etHorario = dialogView.findViewById(R.id.et_curso_horario);
        TextInputEditText etDescripcion = dialogView.findViewById(R.id.et_curso_descripcion);
        LinearLayout layoutColores = dialogView.findViewById(R.id.layout_colores);

        boolean esEdicion = cursoExistente != null;
        builder.setTitle(esEdicion ? "Editar curso" : "Nuevo curso");

        final String[] colorSeleccionado = {esEdicion ? cursoExistente.getColor() : coloresDisponibles[0]};

        if (esEdicion) {
            etNombre.setText(cursoExistente.getNombre());
            etProfesor.setText(cursoExistente.getProfesor() != null ? cursoExistente.getProfesor() : "");
            etHorario.setText(cursoExistente.getHorario() != null ? cursoExistente.getHorario() : "");
            etDescripcion.setText(cursoExistente.getDescripcion() != null ? cursoExistente.getDescripcion() : "");
        }

        for (int i = 0; i < coloresDisponibles.length; i++) {
            String hexColor = coloresDisponibles[i];
            ImageView colorView = new ImageView(requireContext());
            int size = getResources().getDimensionPixelSize(R.dimen.icon_lg);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
            lp.setMargins(4, 4, 4, 4);
            colorView.setLayoutParams(lp);

            GradientDrawable circle = new GradientDrawable();
            circle.setShape(GradientDrawable.OVAL);
            circle.setColor(Color.parseColor(hexColor));
            circle.setStroke(3, hexColor.equals(colorSeleccionado[0])
                    ? Color.parseColor("#1F2937") : Color.TRANSPARENT);
            colorView.setBackground(circle);

            final String currentColor = hexColor;
            colorView.setOnClickListener(v -> {
                colorSeleccionado[0] = currentColor;
                for (int j = 0; j < layoutColores.getChildCount(); j++) {
                    View child = layoutColores.getChildAt(j);
                    if (child.getBackground() instanceof GradientDrawable) {
                        GradientDrawable gd = (GradientDrawable) child.getBackground();
                        gd.setStroke(3, coloresDisponibles[j].equals(currentColor)
                                ? Color.parseColor("#1F2937") : Color.TRANSPARENT);
                    }
                }
            });

            layoutColores.addView(colorView);
        }

        builder.setPositiveButton(esEdicion ? "Guardar" : "Crear", null);
        builder.setNegativeButton("Cancelar", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            if (nombre.isEmpty()) {
                etNombre.setError("El nombre es obligatorio");
                return;
            }

            String profesor = etProfesor.getText().toString().trim();
            String horario = etHorario.getText().toString().trim();
            String descripcion = etDescripcion.getText().toString().trim();

            if (esEdicion) {
                cursoExistente.setNombre(nombre);
                cursoExistente.setProfesor(profesor);
                cursoExistente.setColor(colorSeleccionado[0]);
                cursoExistente.setHorario(horario);
                cursoExistente.setDescripcion(descripcion);
                cursoRepository.updateCurso(cursoExistente, new CursoRepository.CursoCallback() {
                    @Override
                    public void onSuccess(Curso curso) {
                        dialog.dismiss();
                        cargarCursos();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Curso nuevo = new Curso(nombre, profesor, colorSeleccionado[0]);
                nuevo.setHorario(horario);
                nuevo.setDescripcion(descripcion);
                cursoRepository.createCurso(nuevo, new CursoRepository.CursoCallback() {
                    @Override
                    public void onSuccess(Curso curso) {
                        dialog.dismiss();
                        cargarCursos();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    // ===================== ELIMINAR CURSO =====================

    private void confirmarEliminarCurso(final Curso curso) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar curso")
                .setMessage("¿Estás seguro de eliminar \"" + curso.getNombre() + "\"?\n" +
                        "Todas las actividades asociadas también se eliminarán.")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    cursoRepository.deleteCurso(curso.getId(), new CursoRepository.VoidCallback() {
                        @Override
                        public void onDone() {
                            cargarCursos();
                            Toast.makeText(requireContext(), "Curso eliminado", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
