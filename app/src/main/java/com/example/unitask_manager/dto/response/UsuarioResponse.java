package com.example.unitask_manager.dto.response;

import com.google.gson.annotations.SerializedName;

public class UsuarioResponse {

    @SerializedName("id")
    private long id;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("email")
    private String email;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
