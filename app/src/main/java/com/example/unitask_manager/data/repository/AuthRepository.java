package com.example.unitask_manager.data.repository;

import android.content.Context;

import com.example.unitask_manager.data.local.TokenManager;
import com.example.unitask_manager.dto.request.LoginRequest;
import com.example.unitask_manager.dto.request.RegisterRequest;
import com.example.unitask_manager.dto.response.AuthResponse;
import com.example.unitask_manager.dto.response.UsuarioResponse;
import com.example.unitask_manager.models.Usuario;
import com.example.unitask_manager.network.ApiClient;
import com.example.unitask_manager.network.ApiService;
import com.example.unitask_manager.settings.SettingsPreferenceManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    private final ApiService apiService;
    private final TokenManager tokenManager;
    private final SettingsPreferenceManager settingsPrefs;

    public AuthRepository(Context context, TokenManager tokenManager) {
        this.apiService = ApiClient.getApiService(tokenManager);
        this.tokenManager = tokenManager;
        this.settingsPrefs = new SettingsPreferenceManager(context);
    }

    public void login(String email, String contrasena, final AuthCallback callback) {
        LoginRequest request = new LoginRequest(email, contrasena);
        apiService.login(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tokenManager.saveToken(response.body().getToken());
                    UsuarioResponse userResp = response.body().getUsuario();
                    saveUserPrefs(userResp);
                    Usuario usuario = new Usuario(userResp.getId(), userResp.getNombre(), userResp.getEmail(), contrasena);
                    callback.onSuccess(usuario);
                } else {
                    callback.onError("Correo o contraseña incorrectos");
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                callback.onError("Error de conexión: " + t.getMessage());
            }
        });
    }

    public void register(String nombre, String email, String contrasena, final AuthCallback callback) {
        RegisterRequest request = new RegisterRequest(nombre, email, contrasena);
        apiService.register(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tokenManager.saveToken(response.body().getToken());
                    UsuarioResponse userResp = response.body().getUsuario();
                    saveUserPrefs(userResp);
                    Usuario usuario = new Usuario(userResp.getId(), userResp.getNombre(), userResp.getEmail(), contrasena);
                    callback.onSuccess(usuario);
                } else {
                    callback.onError("Error al registrarse");
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                callback.onError("Error de conexión: " + t.getMessage());
            }
        });
    }

    public void logout() {
        tokenManager.clearSession();
        settingsPrefs.clearUserPrefs();
    }

    public boolean isLoggedIn() {
        return tokenManager.hasToken();
    }

    private void saveUserPrefs(UsuarioResponse userResp) {
        settingsPrefs.setUserName(userResp.getNombre());
        settingsPrefs.setUserEmail(userResp.getEmail());
    }

    public interface AuthCallback {
        void onSuccess(Usuario usuario);
        void onError(String error);
    }
}
