package com.example.unitask_manager.data.repository;

import com.example.unitask_manager.data.local.TokenManager;
import com.example.unitask_manager.database.DatabaseHelper;
import com.example.unitask_manager.dto.request.CreateActividadRequest;
import com.example.unitask_manager.dto.response.ActividadResponse;
import com.example.unitask_manager.models.Actividad;
import com.example.unitask_manager.network.ApiClient;
import com.example.unitask_manager.network.ApiService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActividadRepository {

    private final ApiService apiService;
    private final DatabaseHelper dbHelper;

    public ActividadRepository(DatabaseHelper dbHelper, TokenManager tokenManager) {
        this.apiService = ApiClient.getApiService(tokenManager);
        this.dbHelper = dbHelper;
    }

    public void getActividades(final ActividadesCallback callback) {
        apiService.getActividades(null, null, null, null, null, null, null, null)
                .enqueue(new Callback<List<ActividadResponse>>() {
            @Override
            public void onResponse(Call<List<ActividadResponse>> call, Response<List<ActividadResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(mapToModelList(response.body()));
                } else {
                    fallbackToLocal(callback);
                }
            }

            @Override
            public void onFailure(Call<List<ActividadResponse>> call, Throwable t) {
                fallbackToLocal(callback);
            }
        });
    }

    public void getActividadesByCurso(long cursoId, final ActividadesCallback callback) {
        apiService.getActividades(null, null, null, cursoId, null, null, null, null)
                .enqueue(new Callback<List<ActividadResponse>>() {
            @Override
            public void onResponse(Call<List<ActividadResponse>> call, Response<List<ActividadResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(mapToModelList(response.body()));
                } else {
                    callback.onSuccess(dbHelper.obtenerActividadesPorCursoId(cursoId));
                }
            }

            @Override
            public void onFailure(Call<List<ActividadResponse>> call, Throwable t) {
                callback.onSuccess(dbHelper.obtenerActividadesPorCursoId(cursoId));
            }
        });
    }

    public void createActividad(Actividad actividad, final ActividadCallback callback) {
        CreateActividadRequest request = new CreateActividadRequest(
                actividad.getIdCurso(), actividad.getTitulo(), actividad.getTipo(),
                actividad.getFecha(), actividad.getHora(), actividad.getPrioridad(),
                actividad.getDescripcion());

        apiService.createActividad(request).enqueue(new Callback<ActividadResponse>() {
            @Override
            public void onResponse(Call<ActividadResponse> call, Response<ActividadResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    actividad.setId(response.body().getId());
                }
                long id = dbHelper.insertarActividad(actividad);
                if (actividad.getId() <= 0) actividad.setId(id);
                if (callback != null) callback.onSuccess(actividad);
            }

            @Override
            public void onFailure(Call<ActividadResponse> call, Throwable t) {
                long id = dbHelper.insertarActividad(actividad);
                actividad.setId(id);
                if (callback != null) callback.onSuccess(actividad);
            }
        });
    }

    public void updateActividad(Actividad actividad, final ActividadCallback callback) {
        Map<String, Object> body = new HashMap<>();
        body.put("titulo", actividad.getTitulo());
        body.put("tipo", actividad.getTipo());
        body.put("fecha", actividad.getFecha());
        body.put("hora", actividad.getHora());
        body.put("prioridad", actividad.getPrioridad());
        body.put("descripcion", actividad.getDescripcion());
        body.put("id_curso", actividad.getIdCurso() > 0 ? actividad.getIdCurso() : -1);
        body.put("completada", actividad.isCompletada() ? 1 : 0);

        apiService.updateActividad(actividad.getId(), body).enqueue(new Callback<ActividadResponse>() {
            @Override
            public void onResponse(Call<ActividadResponse> call, Response<ActividadResponse> response) {
                dbHelper.actualizarActividad(actividad);
                if (callback != null) callback.onSuccess(actividad);
            }

            @Override
            public void onFailure(Call<ActividadResponse> call, Throwable t) {
                dbHelper.actualizarActividad(actividad);
                if (callback != null) callback.onSuccess(actividad);
            }
        });
    }

    public void toggleCompletada(Actividad actividad, final ActividadCallback callback) {
        int nuevoEstado = actividad.isCompletada() ? 0 : 1;
        actividad.setCompletada(nuevoEstado == 1);

        Map<String, Object> body = new HashMap<>();
        body.put("completada", nuevoEstado);

        apiService.updateActividad(actividad.getId(), body).enqueue(new Callback<ActividadResponse>() {
            @Override
            public void onResponse(Call<ActividadResponse> call, Response<ActividadResponse> response) {
                dbHelper.marcarCompletada(actividad.getId(), actividad.isCompletada());
                if (callback != null) callback.onSuccess(actividad);
            }

            @Override
            public void onFailure(Call<ActividadResponse> call, Throwable t) {
                dbHelper.marcarCompletada(actividad.getId(), actividad.isCompletada());
                if (callback != null) callback.onSuccess(actividad);
            }
        });
    }

    public void deleteActividad(long id, final VoidCallback callback) {
        apiService.deleteActividad(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                dbHelper.eliminarActividad(id);
                if (callback != null) callback.onDone();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                dbHelper.eliminarActividad(id);
                if (callback != null) callback.onDone();
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
            a.setFecha(r.getFecha());
            a.setHora(r.getHora());
            a.setPrioridad(r.getPrioridad());
            a.setDescripcion(r.getDescripcion());
            a.setCompletada(r.getCompletada() == 1);
            list.add(a);
        }
        return list;
    }

    private void fallbackToLocal(ActividadesCallback callback) {
        List<Actividad> actividades = dbHelper.obtenerActividades();
        callback.onSuccess(actividades);
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
