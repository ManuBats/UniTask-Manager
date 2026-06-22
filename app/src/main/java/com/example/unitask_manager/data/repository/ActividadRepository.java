package com.example.unitask_manager.data.repository;

import com.example.unitask_manager.data.local.TokenManager;
import com.example.unitask_manager.dto.request.CreateActividadRequest;
import com.example.unitask_manager.dto.response.ActividadResponse;
import com.example.unitask_manager.models.Actividad;
import com.example.unitask_manager.network.ApiClient;
import com.example.unitask_manager.network.ApiService;
import com.example.unitask_manager.utils.DateUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActividadRepository {

    private final ApiService apiService;

    public ActividadRepository(TokenManager tokenManager) {
        this.apiService = ApiClient.getApiService(tokenManager);
    }

    public void getActividades(String fecha, Integer prioridad, Integer completada,
                               Long idCurso, String fechaInicio, String fechaFin,
                               String orderBy, Integer limit,
                               final ActividadesCallback callback) {
        String apiFecha = (fecha != null && !fecha.isEmpty()) ? DateUtils.toApi(fecha) : null;
        String apiFechaInicio = (fechaInicio != null && !fechaInicio.isEmpty()) ? DateUtils.toApi(fechaInicio) : null;
        String apiFechaFin = (fechaFin != null && !fechaFin.isEmpty()) ? DateUtils.toApi(fechaFin) : null;

        apiService.getActividades(apiFecha, prioridad, completada, idCurso,
                        apiFechaInicio, apiFechaFin, orderBy, limit)
                .enqueue(new Callback<List<ActividadResponse>>() {
                    @Override
                    public void onResponse(Call<List<ActividadResponse>> call, Response<List<ActividadResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(mapToModelList(response.body()));
                        } else {
                            callback.onError("Error al obtener actividades: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ActividadResponse>> call, Throwable t) {
                        callback.onError("Error de conexi\u00f3n: " + t.getMessage());
                    }
                });
    }

    public void getActividades(final ActividadesCallback callback) {
        getActividades(null, null, null, null, null, null, null, null, callback);
    }

    public void getActividadesByFecha(String fechaDisplay, final ActividadesCallback callback) {
        getActividades(fechaDisplay, null, null, null, null, null, null, null, callback);
    }

    public void getActividadesByCurso(long cursoId, final ActividadesCallback callback) {
        getActividades(null, null, null, cursoId, null, null, null, null, callback);
    }

    public void getActividadesByRango(String fechaInicioDisplay, String fechaFinDisplay,
                                      final ActividadesCallback callback) {
        getActividades(null, null, null, null, fechaInicioDisplay, fechaFinDisplay, null, null, callback);
    }

    public void createActividad(Actividad actividad, final ActividadCallback callback) {
        String apiFecha = DateUtils.toApi(actividad.getFecha());
        CreateActividadRequest request = new CreateActividadRequest(
                actividad.getIdCurso(), actividad.getTitulo(), actividad.getTipo(),
                apiFecha, actividad.getHora(), actividad.getPrioridad(),
                actividad.getDescripcion());

        apiService.createActividad(request).enqueue(new Callback<ActividadResponse>() {
            @Override
            public void onResponse(Call<ActividadResponse> call, Response<ActividadResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    actividad.setId(response.body().getId());
                    if (callback != null) callback.onSuccess(actividad);
                } else {
                    if (callback != null) callback.onError("Error al crear actividad: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ActividadResponse> call, Throwable t) {
                if (callback != null) callback.onError("Error de conexi\u00f3n: " + t.getMessage());
            }
        });
    }

    public void updateActividad(Actividad actividad, final ActividadCallback callback) {
        Map<String, Object> body = new HashMap<>();
        body.put("titulo", actividad.getTitulo());
        body.put("tipo", actividad.getTipo());
        body.put("fecha", DateUtils.toApi(actividad.getFecha()));
        body.put("hora", actividad.getHora());
        body.put("prioridad", actividad.getPrioridad());
        body.put("descripcion", actividad.getDescripcion());
        body.put("id_curso", actividad.getIdCurso() > 0 ? actividad.getIdCurso() : -1);
        body.put("completada", actividad.isCompletada());

        apiService.updateActividad(actividad.getId(), body).enqueue(new Callback<ActividadResponse>() {
            @Override
            public void onResponse(Call<ActividadResponse> call, Response<ActividadResponse> response) {
                if (response.isSuccessful()) {
                    if (callback != null) callback.onSuccess(actividad);
                } else {
                    if (callback != null) callback.onError("Error al actualizar actividad: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ActividadResponse> call, Throwable t) {
                if (callback != null) callback.onError("Error de conexi\u00f3n: " + t.getMessage());
            }
        });
    }

    public void toggleCompletada(Actividad actividad, final ActividadCallback callback) {
        boolean nuevoEstado = !actividad.isCompletada();

        Map<String, Object> body = new HashMap<>();
        body.put("completada", nuevoEstado);

        apiService.updateActividad(actividad.getId(), body).enqueue(new Callback<ActividadResponse>() {
            @Override
            public void onResponse(Call<ActividadResponse> call, Response<ActividadResponse> response) {
                if (response.isSuccessful()) {
                    actividad.setCompletada(nuevoEstado);
                    if (callback != null) callback.onSuccess(actividad);
                } else {
                    if (callback != null) callback.onError("Error al cambiar estado: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ActividadResponse> call, Throwable t) {
                if (callback != null) callback.onError("Error de conexi\u00f3n: " + t.getMessage());
            }
        });
    }

    public void deleteActividad(long id, final VoidCallback callback) {
        apiService.deleteActividad(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    if (callback != null) callback.onDone();
                } else {
                    if (callback != null) callback.onError("Error al eliminar actividad: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (callback != null) callback.onError("Error de conexi\u00f3n: " + t.getMessage());
            }
        });
    }

    private List<Actividad> mapToModelList(List<ActividadResponse> responses) {
        List<Actividad> list = new ArrayList<>();
        for (ActividadResponse r : responses) {
            Actividad a = new Actividad();
            a.setId(r.getId());
            a.setIdCurso(r.getIdCurso() != null ? r.getIdCurso() : -1);
            a.setTitulo(r.getTitulo());
            a.setTipo(r.getTipo());
            a.setFecha(DateUtils.toDisplay(r.getFecha()));
            a.setHora(r.getHora());
            a.setPrioridad(r.getPrioridad());
            a.setDescripcion(r.getDescripcion());
            a.setCompletada(r.getCompletada() == 1);
            list.add(a);
        }
        return list;
    }

    public interface ActividadesCallback {
        void onSuccess(List<Actividad> actividades);
        void onError(String error);
    }

    public interface ActividadCallback {
        void onSuccess(Actividad actividad);
        void onError(String error);
    }

    public interface VoidCallback {
        void onDone();
        void onError(String error);
    }
}
