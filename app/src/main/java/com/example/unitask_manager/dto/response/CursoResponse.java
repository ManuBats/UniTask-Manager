package com.example.unitask_manager.dto.response;

import com.google.gson.annotations.SerializedName;

public class CursoResponse {

    @SerializedName("id")
    private long id;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("profesor")
    private String profesor;

    @SerializedName("color")
    private String color;

    @SerializedName("horario")
    private String horario;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("pendientes")
    private int pendientes;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getProfesor() { return profesor; }
    public void setProfesor(String profesor) { this.profesor = profesor; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getPendientes() { return pendientes; }
    public void setPendientes(int pendientes) { this.pendientes = pendientes; }
}
