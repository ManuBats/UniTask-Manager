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
import com.example.unitask_manager.models.Actividad;
import com.example.unitask_manager.models.Curso;
import com.example.unitask_manager.views.BarChartView;
import com.example.unitask_manager.views.CircularProgressView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class StatsFragment extends Fragment {

    private TextView tvProgressPercent, tvMotivacion, tvTopCurso, tvTopCursoCount, tvPendientesCount;
    private CircularProgressView circularProgress;
    private BarChartView barChart;
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
        barChart = view.findViewById(R.id.bar_chart);
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

        cargarBarChart();
        cargarTopCurso();
    }

    private void cargarBarChart() {
        SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy", new Locale("es", "ES"));
        String[] labels = {"L", "M", "M", "J", "V", "S", "D"};
        int[] values = new int[7];
        String[] dayAbbr = {"D", "L", "M", "M", "J", "V", "S"};

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        List<Actividad> completadas = dbHelper.obtenerActividades();

        for (int i = 0; i < 7; i++) {
            String dateStr = sdf.format(cal.getTime());
            int count = 0;
            for (Actividad a : completadas) {
                if (a.isCompletada() && a.getFecha().equals(dateStr)) {
                    count++;
                }
            }
            values[i] = count;
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        barChart.setData(values, labels);
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
