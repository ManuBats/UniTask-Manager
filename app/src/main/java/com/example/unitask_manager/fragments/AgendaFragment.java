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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unitask_manager.R;
import com.example.unitask_manager.adapters.ActividadesAdapter;
import com.example.unitask_manager.models.Actividad;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AgendaFragment extends Fragment {

    private TextView tvMesAnio, tvFechaSeleccionada, tvSinActividades;
    private GridLayout calendarGrid;
    private RecyclerView rvActividadesDia;
    private ImageButton btnMesAnterior, btnMesSiguiente;

    private int currentYear, currentMonth;
    private final Calendar selectedDate = Calendar.getInstance();

    private ActividadesAdapter adapter;
    private final List<Actividad> actividadesDia = new ArrayList<>();

    private final SimpleDateFormat sdfMonthYear = new SimpleDateFormat("MMMM yyyy", new Locale("es", "ES"));
    private final SimpleDateFormat sdfFullDate = new SimpleDateFormat("d MMMM yyyy", new Locale("es", "ES"));
    private final SimpleDateFormat sdfDisplay = new SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));

    private int lastCellHeightPx;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agenda, container, false);
        initViews(view);
        initState();
        setupListeners();
        updateCalendar();
        updateActividadesDia();
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
        adapter = new ActividadesAdapter(actividadesDia, null);
        rvActividadesDia.setAdapter(adapter);
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

            TextView dayCell = new TextView(requireContext());
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = 0;
            lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            if (lastCellHeightPx > 0) {
                lp.height = lastCellHeightPx;
            }
            dayCell.setLayoutParams(lp);
            dayCell.setGravity(Gravity.CENTER);
            dayCell.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            dayCell.setText(String.valueOf(day));
            dayCell.setClickable(true);
            dayCell.setFocusable(true);

            boolean isToday = day == today.get(Calendar.DAY_OF_MONTH)
                    && currentMonth == today.get(Calendar.MONTH)
                    && currentYear == today.get(Calendar.YEAR);
            boolean isSelected = dateStr.equals(selectedDateStr);

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

            int selectedDay = day;
            dayCell.setOnClickListener(v -> {
                selectedDate.set(currentYear, currentMonth, selectedDay);
                updateCalendar();
                updateActividadesDia();
            });

            calendarGrid.addView(dayCell);
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
        List<Actividad> todas = getActividadesHardcodeadas();
        List<Actividad> delDia = new ArrayList<>();
        for (Actividad a : todas) {
            if (a.getFecha().equals(selectedDateStr)) {
                delDia.add(a);
            }
        }

        actividadesDia.clear();
        actividadesDia.addAll(delDia);
        adapter.notifyDataSetChanged();

        tvSinActividades.setVisibility(actividadesDia.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private List<Actividad> getActividadesHardcodeadas() {
        List<Actividad> lista = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);

        CalendarioHelper helper = new CalendarioHelper(year, month);

        lista.add(new Actividad("Estudiar Álgebra Lineal", "Examen",
                helper.obtenerFecha(5), "10:00 AM",
                Actividad.PRIORIDAD_ALTA, "Capítulos 3-5", 1));

        lista.add(new Actividad("Entregar Proyecto Programación", "Tarea",
                helper.obtenerFecha(5), "02:00 PM",
                Actividad.PRIORIDAD_MEDIA, "App Android", 2));

        lista.add(new Actividad("Preparar Exposición Historia", "Exposición",
                helper.obtenerFecha(7), "04:30 PM",
                Actividad.PRIORIDAD_BAJA, "Revolución Industrial", 3));

        lista.add(new Actividad("Leer Capítulo 5 Física", "Tarea",
                helper.obtenerFecha(5), "06:00 PM",
                Actividad.PRIORIDAD_ALTA, "Movimiento ondulatorio", 2));

        lista.add(new Actividad("Cuestionario Cálculo Diferencial", "Examen",
                helper.obtenerFecha(12), "08:00 AM",
                Actividad.PRIORIDAD_ALTA, "Derivadas parciales", 1));

        lista.add(new Actividad("Resumen Química Orgánica", "Tarea",
                helper.obtenerFecha(12), "11:30 AM",
                Actividad.PRIORIDAD_MEDIA, "Hidrocarburos", 3));

        lista.add(new Actividad("Exposición Literatura", "Exposición",
                helper.obtenerFecha(15), "03:00 PM",
                Actividad.PRIORIDAD_BAJA, "Realismo mágico", 2));

        lista.add(new Actividad("Practicar ejercicios SQL", "Tarea",
                helper.obtenerFecha(19), "09:00 AM",
                Actividad.PRIORIDAD_MEDIA, "Consultas complejas", 1));

        lista.add(new Actividad("Entregar Ensayo Filosofía", "Tarea",
                helper.obtenerFecha(22), "11:59 PM",
                Actividad.PRIORIDAD_ALTA, "Ética y moral", 3));

        lista.add(new Actividad("Repasar Trigonometría", "Tarea",
                helper.obtenerFecha(26), "05:00 PM",
                Actividad.PRIORIDAD_BAJA, "Funciones trigonométricas", 1));

        return lista;
    }

    private class CalendarioHelper {
        private final int year;
        private final int month;

        CalendarioHelper(int year, int month) {
            this.year = year;
            this.month = month;
        }

        String obtenerFecha(int day) {
            Calendar c = Calendar.getInstance();
            c.set(year, month, day);
            return sdfFullDate.format(c.getTime());
        }
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}
