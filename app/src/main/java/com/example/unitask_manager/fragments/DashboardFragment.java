package com.example.unitask_manager.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unitask_manager.R;
import com.example.unitask_manager.activities.MainActivity;
import com.example.unitask_manager.adapters.ActividadesAdapter;
import com.example.unitask_manager.database.DatabaseHelper;
import com.example.unitask_manager.models.Actividad;
import com.example.unitask_manager.settings.SettingsPreferenceManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    private static final int LIMITE_VISTA = 5;

    private TextView tvSaludo;
    private TextView tvFechaActual;
    private TextView tvCompletadas;
    private TextView tvPendientes;
    private TextView tvVerMasUrgentes, tvVerMasProximas;
    private TextView tvAvatarHeader;
    private RecyclerView rvUrgentes, rvProximas;

    private ActividadesAdapter adapterUrgentes, adapterProximas;
    private List<Actividad> urgentes, proximas;
    private List<Actividad> todasUrgentes, todasProximas;
    private boolean urgentesExpandido = false;
    private boolean proximasExpandido = false;

    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        dbHelper = new DatabaseHelper(requireContext());
        initViews(view);
        configurarRecyclers();
        cargarFecha();
        cargarNombreUsuario();
        cargarActividades();
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
        tvVerMasUrgentes = view.findViewById(R.id.tv_ver_mas_urgentes);
        tvVerMasProximas = view.findViewById(R.id.tv_ver_mas_proximas);
        tvAvatarHeader = view.findViewById(R.id.tv_avatar_header);
    }

    private void configurarRecyclers() {
        urgentes = new ArrayList<>();
        proximas = new ArrayList<>();

        rvUrgentes.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUrgentes.setNestedScrollingEnabled(false);
        adapterUrgentes = new ActividadesAdapter(urgentes, this::abrirEditarActividad);
        adapterUrgentes.setOnCheckedListener(this::toggleCompletada);
        rvUrgentes.setAdapter(adapterUrgentes);

        rvProximas.setLayoutManager(new LinearLayoutManager(getContext()));
        rvProximas.setNestedScrollingEnabled(false);
        adapterProximas = new ActividadesAdapter(proximas, this::abrirEditarActividad);
        adapterProximas.setOnCheckedListener(this::toggleCompletada);
        rvProximas.setAdapter(adapterProximas);
    }

    private void toggleCompletada(Actividad actividad, boolean completada) {
        dbHelper.marcarCompletada(actividad.getId(), completada);
        cargarActividades();
    }

    private void abrirEditarActividad(Actividad actividad) {
        AddTaskFragment fragment = new AddTaskFragment();
        Bundle args = new Bundle();
        args.putLong("actividad_id", actividad.getId());
        fragment.setArguments(args);
        ((MainActivity) requireActivity()).cargarFragmento(fragment, true);
    }

    private void cargarFecha() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
        String fecha = sdf.format(new Date());
        fecha = fecha.substring(0, 1).toUpperCase() + fecha.substring(1);
        tvFechaActual.setText(fecha);
    }

    private void cargarNombreUsuario() {
        SettingsPreferenceManager prefs = new SettingsPreferenceManager(requireContext());
        String nombre = prefs.getUserName();
        tvSaludo.setText("¡Hola, " + nombre + "! 👋");
        tvAvatarHeader.setText(SettingsPreferenceManager.computeInitials(nombre));
    }

    private void cargarActividades() {
        todasUrgentes = dbHelper.obtenerActividadesPorPrioridad(Actividad.PRIORIDAD_ALTA, true);
        todasProximas = dbHelper.obtenerActividadesPorPrioridad(Actividad.PRIORIDAD_MEDIA, true);

        urgentesExpandido = false;
        proximasExpandido = false;
        actualizarListaUrgentes();
        actualizarListaProximas();

        tvCompletadas.setText(String.valueOf(dbHelper.contarCompletadas()));
        tvPendientes.setText(String.valueOf(dbHelper.contarPendientes()));
    }

    private void actualizarListaUrgentes() {
        urgentes.clear();
        if (urgentesExpandido) {
            urgentes.addAll(todasUrgentes);
            tvVerMasUrgentes.setText("Mostrar menos");
        } else {
            int limite = Math.min(todasUrgentes.size(), LIMITE_VISTA);
            for (int i = 0; i < limite; i++) {
                urgentes.add(todasUrgentes.get(i));
            }
            tvVerMasUrgentes.setText(todasUrgentes.size() > LIMITE_VISTA ? "Ver más" : "");
            tvVerMasUrgentes.setVisibility(todasUrgentes.size() > LIMITE_VISTA ? View.VISIBLE : View.GONE);
        }
        adapterUrgentes.notifyDataSetChanged();
    }

    private void actualizarListaProximas() {
        proximas.clear();
        if (proximasExpandido) {
            proximas.addAll(todasProximas);
            tvVerMasProximas.setText("Mostrar menos");
        } else {
            int limite = Math.min(todasProximas.size(), LIMITE_VISTA);
            for (int i = 0; i < limite; i++) {
                proximas.add(todasProximas.get(i));
            }
            tvVerMasProximas.setText(todasProximas.size() > LIMITE_VISTA ? "Ver más" : "");
            tvVerMasProximas.setVisibility(todasProximas.size() > LIMITE_VISTA ? View.VISIBLE : View.GONE);
        }
        adapterProximas.notifyDataSetChanged();
    }

    private void configurarListeners(View view) {
        view.findViewById(R.id.card_completadas).setOnClickListener(v ->
                Toast.makeText(getContext(), "Actividades completadas", Toast.LENGTH_SHORT).show());
        view.findViewById(R.id.card_pendientes).setOnClickListener(v ->
                Toast.makeText(getContext(), "Actividades pendientes", Toast.LENGTH_SHORT).show());
        view.findViewById(R.id.fab_add_activity).setOnClickListener(v -> {
            AddTaskFragment fragment = new AddTaskFragment();
            ((MainActivity) requireActivity()).cargarFragmento(fragment, true);
        });

        tvVerMasUrgentes.setOnClickListener(v -> {
            urgentesExpandido = !urgentesExpandido;
            actualizarListaUrgentes();
        });

        tvVerMasProximas.setOnClickListener(v -> {
            proximasExpandido = !proximasExpandido;
            actualizarListaProximas();
        });

        tvAvatarHeader.setOnClickListener(v -> {
            AjustesFragment fragment = new AjustesFragment();
            ((MainActivity) requireActivity()).cargarFragmento(fragment, true);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarActividades();
    }
}
