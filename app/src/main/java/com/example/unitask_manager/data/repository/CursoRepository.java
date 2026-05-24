package com.example.unitask_manager.data.repository;

import com.example.unitask_manager.data.local.TokenManager;
import com.example.unitask_manager.database.DatabaseHelper;
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
    private final DatabaseHelper dbHelper;

    public CursoRepository(DatabaseHelper dbHelper, TokenManager tokenManager) {
        this.apiService = ApiClient.getApiService(tokenManager);
        this.dbHelper = dbHelper;
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
                    fallbackToLocal(callback);
                }
            }

            @Override
            public void onFailure(Call<List<CursoResponse>> call, Throwable t) {
                fallbackToLocal(callback);
            }
        });
    }

    private void fallbackToLocal(CursosCallback callback) {
        List<Curso> cursos = dbHelper.obtenerCursos();
        callback.onSuccess(cursos);
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
                    dbHelper.insertarCurso(curso);
                    callback.onSuccess(curso);
                } else {
                    long id = dbHelper.insertarCurso(curso);
                    curso.setId(id);
                    callback.onSuccess(curso);
                }
            }

            @Override
            public void onFailure(Call<CursoResponse> call, Throwable t) {
                long id = dbHelper.insertarCurso(curso);
                curso.setId(id);
                callback.onSuccess(curso);
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
                dbHelper.actualizarCurso(curso);
                if (callback != null) callback.onSuccess(curso);
            }

            @Override
            public void onFailure(Call<CursoResponse> call, Throwable t) {
                dbHelper.actualizarCurso(curso);
                if (callback != null) callback.onSuccess(curso);
            }
        });
    }

    public void deleteCurso(long id, final VoidCallback callback) {
        apiService.deleteCurso(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                dbHelper.eliminarCurso(id);
                if (callback != null) callback.onDone();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                dbHelper.eliminarCurso(id);
                if (callback != null) callback.onDone();
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
