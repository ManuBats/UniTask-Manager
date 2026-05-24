package com.example.unitask_manager.dto.request;

import com.google.gson.annotations.SerializedName;

public class CreateActividadRequest {

    @SerializedName("id_curso")
    private long idCurso;

    @SerializedName("titulo")
    private String titulo;

    @SerializedName("tipo")
    private String tipo;

    @SerializedName("fecha")
    private String fecha;

    @SerializedName("hora")
    private String hora;

    @SerializedName("prioridad")
    private int prioridad;

    @SerializedName("descripcion")
    private String descripcion;

    public CreateActividadRequest(long idCurso, String titulo, String tipo, String fecha, String hora, int prioridad, String descripcion) {
        this.idCurso = idCurso;
        this.titulo = titulo;
        this.tipo = tipo;
        this.fecha = fecha;
        this.hora = hora;
        this.prioridad = prioridad;
        this.descripcion = descripcion;
    }

    public long getIdCurso() { return idCurso; }
    public void setIdCurso(long idCurso) { this.idCurso = idCurso; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }

    public int getPrioridad() { return prioridad; }
    public void setPrioridad(int prioridad) { this.prioridad = prioridad; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
