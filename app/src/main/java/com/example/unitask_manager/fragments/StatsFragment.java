package com.example.unitask_manager.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.unitask_manager.R;
import com.example.unitask_manager.data.local.TokenManager;
import com.example.unitask_manager.data.repository.ActividadRepository;
import com.example.unitask_manager.data.repository.CursoRepository;
import com.example.unitask_manager.models.Actividad;
import com.example.unitask_manager.models.Curso;
import com.example.unitask_manager.views.CircularProgressView;
import com.example.unitask_manager.views.PieChartView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsFragment extends Fragment {

    private TextView tvProgressPercent, tvMotivacion, tvTopCurso, tvTopCursoCount, tvPendientesCount;
    private CircularProgressView circularProgress;
    private PieChartView pieChart;
    private ActividadRepository actividadRepo;
    private CursoRepository cursoRepo;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TokenManager tokenManager = new TokenManager(requireContext());
        actividadRepo = new ActividadRepository(tokenManager);
        cursoRepo = new CursoRepository(tokenManager);

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
        actividadRepo.getActividades(new ActividadRepository.ActividadesCallback() {
            @Override
            public void onSuccess(List<Actividad> actividades) {
                int total = actividades.size();
                int completadas = 0;
                int pendientes = 0;

                Map<Long, Integer> cursoTotal = new HashMap<>();
                Map<Long, Integer> cursoCompletadas = new HashMap<>();

                for (Actividad a : actividades) {
                    if (a.isCompletada()) {
                        completadas++;
                    } else {
                        pendientes++;
                    }

                    long idCurso = a.getIdCurso();
                    if (idCurso > 0) {
                        cursoTotal.put(idCurso, cursoTotal.getOrDefault(idCurso, 0) + 1);
                        if (a.isCompletada()) {
                            cursoCompletadas.put(idCurso, cursoCompletadas.getOrDefault(idCurso, 0) + 1);
                        }
                    }
                }

                final float porcentaje = total > 0 ? (float) completadas / total : 0f;
                final int pendientesFinal = pendientes;
                final Map<Long, Integer> totals = cursoTotal;
                final Map<Long, Integer> completadass = cursoCompletadas;

                requireActivity().runOnUiThread(() -> {
                    tvProgressPercent.setText(Math.round(porcentaje * 100) + "%");
                    circularProgress.setProgress(porcentaje);

                    if (porcentaje >= 0.75f) {
                        tvMotivacion.setText("\u00a1Excelente progreso!");
                    } else if (porcentaje >= 0.50f) {
                        tvMotivacion.setText("\u00a1Buen trabajo!");
                    } else if (porcentaje >= 0.25f) {
                        tvMotivacion.setText("Vas por buen camino");
                    } else {
                        tvMotivacion.setText("\u00a1Empieza a completar tareas!");
                    }

                    tvPendientesCount.setText(String.valueOf(pendientesFinal));
                });

                cargarInfoCursos(actividades, totals, completadass);
            }

            @Override
            public void onError(String error) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void cargarInfoCursos(List<Actividad> actividades,
                                   Map<Long, Integer> cursoTotal,
                                   Map<Long, Integer> cursoCompletadas) {
        cursoRepo.getCursos(new CursoRepository.CursosCallback() {
            @Override
            public void onSuccess(List<Curso> cursos) {
                Map<Long, String> nombresMap = new HashMap<>();
                Map<Long, String> coloresMap = new HashMap<>();
                for (Curso c : cursos) {
                    nombresMap.put(c.getId(), c.getNombre());
                    coloresMap.put(c.getId(), c.getColor());
                }
                requireActivity().runOnUiThread(() -> {
                    cargarPieChart(nombresMap, coloresMap, cursoTotal, cursoCompletadas);
                    cargarTopCurso(nombresMap, actividades);
                });
            }

            @Override
            public void onError(String error) {
            }
        });
    }

    private void cargarPieChart(Map<Long, String> nombresMap,
                                 Map<Long, String> coloresMap,
                                 Map<Long, Integer> cursoTotal,
                                 Map<Long, Integer> cursoCompletadas) {
        List<Long> cursoIds = new ArrayList<>(cursoTotal.keySet());
        String[] nombres = new String[cursoIds.size()];
        float[] porcentajes = new float[cursoIds.size()];
        int[] colores = new int[cursoIds.size()];

        for (int i = 0; i < cursoIds.size(); i++) {
            Long id = cursoIds.get(i);
            nombres[i] = nombresMap.containsKey(id) ? nombresMap.get(id) : "Curso #" + id;
            int total = cursoTotal.get(id);
            int comp = cursoCompletadas.getOrDefault(id, 0);
            porcentajes[i] = total > 0 ? (float) comp / total : 0f;
            colores[i] = parseColor(coloresMap.get(id));
        }

        pieChart.setData(nombres, porcentajes, colores);
    }

    private int parseColor(String colorStr) {
        if (colorStr == null) return 0xFF7C3AED;
        try {
            return android.graphics.Color.parseColor(colorStr);
        } catch (Exception e) {
            return 0xFF7C3AED;
        }
    }

    private void cargarTopCurso(Map<Long, String> nombresMap, List<Actividad> actividades) {
        Map<Long, Integer> pendientesPorCurso = new HashMap<>();
        for (Actividad a : actividades) {
            if (!a.isCompletada() && a.getIdCurso() > 0) {
                long id = a.getIdCurso();
                pendientesPorCurso.put(id, pendientesPorCurso.getOrDefault(id, 0) + 1);
            }
        }

        long topId = -1;
        int maxPendientes = 0;
        for (Map.Entry<Long, Integer> entry : pendientesPorCurso.entrySet()) {
            if (entry.getValue() > maxPendientes) {
                maxPendientes = entry.getValue();
                topId = entry.getKey();
            }
        }

        if (topId != -1 && maxPendientes > 0) {
            String nombre = nombresMap.containsKey(topId) ? nombresMap.get(topId) : "Curso #" + topId;
            tvTopCurso.setText(nombre);
            tvTopCursoCount.setText(maxPendientes + " actividades pendientes");
        } else {
            tvTopCurso.setText("\u2014");
            tvTopCursoCount.setText("0 actividades pendientes");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarEstadisticas();
    }
}
