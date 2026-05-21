package com.example.unitask_manager.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.unitask_manager.R;
import com.example.unitask_manager.database.DatabaseHelper;
import com.example.unitask_manager.models.Curso;
import com.example.unitask_manager.views.CircularProgressView;
import com.example.unitask_manager.views.PieChartView;

import java.util.List;

public class StatsFragment extends Fragment {

    private TextView tvProgressPercent, tvMotivacion, tvTopCurso, tvTopCursoCount, tvPendientesCount;
    private CircularProgressView circularProgress;
    private PieChartView pieChart;
    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbHelper = new DatabaseHelper(requireContext());
        tvProgressPercent = view.findViewById(R.id.tv_progress_percent);
        tvMotivacion = view.findViewById(R.id.tv_motivacion);
        circularProgress = view.findViewById(R.id.circular_progress);
        pieChart = view.findViewById(R.id.pie_chart);
        tvTopCurso = view.findViewById(R.id.tv_top_curso);
        tvTopCursoCount = view.findViewById(R.id.tv_top_curso_count);
        tvPendientesCount = view.findViewById(R.id.tv_pendientes_count);
        cargarEstadisticas();
    }

    private void cargarEstadisticas() {
        int total = dbHelper.obtenerActividades().size();
        int completadas = dbHelper.contarCompletadas();
        int pendientes = dbHelper.contarPendientes();

        float porcentaje = total > 0 ? (float) completadas / total : 0f;

        tvProgressPercent.setText(Math.round(porcentaje * 100) + "%");
        circularProgress.setProgress(porcentaje);

        if (porcentaje >= 0.75f) {
            tvMotivacion.setText("¡Excelente progreso!");
        } else if (porcentaje >= 0.50f) {
            tvMotivacion.setText("¡Buen trabajo!");
        } else if (porcentaje >= 0.25f) {
            tvMotivacion.setText("Vas por buen camino");
        } else {
            tvMotivacion.setText("¡Empieza a completar tareas!");
        }

        tvPendientesCount.setText(String.valueOf(pendientes));

        cargarPieChart();
        cargarTopCurso();
    }

    private void cargarPieChart() {
        List<Curso> cursos = dbHelper.obtenerCursos();
        String[] nombres = new String[cursos.size()];
        float[] porcentajes = new float[cursos.size()];
        int[] colores = new int[cursos.size()];

        for (int i = 0; i < cursos.size(); i++) {
            Curso c = cursos.get(i);
            nombres[i] = c.getNombre();
            colores[i] = parseColor(c.getColor());
            int totalActividades = dbHelper.contarActividadesPorCurso(c.getId());
            if (totalActividades > 0) {
                int completadas = dbHelper.contarCompletadasPorCurso(c.getId());
                porcentajes[i] = (float) completadas / totalActividades;
            } else {
                porcentajes[i] = 0f;
            }
        }

        pieChart.setData(nombres, porcentajes, colores);
    }

    private int parseColor(String colorStr) {
        try {
            return android.graphics.Color.parseColor(colorStr);
        } catch (Exception e) {
            return 0xFF7C3AED;
        }
    }

    private void cargarTopCurso() {
        List<Curso> cursos = dbHelper.obtenerCursos();
        String topNombre = "—";
        String topCount = "0 actividades pendientes";

        if (!cursos.isEmpty()) {
            Curso top = null;
            int maxPendientes = 0;
            for (Curso c : cursos) {
                int pendientes = dbHelper.contarPendientesPorCurso(c.getId());
                if (pendientes > maxPendientes) {
                    maxPendientes = pendientes;
                    top = c;
                }
            }
            if (top != null && maxPendientes > 0) {
                topNombre = top.getNombre();
                topCount = maxPendientes + " actividades pendientes";
            }
        }

        tvTopCurso.setText(topNombre);
        tvTopCursoCount.setText(topCount);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dbHelper != null) {
            cargarEstadisticas();
        }
    }
}
