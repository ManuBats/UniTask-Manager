package com.example.unitask_manager.fragments;

import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.example.unitask_manager.data.repository.ActividadRepository;
import com.example.unitask_manager.models.Actividad;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class AgendaFragment extends Fragment {

    private TextView tvMesAnio, tvFechaSeleccionada, tvSinActividades;
    private GridLayout calendarGrid;
    private RecyclerView rvActividadesDia;
    private ImageButton btnMesAnterior, btnMesSiguiente;

    private int currentYear, currentMonth;
    private final Calendar selectedDate = Calendar.getInstance();

    private ActividadesAdapter adapter;
    private final List<Actividad> actividadesDia = new ArrayList<>();
    private final List<Actividad> allActividades = new ArrayList<>();

    private final SimpleDateFormat sdfMonthYear = new SimpleDateFormat("MMMM yyyy", new Locale("es", "ES"));
    private final SimpleDateFormat sdfFullDate = new SimpleDateFormat("d MMMM yyyy", new Locale("es", "ES"));
    private final SimpleDateFormat sdfDisplay = new SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));

    private int lastCellHeightPx;
    private ActividadRepository actividadRepository;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agenda, container, false);
        TokenManager tokenManager = new TokenManager(requireContext());
        actividadRepository = new ActividadRepository(tokenManager);
        initViews(view);
        initState();
        setupListeners();
        cargarActividades();
        return view;
    }

    private void initViews(View view) {
        tvMesAnio = view.findViewById(R.id.tv_mes_anio);
        tvFechaSeleccionada = view.findViewById(R.id.tv_fecha_seleccionada);
        tvSinActividades = view.findViewById(R.id.tv_sin_actividades);
        calendarGrid = view.findViewById(R.id.calendar_grid);
        rvActividadesDia = view.findViewById(R.id.rv_actividades_dia);
        btnMesAnterior = view.findViewById(R.id.btn_mes_anterior);
        btnMesSiguiente = view.findViewById(R.id.btn_mes_siguiente);
    }

    private void initState() {
        Calendar now = Calendar.getInstance();
        currentYear = now.get(Calendar.YEAR);
        currentMonth = now.get(Calendar.MONTH);
        selectedDate.setTimeInMillis(now.getTimeInMillis());

        rvActividadesDia.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ActividadesAdapter(actividadesDia, this::abrirEditarActividad);
        adapter.setOnCheckedListener(this::toggleCompletada);
        rvActividadesDia.setAdapter(adapter);
    }

    private void cargarActividades() {
        actividadRepository.getActividades(new ActividadRepository.ActividadesCallback() {
            @Override
            public void onSuccess(List<Actividad> actividades) {
                allActividades.clear();
                allActividades.addAll(actividades);
                updateCalendar();
                updateActividadesDia();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                updateCalendar();
                updateActividadesDia();
            }
        });
    }

    private void toggleCompletada(Actividad actividad, boolean completada) {
        actividadRepository.toggleCompletada(actividad, new ActividadRepository.ActividadCallback() {
            @Override
            public void onSuccess(Actividad actividad) {
                updateActividadesDia();
                updateCalendar();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void abrirEditarActividad(Actividad actividad) {
        AddTaskFragment fragment = new AddTaskFragment();
        Bundle args = new Bundle();
        args.putLong("actividad_id", actividad.getId());
        args.putString("actividad_titulo", actividad.getTitulo());
        args.putString("actividad_tipo", actividad.getTipo());
        args.putString("actividad_fecha", actividad.getFecha());
        args.putString("actividad_hora", actividad.getHora());
        args.putInt("actividad_prioridad", actividad.getPrioridad());
        args.putString("actividad_descripcion", actividad.getDescripcion());
        args.putLong("actividad_id_curso", actividad.getIdCurso());
        args.putBoolean("actividad_completada", actividad.isCompletada());
        fragment.setArguments(args);
        ((MainActivity) requireActivity()).cargarFragmento(fragment, true);
    }

    private void setupListeners() {
        btnMesAnterior.setOnClickListener(v -> navegarMes(-1));
        btnMesSiguiente.setOnClickListener(v -> navegarMes(1));
    }

    private void navegarMes(int delta) {
        currentMonth += delta;
        if (currentMonth < 0) {
            currentMonth = 11;
            currentYear--;
        } else if (currentMonth > 11) {
            currentMonth = 0;
            currentYear++;
        }
        int maxDay = getMaxDaysInMonth(currentYear, currentMonth);
        if (selectedDate.get(Calendar.DAY_OF_MONTH) > maxDay) {
            selectedDate.set(currentYear, currentMonth, maxDay);
        } else {
            selectedDate.set(currentYear, currentMonth, selectedDate.get(Calendar.DAY_OF_MONTH));
        }
        updateCalendar();
        updateActividadesDia();
    }

    private int getMaxDaysInMonth(int year, int month) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, 1);
        return c.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    private void updateCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.set(currentYear, currentMonth, 1);

        String mesAnio = capitalize(sdfMonthYear.format(cal.getTime()));
        tvMesAnio.setText(mesAnio);

        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int firstDayColumn = firstDayOfWeek - 1;
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        calendarGrid.removeAllViews();
        int totalCells = firstDayColumn + daysInMonth;
        int rows = (int) Math.ceil(totalCells / 7.0);
        calendarGrid.setRowCount(rows);

        Calendar today = Calendar.getInstance();
        String selectedDateStr = sdfFullDate.format(selectedDate.getTime());

        Set<String> fechasConActividades = new HashSet<>();
        for (Actividad a : allActividades) {
            fechasConActividades.add(a.getFecha());
        }

        int dotSizePx = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
        int purpleColor = ContextCompat.getColor(requireContext(), R.color.primary_purple);

        for (int i = 0; i < firstDayColumn; i++) {
            TextView empty = new TextView(requireContext());
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = 0;
            lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            empty.setLayoutParams(lp);
            calendarGrid.addView(empty);
        }

        for (int day = 1; day <= daysInMonth; day++) {
            Calendar dayCal = Calendar.getInstance();
            dayCal.set(currentYear, currentMonth, day);
            String dateStr = sdfFullDate.format(dayCal.getTime());

            boolean isToday = day == today.get(Calendar.DAY_OF_MONTH)
                    && currentMonth == today.get(Calendar.MONTH)
                    && currentYear == today.get(Calendar.YEAR);
            boolean isSelected = dateStr.equals(selectedDateStr);
            boolean hasActivity = fechasConActividades.contains(dateStr);

            LinearLayout cellContainer = new LinearLayout(requireContext());
            cellContainer.setOrientation(LinearLayout.VERTICAL);
            cellContainer.setGravity(Gravity.CENTER);
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = 0;
            lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            if (lastCellHeightPx > 0) {
                lp.height = lastCellHeightPx;
            }
            cellContainer.setLayoutParams(lp);

            TextView dayCell = new TextView(requireContext());
            dayCell.setText(String.valueOf(day));
            dayCell.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            dayCell.setGravity(Gravity.CENTER);

            if (isSelected) {
                int bgColor = ContextCompat.getColor(requireContext(), R.color.calendar_selected_bg);
                GradientDrawable bg = new GradientDrawable();
                bg.setShape(GradientDrawable.OVAL);
                bg.setColor(bgColor);
                dayCell.setBackground(bg);
                dayCell.setTextColor(ContextCompat.getColor(requireContext(), R.color.calendar_selected_text));
            } else {
                dayCell.setTextColor(ContextCompat.getColor(requireContext(), R.color.calendar_day_text));
            }

            if (isToday && !isSelected) {
                dayCell.setTypeface(null, Typeface.BOLD);
                dayCell.setTextColor(ContextCompat.getColor(requireContext(), R.color.calendar_today_text));
            }

            cellContainer.addView(dayCell);

            if (hasActivity && !isSelected) {
                View dot = new View(requireContext());
                LinearLayout.LayoutParams dotLp = new LinearLayout.LayoutParams(dotSizePx, dotSizePx);
                dotLp.setMargins(0, Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics())), 0, 0);
                dot.setLayoutParams(dotLp);
                GradientDrawable dotBg = new GradientDrawable();
                dotBg.setShape(GradientDrawable.OVAL);
                dotBg.setColor(purpleColor);
                dot.setBackground(dotBg);
                cellContainer.addView(dot);
            }

            int selectedDay = day;
            cellContainer.setOnClickListener(v -> {
                selectedDate.set(currentYear, currentMonth, selectedDay);
                updateCalendar();
                updateActividadesDia();
            });

            calendarGrid.addView(cellContainer);
        }

        equalizeCellHeights();
    }

    private void equalizeCellHeights() {
        calendarGrid.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                calendarGrid.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int gridHeight = calendarGrid.getHeight();
                int rowCount = calendarGrid.getRowCount();
                if (gridHeight <= 0 || rowCount <= 0) return;

                int cellHeight = gridHeight / rowCount;
                lastCellHeightPx = cellHeight;

                for (int i = 0; i < calendarGrid.getChildCount(); i++) {
                    View child = calendarGrid.getChildAt(i);
                    ViewGroup.LayoutParams lp = child.getLayoutParams();
                    if (lp.height != cellHeight) {
                        lp.height = cellHeight;
                        child.setLayoutParams(lp);
                    }
                }
            }
        });
    }

    private void updateActividadesDia() {
        String displayDate = capitalize(sdfDisplay.format(selectedDate.getTime()));
        tvFechaSeleccionada.setText(displayDate);

        String selectedDateStr = sdfFullDate.format(selectedDate.getTime());
        List<Actividad> delDia = new ArrayList<>();
        for (Actividad a : allActividades) {
            if (a.getFecha().equals(selectedDateStr)) {
                delDia.add(a);
            }
        }

        Collections.sort(delDia, (a, b) -> Integer.compare(b.getPrioridad(), a.getPrioridad()));

        actividadesDia.clear();
        actividadesDia.addAll(delDia);
        adapter.notifyDataSetChanged();

        tvSinActividades.setVisibility(actividadesDia.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarActividades();
    }
}
