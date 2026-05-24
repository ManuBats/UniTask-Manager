package com.example.unitask_manager.network;

import com.example.unitask_manager.dto.request.CreateActividadRequest;
import com.example.unitask_manager.dto.request.LoginRequest;
import com.example.unitask_manager.dto.request.RegisterRequest;
import com.example.unitask_manager.dto.response.ActividadResponse;
import com.example.unitask_manager.dto.response.AuthResponse;
import com.example.unitask_manager.dto.response.CursoResponse;
import com.example.unitask_manager.dto.response.StatsResponse;
import com.example.unitask_manager.dto.response.UsuarioResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // Auth
    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<AuthResponse> register(@Body RegisterRequest request);

    @GET("auth/me")
    Call<UsuarioResponse> getCurrentUser();

    @PUT("auth/me")
    Call<UsuarioResponse> updateUser(@Body UsuarioResponse usuario);

    @PUT("auth/password")
    Call<Void> changePassword(@Body Map<String, String> passwordBody);

    // Cursos
    @GET("cursos")
    Call<List<CursoResponse>> getCursos();

    @POST("cursos")
    Call<CursoResponse> createCurso(@Body CursoResponse curso);

    @PATCH("cursos/{id}")
    Call<CursoResponse> updateCurso(@Path("id") long id, @Body CursoResponse curso);

    @DELETE("cursos/{id}")
    Call<Void> deleteCurso(@Path("id") long id);

    // Actividades
    @GET("actividades")
    Call<List<ActividadResponse>> getActividades(
            @Query("fecha") String fecha,
            @Query("prioridad") Integer prioridad,
            @Query("completada") Integer completada,
            @Query("id_curso") Long idCurso,
            @Query("fechaInicio") String fechaInicio,
            @Query("fechaFin") String fechaFin,
            @Query("orderBy") String orderBy,
            @Query("limit") Integer limit
    );

    @POST("actividades")
    Call<ActividadResponse> createActividad(@Body CreateActividadRequest request);

    @PATCH("actividades/{id}")
    Call<ActividadResponse> updateActividad(@Path("id") long id, @Body Map<String, Object> body);

    @DELETE("actividades/{id}")
    Call<Void> deleteActividad(@Path("id") long id);

    // Stats
    @GET("stats/dashboard")
    Call<StatsResponse> getDashboardStats();
}
