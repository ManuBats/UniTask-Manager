package com.example.unitask_manager.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.unitask_manager.R;
import com.example.unitask_manager.database.DatabaseHelper;
import com.example.unitask_manager.models.Actividad;
import com.example.unitask_manager.models.Curso;
import com.example.unitask_manager.utils.Constants;

import java.util.Calendar;
import java.util.List;

public class AddTaskFragment extends Fragment {

    private EditText etTitulo, etDescripcion;
    private Spinner spinnerTipo, spinnerCurso;
    private TextView tvFecha, tvHora;
    private View btnPrioridadAlta, btnPrioridadMedia, btnPrioridadBaja;
    private View btnGuardar;

    private int prioridadSeleccionada = Actividad.PRIORIDAD_MEDIA;
    private String fechaSeleccionada = "";
    private String horaSeleccionada = "";
    private DatabaseHelper dbHelper;
    private List<Curso> listaCursos;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_task, container, false);
        dbHelper = new DatabaseHelper(getContext());
        initViews(view);
        configurarSpinners();
        configurarListeners();
        seleccionarPrioridad(btnPrioridadMedia);
        return view;
    }

    private void initViews(View view) {
        etTitulo = view.findViewById(R.id.et_titulo);
        etDescripcion = view.findViewById(R.id.et_descripcion);
        spinnerTipo = view.findViewById(R.id.spinner_tipo);
        spinnerCurso = view.findViewById(R.id.spinner_curso);
        tvFecha = view.findViewById(R.id.tv_fecha);
        tvHora = view.findViewById(R.id.tv_hora);
        btnPrioridadAlta = view.findViewById(R.id.btn_prioridad_alta);
        btnPrioridadMedia = view.findViewById(R.id.btn_prioridad_media);
        btnPrioridadBaja = view.findViewById(R.id.btn_prioridad_baja);
        btnGuardar = view.findViewById(R.id.btn_guardar);
    }

    private void configurarSpinners() {
        ArrayAdapter<String> tipoAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, Constants.TIPOS_ACTIVIDAD);
        spinnerTipo.setAdapter(tipoAdapter);

        cargarCursos();
    }

    private void cargarCursos() {
        listaCursos = dbHelper.obtenerCursos();
        String[] nombresCursos = new String[listaCursos.size()];
        for (int i = 0; i < listaCursos.size(); i++) {
            nombresCursos[i] = listaCursos.get(i).getNombre();
        }
        if (nombresCursos.length == 0) {
            nombresCursos = new String[]{"Sin cursos — crea uno en la pestaña Cursos"};
        }
        ArrayAdapter<String> cursoAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, nombresCursos);
        spinnerCurso.setAdapter(cursoAdapter);
    }

    private void configurarListeners() {
        tvFecha.setOnClickListener(v -> mostrarDatePicker());
        tvHora.setOnClickListener(v -> mostrarTimePicker());

        btnPrioridadAlta.setOnClickListener(v -> seleccionarPrioridad(btnPrioridadAlta));
        btnPrioridadMedia.setOnClickListener(v -> seleccionarPrioridad(btnPrioridadMedia));
        btnPrioridadBaja.setOnClickListener(v -> seleccionarPrioridad(btnPrioridadBaja));

        btnGuardar.setOnClickListener(v -> guardarActividad());
    }

    private void seleccionarPrioridad(View seleccionado) {
        btnPrioridadAlta.setBackgroundResource(R.drawable.bg_priority_high);
        btnPrioridadMedia.setBackgroundResource(R.drawable.bg_priority_medium);
        btnPrioridadBaja.setBackgroundResource(R.drawable.bg_priority_low);

        seleccionado.setBackgroundResource(R.drawable.bg_priority_selected);

        if (seleccionado == btnPrioridadAlta) {
            prioridadSeleccionada = Actividad.PRIORIDAD_ALTA;
        } else if (seleccionado == btnPrioridadMedia) {
            prioridadSeleccionada = Actividad.PRIORIDAD_MEDIA;
        } else if (seleccionado == btnPrioridadBaja) {
            prioridadSeleccionada = Actividad.PRIORIDAD_BAJA;
        }
    }

    private void mostrarDatePicker() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog picker = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {
                    String mes = (month + 1) < 10 ? "0" + (month + 1) : String.valueOf(month + 1);
                    String dia = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
                    fechaSeleccionada = dia + "/" + mes + "/" + year;
                    tvFecha.setText(fechaSeleccionada);
                    tvFecha.setTextColor(getResources().getColor(R.color.text_primary, null));
                },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        picker.show();
    }

    private void mostrarTimePicker() {
        Calendar cal = Calendar.getInstance();
        TimePickerDialog picker = new TimePickerDialog(getContext(),
                (view, hourOfDay, minute) -> {
                    String h = hourOfDay < 10 ? "0" + hourOfDay : String.valueOf(hourOfDay);
                    String m = minute < 10 ? "0" + minute : String.valueOf(minute);
                    horaSeleccionada = h + ":" + m;
                    tvHora.setText(horaSeleccionada);
                    tvHora.setTextColor(getResources().getColor(R.color.text_primary, null));
                },
                cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
        picker.show();
    }

    private void guardarActividad() {
        String titulo = etTitulo.getText().toString().trim();
        if (titulo.isEmpty()) {
            etTitulo.setError("El titulo es obligatorio");
            return;
        }

        String tipo = spinnerTipo.getSelectedItem() != null
                ? spinnerTipo.getSelectedItem().toString() : Constants.TIPOS_ACTIVIDAD[0];

        if (fechaSeleccionada.isEmpty()) {
            Toast.makeText(getContext(), "Selecciona una fecha", Toast.LENGTH_SHORT).show();
            return;
        }

        if (horaSeleccionada.isEmpty()) {
            Toast.makeText(getContext(), "Selecciona una hora", Toast.LENGTH_SHORT).show();
            return;
        }

        String descripcion = etDescripcion.getText().toString().trim();

        long idCurso = -1;
        if (spinnerCurso.getSelectedItem() != null && listaCursos != null && !listaCursos.isEmpty()) {
            int pos = spinnerCurso.getSelectedItemPosition();
            if (pos >= 0 && pos < listaCursos.size()) {
                idCurso = listaCursos.get(pos).getId();
            }
        }

        Actividad actividad = new Actividad(titulo, tipo, fechaSeleccionada, horaSeleccionada,
                prioridadSeleccionada, descripcion, idCurso);

        long resultado = dbHelper.insertarActividad(actividad);
        if (resultado != -1) {
            Toast.makeText(getContext(), "Actividad guardada exitosamente", Toast.LENGTH_SHORT).show();
            limpiarFormulario();
        } else {
            Toast.makeText(getContext(), "Error al guardar la actividad", Toast.LENGTH_SHORT).show();
        }
    }

    private void limpiarFormulario() {
        etTitulo.setText("");
        etDescripcion.setText("");
        spinnerTipo.setSelection(0);
        fechaSeleccionada = "";
        horaSeleccionada = "";
        tvFecha.setText("Seleccionar fecha");
        tvFecha.setTextColor(getResources().getColor(R.color.text_secondary, null));
        tvHora.setText("Seleccionar hora");
        tvHora.setTextColor(getResources().getColor(R.color.text_secondary, null));
        seleccionarPrioridad(btnPrioridadMedia);
    }
}
