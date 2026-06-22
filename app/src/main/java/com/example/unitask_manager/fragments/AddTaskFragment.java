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
import com.example.unitask_manager.data.local.TokenManager;
import com.example.unitask_manager.data.repository.ActividadRepository;
import com.example.unitask_manager.data.repository.CursoRepository;
import com.example.unitask_manager.models.Actividad;
import com.example.unitask_manager.models.Curso;
import com.example.unitask_manager.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddTaskFragment extends Fragment {

    private EditText etTitulo, etDescripcion;
    private Spinner spinnerTipo, spinnerCurso;
    private TextView tvFecha, tvHora, tvTituloHeader;
    private View btnPrioridadAlta, btnPrioridadMedia, btnPrioridadBaja;
    private View btnGuardar, btnBack;

    private int prioridadSeleccionada = Actividad.PRIORIDAD_MEDIA;
    private String fechaSeleccionada = "";
    private String horaSeleccionada = "";
    private List<Curso> listaCursos = new ArrayList<>();
    private Long editingActividadId = null;

    private ActividadRepository actividadRepository;
    private CursoRepository cursoRepository;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_task, container, false);

        TokenManager tokenManager = new TokenManager(getContext());
        actividadRepository = new ActividadRepository(tokenManager);
        cursoRepository = new CursoRepository(tokenManager);

        initViews(view);
        configurarSpinners();
        configurarListeners();
        seleccionarPrioridad(btnPrioridadMedia);

        if (getArguments() != null && getArguments().containsKey("actividad_id")) {
            editingActividadId = getArguments().getLong("actividad_id");
            cargarActividadDesdeBundle();
        }

        return view;
    }

    private void initViews(View view) {
        tvTituloHeader = view.findViewById(R.id.tv_titulo_header);
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
        btnBack = view.findViewById(R.id.btn_back_add_task);
    }

    private void cargarActividadDesdeBundle() {
        Bundle args = getArguments();
        if (args == null) return;

        tvTituloHeader.setText("Editar actividad");
        ((TextView) btnGuardar).setText("Actualizar actividad");

        etTitulo.setText(args.getString("actividad_titulo", ""));
        etDescripcion.setText(args.getString("actividad_descripcion", ""));

        String tipo = args.getString("actividad_tipo", "");
        String[] tipos = Constants.TIPOS_ACTIVIDAD;
        for (int i = 0; i < tipos.length; i++) {
            if (tipos[i].equalsIgnoreCase(tipo)) {
                spinnerTipo.setSelection(i);
                break;
            }
        }

        fechaSeleccionada = args.getString("actividad_fecha", "");
        tvFecha.setText(fechaSeleccionada);
        tvFecha.setTextColor(getResources().getColor(R.color.text_primary, null));

        horaSeleccionada = args.getString("actividad_hora", "");
        if (horaSeleccionada != null && !horaSeleccionada.isEmpty()) {
            tvHora.setText(horaSeleccionada);
            tvHora.setTextColor(getResources().getColor(R.color.text_primary, null));
        }

        prioridadSeleccionada = args.getInt("actividad_prioridad", Actividad.PRIORIDAD_MEDIA);
        switch (prioridadSeleccionada) {
            case Actividad.PRIORIDAD_ALTA:
                seleccionarPrioridad(btnPrioridadAlta);
                break;
            case Actividad.PRIORIDAD_MEDIA:
                seleccionarPrioridad(btnPrioridadMedia);
                break;
            case Actividad.PRIORIDAD_BAJA:
                seleccionarPrioridad(btnPrioridadBaja);
                break;
        }

        long idCurso = args.getLong("actividad_id_curso", -1);
        if (idCurso != -1 && listaCursos != null) {
            for (int i = 0; i < listaCursos.size(); i++) {
                if (listaCursos.get(i).getId() == idCurso) {
                    spinnerCurso.setSelection(i);
                    break;
                }
            }
        }
    }

    private void configurarSpinners() {
        ArrayAdapter<String> tipoAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, Constants.TIPOS_ACTIVIDAD);
        spinnerTipo.setAdapter(tipoAdapter);

        cargarCursos();
    }

    private void cargarCursos() {
        cursoRepository.getCursos(new CursoRepository.CursosCallback() {
            @Override
            public void onSuccess(List<Curso> cursos) {
                listaCursos = cursos;
                String[] nombresCursos = new String[listaCursos.size()];
                for (int i = 0; i < listaCursos.size(); i++) {
                    nombresCursos[i] = listaCursos.get(i).getNombre();
                }
                if (nombresCursos.length == 0) {
                    nombresCursos = new String[]{"Sin cursos — crea uno en la pesta\u00f1a Cursos"};
                }
                ArrayAdapter<String> cursoAdapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_spinner_dropdown_item, nombresCursos);
                spinnerCurso.setAdapter(cursoAdapter);

                long cursoId = getArguments() != null ? getArguments().getLong("curso_id", -1) : -1;
                if (cursoId != -1) {
                    for (int i = 0; i < listaCursos.size(); i++) {
                        if (listaCursos.get(i).getId() == cursoId) {
                            spinnerCurso.setSelection(i);
                            break;
                        }
                    }
                }

                if (editingActividadId != null) {
                    long idCursoArg = getArguments() != null ? getArguments().getLong("actividad_id_curso", -1) : -1;
                    if (idCursoArg != -1) {
                        for (int i = 0; i < listaCursos.size(); i++) {
                            if (listaCursos.get(i).getId() == idCursoArg) {
                                spinnerCurso.setSelection(i);
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configurarListeners() {
        tvFecha.setOnClickListener(v -> mostrarDatePicker());
        tvHora.setOnClickListener(v -> mostrarTimePicker());

        btnPrioridadAlta.setOnClickListener(v -> seleccionarPrioridad(btnPrioridadAlta));
        btnPrioridadMedia.setOnClickListener(v -> seleccionarPrioridad(btnPrioridadMedia));
        btnPrioridadBaja.setOnClickListener(v -> seleccionarPrioridad(btnPrioridadBaja));

        btnGuardar.setOnClickListener(v -> guardarActividad());
        btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
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
        SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy", new Locale("es", "ES"));
        DatePickerDialog picker = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth);
                    fechaSeleccionada = sdf.format(selected.getTime());
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

        if (editingActividadId != null) {
            actualizarActividad(titulo, tipo, descripcion, idCurso);
        } else {
            crearActividad(titulo, tipo, descripcion, idCurso);
        }
    }

    private void crearActividad(String titulo, String tipo, String descripcion, long idCurso) {
        if (listaCursos == null || listaCursos.isEmpty()) {
            Toast.makeText(getContext(), "Debes crear un curso primero en la pesta\u00f1a Cursos", Toast.LENGTH_SHORT).show();
            return;
        }

        Actividad actividad = new Actividad(titulo, tipo, fechaSeleccionada, horaSeleccionada,
                prioridadSeleccionada, descripcion, idCurso);

        actividadRepository.createActividad(actividad, new ActividadRepository.ActividadCallback() {
            @Override
            public void onSuccess(Actividad actividad) {
                Toast.makeText(getContext(), "Actividad guardada", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarActividad(String titulo, String tipo, String descripcion, long idCurso) {
        Actividad actividad = new Actividad(titulo, tipo, fechaSeleccionada, horaSeleccionada,
                prioridadSeleccionada, descripcion, idCurso);
        actividad.setId(editingActividadId);

        Bundle args = getArguments();
        boolean completada = args != null && args.getBoolean("actividad_completada", false);
        actividad.setCompletada(completada);

        actividadRepository.updateActividad(actividad, new ActividadRepository.ActividadCallback() {
            @Override
            public void onSuccess(Actividad actividad) {
                Toast.makeText(getContext(), "Actividad actualizada", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
