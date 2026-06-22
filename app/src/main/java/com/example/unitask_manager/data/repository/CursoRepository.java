package com.example.unitask_manager.data.repository;

import com.example.unitask_manager.data.local.TokenManager;
import com.example.unitask_manager.dto.response.CursoResponse;
import com.example.unitask_manager.models.Curso;
import com.example.unitask_manager.network.ApiClient;
import com.example.unitask_manager.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CursoRepository {

    private final ApiService apiService;

    public CursoRepository(TokenManager tokenManager) {
        this.apiService = ApiClient.getApiService(tokenManager);
    }

    public void getCursos(final CursosCallback callback) {
        apiService.getCursos().enqueue(new Callback<List<CursoResponse>>() {
            @Override
            public void onResponse(Call<List<CursoResponse>> call, Response<List<CursoResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Curso> cursos = new ArrayList<>();
                    for (CursoResponse cr : response.body()) {
                        Curso curso = new Curso(cr.getId(), cr.getNombre(), cr.getProfesor(),
                                cr.getColor(), cr.getPendientes(), cr.getHorario(), 0, cr.getDescripcion());
                        cursos.add(curso);
                    }
                    callback.onSuccess(cursos);
                } else {
                    callback.onError("Error al obtener cursos: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<CursoResponse>> call, Throwable t) {
                callback.onError("Error de conexión: " + t.getMessage());
            }
        });
    }

    public void createCurso(Curso curso, final CursoCallback callback) {
        CursoResponse request = new CursoResponse();
        request.setNombre(curso.getNombre());
        request.setProfesor(curso.getProfesor());
        request.setColor(curso.getColor());
        request.setHorario(curso.getHorario());
        request.setDescripcion(curso.getDescripcion());

        apiService.createCurso(request).enqueue(new Callback<CursoResponse>() {
            @Override
            public void onResponse(Call<CursoResponse> call, Response<CursoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CursoResponse cr = response.body();
                    curso.setId(cr.getId());
                    curso.setPendientes(cr.getPendientes());
                    callback.onSuccess(curso);
                } else {
                    callback.onError("Error al crear curso: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<CursoResponse> call, Throwable t) {
                callback.onError("Error de conexión: " + t.getMessage());
            }
        });
    }

    public void updateCurso(Curso curso, final CursoCallback callback) {
        CursoResponse request = new CursoResponse();
        request.setNombre(curso.getNombre());
        request.setProfesor(curso.getProfesor());
        request.setColor(curso.getColor());
        request.setHorario(curso.getHorario());
        request.setDescripcion(curso.getDescripcion());

        apiService.updateCurso(curso.getId(), request).enqueue(new Callback<CursoResponse>() {
            @Override
            public void onResponse(Call<CursoResponse> call, Response<CursoResponse> response) {
                if (response.isSuccessful()) {
                    if (callback != null) callback.onSuccess(curso);
                } else {
                    if (callback != null) callback.onError("Error al actualizar curso: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<CursoResponse> call, Throwable t) {
                if (callback != null) callback.onError("Error de conexión: " + t.getMessage());
            }
        });
    }

    public void deleteCurso(long id, final VoidCallback callback) {
        apiService.deleteCurso(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    if (callback != null) callback.onDone();
                } else {
                    if (callback != null) callback.onError("Error al eliminar curso: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (callback != null) callback.onError("Error de conexión: " + t.getMessage());
            }
        });
    }

    public interface CursosCallback {
        void onSuccess(List<Curso> cursos);
        void onError(String error);
    }

    public interface CursoCallback {
        void onSuccess(Curso curso);
        void onError(String error);
    }

    public interface VoidCallback {
        void onDone();
        void onError(String error);
    }
}
