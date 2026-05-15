package com.example.unitask_manager.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unitask_manager.R;
import com.example.unitask_manager.adapters.ActividadesAdapter;
import com.example.unitask_manager.models.Actividad;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    private TextView tvSaludo;
    private TextView tvFechaActual;
    private TextView tvCompletadas;
    private TextView tvPendientes;
    private RecyclerView rvUrgentes, rvProximas;

    private ActividadesAdapter adapterUrgentes, adapterProximas;
    private List<Actividad> urgentes, proximas;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        initViews(view);
        configurarRecyclers();
        cargarFecha();
        cargarDatosEjemplo();
        configurarListeners(view);
        return view;
    }

    private void initViews(View view) {
        tvSaludo = view.findViewById(R.id.tv_saludo);
        tvFechaActual = view.findViewById(R.id.tv_fecha_actual);
        tvCompletadas = view.findViewById(R.id.tv_completadas);
        tvPendientes = view.findViewById(R.id.tv_pendientes);
        rvUrgentes = view.findViewById(R.id.rv_urgentes);
        rvProximas = view.findViewById(R.id.rv_proximas);
    }

    private void configurarRecyclers() {
        urgentes = new ArrayList<>();
        proximas = new ArrayList<>();

        rvUrgentes.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUrgentes.setNestedScrollingEnabled(false);
        adapterUrgentes = new ActividadesAdapter(urgentes, item ->
                Toast.makeText(getContext(), item.getTitulo(), Toast.LENGTH_SHORT).show());
        rvUrgentes.setAdapter(adapterUrgentes);

        rvProximas.setLayoutManager(new LinearLayoutManager(getContext()));
        rvProximas.setNestedScrollingEnabled(false);
        adapterProximas = new ActividadesAdapter(proximas, item ->
                Toast.makeText(getContext(), item.getTitulo(), Toast.LENGTH_SHORT).show());
        rvProximas.setAdapter(adapterProximas);
    }

    private void cargarFecha() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
        String fecha = sdf.format(new Date());
        fecha = fecha.substring(0, 1).toUpperCase() + fecha.substring(1);
        tvFechaActual.setText(fecha);
    }

    private void cargarDatosEjemplo() {
        urgentes.add(new Actividad("Entregar proyecto final", "Tarea", "13 mayo 2026", "11:59 PM",
                Actividad.PRIORIDAD_ALTA, "", 1));
        urgentes.add(new Actividad("Examen de programación", "Examen", "14 mayo 2026", "2:00 PM",
                Actividad.PRIORIDAD_ALTA, "", 1));
        adapterUrgentes.notifyDataSetChanged();

        proximas.add(new Actividad("Preparar exposición oral", "Exposición", "15 mayo 2026", "8:00 AM",
                Actividad.PRIORIDAD_MEDIA, "", 2));
        proximas.add(new Actividad("Estudiar cálculo diferencial", "Tarea", "16 mayo 2026", "10:00 AM",
                Actividad.PRIORIDAD_MEDIA, "", 3));
        proximas.add(new Actividad("Leer capítulo 5 de física", "Tarea", "17 mayo 2026", "11:59 PM",
                Actividad.PRIORIDAD_MEDIA, "", 3));
        adapterProximas.notifyDataSetChanged();

        tvCompletadas.setText("12");
        tvPendientes.setText("8");
    }

    private void configurarListeners(View view) {
        view.findViewById(R.id.iv_notificaciones).setOnClickListener(v ->
                Toast.makeText(getContext(), "Notificaciones", Toast.LENGTH_SHORT).show());
        view.findViewById(R.id.card_completadas).setOnClickListener(v ->
                Toast.makeText(getContext(), "Actividades completadas", Toast.LENGTH_SHORT).show());
        view.findViewById(R.id.card_pendientes).setOnClickListener(v ->
                Toast.makeText(getContext(), "Actividades pendientes", Toast.LENGTH_SHORT).show());
        view.findViewById(R.id.fab_add_activity).setOnClickListener(v ->
                Toast.makeText(getContext(), "Agregar nueva actividad", Toast.LENGTH_SHORT).show());
    }
}
