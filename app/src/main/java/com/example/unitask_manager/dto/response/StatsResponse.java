package com.example.unitask_manager.dto.response;

import com.google.gson.annotations.SerializedName;

public class StatsResponse {

    @SerializedName("total_actividades")
    private int totalActividades;

    @SerializedName("completadas")
    private int completadas;

    @SerializedName("pendientes")
    private int pendientes;

    @SerializedName("tareas")
    private int tareas;

    @SerializedName("examenes")
    private int examenes;

    @SerializedName("exposiciones")
    private int exposiciones;

    @SerializedName("prioridad_alta")
    private int prioridadAlta;

    @SerializedName("prioridad_media")
    private int prioridadMedia;

    @SerializedName("prioridad_baja")
    private int prioridadBaja;

    @SerializedName("racha_actual")
    private int rachaActual;

    public int getTotalActividades() { return totalActividades; }
    public void setTotalActividades(int totalActividades) { this.totalActividades = totalActividades; }

    public int getCompletadas() { return completadas; }
    public void setCompletadas(int completadas) { this.completadas = completadas; }

    public int getPendientes() { return pendientes; }
    public void setPendientes(int pendientes) { this.pendientes = pendientes; }

    public int getTareas() { return tareas; }
    public void setTareas(int tareas) { this.tareas = tareas; }

    public int getExamenes() { return examenes; }
    public void setExamenes(int examenes) { this.examenes = examenes; }

    public int getExposiciones() { return exposiciones; }
    public void setExposiciones(int exposiciones) { this.exposiciones = exposiciones; }

    public int getPrioridadAlta() { return prioridadAlta; }
    public void setPrioridadAlta(int prioridadAlta) { this.prioridadAlta = prioridadAlta; }

    public int getPrioridadMedia() { return prioridadMedia; }
    public void setPrioridadMedia(int prioridadMedia) { this.prioridadMedia = prioridadMedia; }

    public int getPrioridadBaja() { return prioridadBaja; }
    public void setPrioridadBaja(int prioridadBaja) { this.prioridadBaja = prioridadBaja; }

    public int getRachaActual() { return rachaActual; }
    public void setRachaActual(int rachaActual) { this.rachaActual = rachaActual; }
}
