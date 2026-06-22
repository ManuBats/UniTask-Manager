package com.example.unitask_manager.dto.response;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {

    @SerializedName("token")
    private String token;

    @SerializedName("usuario")
    private UsuarioResponse usuario;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public UsuarioResponse getUsuario() { return usuario; }
    public void setUsuario(UsuarioResponse usuario) { this.usuario = usuario; }
}
