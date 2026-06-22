package com.example.unitask_manager.dto.response;

import com.google.gson.annotations.SerializedName;

public class ActividadResponse {

    @SerializedName("id")
    private long id;

    @SerializedName("id_curso")
    private Long idCurso;

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

    @SerializedName("completada")
    private int completada;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public Long getIdCurso() { return idCurso; }
    public void setIdCurso(Long idCurso) { this.idCurso = idCurso; }

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

    public int getCompletada() { return completada; }
    public void setCompletada(int completada) { this.completada = completada; }
}
