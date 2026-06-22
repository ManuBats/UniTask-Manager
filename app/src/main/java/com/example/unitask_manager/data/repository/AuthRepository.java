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

    public void updateUserProfile(UsuarioResponse usuario, final AuthCallback callback) {
        apiService.updateUser(usuario).enqueue(new Callback<UsuarioResponse>() {
            @Override
            public void onResponse(Call<UsuarioResponse> call, Response<UsuarioResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UsuarioResponse updatedUser = response.body();

                    // CORRECCIÓN: Convertimos el ID numérico a String usando String.valueOf()
                    Usuario usuarioModel = new Usuario(
                            String.valueOf(updatedUser.getId()),
                            updatedUser.getNombre(),
                            updatedUser.getEmail()
                    );

                    callback.onSuccess(usuarioModel);
                } else {
                    callback.onError("Error al actualizar el perfil en el servidor");
                }
            }

            @Override
            public void onFailure(Call<UsuarioResponse> call, Throwable t) {
                callback.onError("Error de red: " + t.getMessage());
            }
        });
    }

    public void updatePassword(String passwordActual, String nuevaPassword, final ObjectCallback callback) {
        java.util.Map<String, String> body = new java.util.HashMap<>();
        body.put("passwordActual", passwordActual);
        body.put("nuevaPassword", nuevaPassword);

        apiService.changePassword(body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // CORRECCIÓN: Quitamos 'null' para cumplir con la firma del callback sin argumentos
                    callback.onSuccess();
                } else {
                    callback.onError("La contraseña actual es incorrecta o no cumple con los requisitos");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Error de red: " + t.getMessage());
            }
        });
    }

    // Interfaz de retorno requerida para controlar el resultado de la contraseña
    public interface ObjectCallback {
        void onSuccess();
        void onError(String error);
    }

    public interface AuthCallback {
        void onSuccess(Usuario usuario);
        void onError(String error);
    }
}
