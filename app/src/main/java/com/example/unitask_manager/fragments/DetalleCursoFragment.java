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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unitask_manager.R;
import com.example.unitask_manager.activities.MainActivity;
import com.example.unitask_manager.adapters.ActividadesAdapter;
import com.example.unitask_manager.data.local.TokenManager;
import com.example.unitask_manager.data.repository.CursoRepository;
import com.example.unitask_manager.database.DatabaseHelper;
import com.example.unitask_manager.models.Actividad;
import com.example.unitask_manager.models.Curso;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class DetalleCursoFragment extends Fragment {

    private TextView tvNombre, tvProfesor, tvPendientes;
    private TextView tvHorario, tvProgresoTexto, tvDescripcion;
    private TextView tvNumPendientes, tvNumCompletadas;
    private ImageView ivIcono;
    private ProgressBar progressBar;
    private View viewColor;
    private View btnDelete, btnEdit, btnBack;
    private RecyclerView rvPendientes, rvCompletadas;
    private FloatingActionButton fabAdd;

    private List<Actividad> listaPendientes, listaCompletadas;
    private ActividadesAdapter adapterPendientes, adapterCompletadas;
    private CursoRepository cursoRepository;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detalle_curso, container, false);
        initViews(view);

        TokenManager tokenManager = new TokenManager(requireContext());
        cursoRepository = new CursoRepository(tokenManager);

        cargarInfoCurso();
        configurarRecycler();
        cargarActividades();
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
        btnEdit = view.findViewById(R.id.btn_edit_curso_detail);
        btnBack = view.findViewById(R.id.btn_back_curso);
        rvPendientes = view.findViewById(R.id.rv_detalle_pendientes);
        rvCompletadas = view.findViewById(R.id.rv_detalle_completadas);
        tvNumPendientes = view.findViewById(R.id.tv_detalle_num_pendientes);
        tvNumCompletadas = view.findViewById(R.id.tv_detalle_num_completadas);
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
        listaPendientes = new ArrayList<>();
        listaCompletadas = new ArrayList<>();

        rvPendientes.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPendientes.setNestedScrollingEnabled(false);
        adapterPendientes = new ActividadesAdapter(listaPendientes, this::abrirEditarActividad);
        adapterPendientes.setOnCheckedListener(this::toggleCompletada);
        rvPendientes.setAdapter(adapterPendientes);

        rvCompletadas.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCompletadas.setNestedScrollingEnabled(false);
        adapterCompletadas = new ActividadesAdapter(listaCompletadas, this::abrirEditarActividad);
        adapterCompletadas.setOnCheckedListener(this::toggleCompletada);
        rvCompletadas.setAdapter(adapterCompletadas);
    }

    private void abrirEditarActividad(Actividad actividad) {
        AddTaskFragment fragment = new AddTaskFragment();
        Bundle args = new Bundle();
        args.putLong("actividad_id", actividad.getId());
        fragment.setArguments(args);
        ((MainActivity) requireActivity()).cargarFragmento(fragment, true);
    }

    private void cargarActividades() {
        long cursoId = getArguments() != null ? getArguments().getLong("curso_id", 0) : 0;
        // Pendiente: migrar a API cuando el integrante 3 conecte ActividadRepository
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        List<Actividad> todas = dbHelper.obtenerActividadesPorCursoId(cursoId);
        listaPendientes.clear();
        listaCompletadas.clear();
        for (Actividad a : todas) {
            if (a.isCompletada()) {
                listaCompletadas.add(a);
            } else {
                listaPendientes.add(a);
            }
        }
        adapterPendientes.notifyDataSetChanged();
        adapterCompletadas.notifyDataSetChanged();
        actualizarProgreso();
    }

    private void toggleCompletada(Actividad actividad, boolean completada) {
        // Pendiente: migrar a API cuando el integrante 3 conecte ActividadRepository
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        dbHelper.marcarCompletada(actividad.getId(), completada);
        actividad.setCompletada(completada);

        if (completada) {
            listaPendientes.remove(actividad);
            listaCompletadas.add(actividad);
        } else {
            listaCompletadas.remove(actividad);
            listaPendientes.add(0, actividad);
        }
        adapterPendientes.notifyDataSetChanged();
        adapterCompletadas.notifyDataSetChanged();
        actualizarProgreso();
    }

    private void actualizarProgreso() {
        int total = listaPendientes.size() + listaCompletadas.size();
        int completadas = listaCompletadas.size();
        int progreso = total > 0 ? (completadas * 100 / total) : 0;
        progressBar.setProgress(progreso);
        tvProgresoTexto.setText(progreso + "%");
        tvPendientes.setText((total - completadas) + " actividad" + ((total - completadas) != 1 ? "es" : "") + " pendiente" + ((total - completadas) != 1 ? "s" : ""));
        tvNumPendientes.setText(String.valueOf(listaPendientes.size()));
        tvNumCompletadas.setText(String.valueOf(listaCompletadas.size()));
    }

    private void configurarListeners(View view) {
        fabAdd.setOnClickListener(v -> {
            AddTaskFragment fragment = new AddTaskFragment();
            Bundle args = new Bundle();
            long cursoId = getArguments() != null ? getArguments().getLong("curso_id", 0) : 0;
            args.putLong("curso_id", cursoId);
            fragment.setArguments(args);
            ((MainActivity) requireActivity()).cargarFragmento(fragment, true);
        });

        btnBack.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());

        btnEdit.setOnClickListener(v -> mostrarDialogoEditarCurso());

        btnDelete.setOnClickListener(v -> {
            String nombreCurso = getArguments() != null ? getArguments().getString("curso_nombre", "este curso") : "este curso";
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Eliminar curso")
                    .setMessage("¿Eliminar \"" + nombreCurso + "\" y sus actividades?")
                    .setPositiveButton("Eliminar", (dialog, which) -> {
                        long cursoId = getArguments() != null ? getArguments().getLong("curso_id", 0) : 0;
                        cursoRepository.deleteCurso(cursoId, new CursoRepository.VoidCallback() {
                            @Override
                            public void onDone() {
                                Toast.makeText(getContext(), "Curso eliminado", Toast.LENGTH_SHORT).show();
                                requireActivity().getSupportFragmentManager().popBackStack();
                            }

                            @Override
                            public void onError(String error) {
                                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    private void mostrarDialogoEditarCurso() {
        Bundle args = getArguments();
        if (args == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_curso, null);
        builder.setView(dialogView);

        TextInputEditText etNombre = dialogView.findViewById(R.id.et_curso_nombre);
        TextInputEditText etProfesor = dialogView.findViewById(R.id.et_curso_docente);
        TextInputEditText etHorario = dialogView.findViewById(R.id.et_curso_horario);
        TextInputEditText etDescripcion = dialogView.findViewById(R.id.et_curso_descripcion);
        LinearLayout layoutColores = dialogView.findViewById(R.id.layout_colores);

        String[] coloresDisponibles = {
                "#7C3AED", "#3B82F6", "#10B981", "#F59E0B", "#EF4444", "#EC4899",
                "#8B5CF6", "#06B6D4", "#84CC16", "#F97316", "#E11D48", "#A855F7"
        };

        builder.setTitle("Editar curso");

        etNombre.setText(args.getString("curso_nombre", ""));
        etProfesor.setText(args.getString("curso_profesor", ""));
        etHorario.setText(args.getString("curso_horario", ""));
        etDescripcion.setText(args.getString("curso_descripcion", ""));

        final String[] colorSeleccionado = {args.getString("curso_color", coloresDisponibles[0])};

        for (String hexColor : coloresDisponibles) {
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

            colorView.setOnClickListener(v -> {
                colorSeleccionado[0] = hexColor;
                for (int j = 0; j < layoutColores.getChildCount(); j++) {
                    View child = layoutColores.getChildAt(j);
                    if (child.getBackground() instanceof GradientDrawable) {
                        GradientDrawable gd = (GradientDrawable) child.getBackground();
                        gd.setStroke(3, coloresDisponibles[j].equals(hexColor)
                                ? Color.parseColor("#1F2937") : Color.TRANSPARENT);
                    }
                }
            });

            layoutColores.addView(colorView);
        }

        builder.setPositiveButton("Guardar", null);
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

            Curso curso = new Curso(
                    args.getLong("curso_id", 0),
                    nombre,
                    profesor,
                    colorSeleccionado[0]
            );
            curso.setHorario(horario);
            curso.setDescripcion(descripcion);

            cursoRepository.updateCurso(curso, new CursoRepository.CursoCallback() {
                @Override
                public void onSuccess(Curso cursoActualizado) {
                    args.putString("curso_nombre", nombre);
                    args.putString("curso_profesor", profesor);
                    args.putString("curso_color", colorSeleccionado[0]);
                    args.putString("curso_horario", horario);
                    args.putString("curso_descripcion", descripcion);
                    cargarInfoCurso();
                    dialog.dismiss();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private List<Actividad> obtenerActividadesPorCurso(long cursoId) {
        // Pendiente: migrar a API cuando el integrante 3 conecte ActividadRepository
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        return dbHelper.obtenerActividadesPorCursoId(cursoId);
    }
}
