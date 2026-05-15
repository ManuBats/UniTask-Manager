package com.example.unitask_manager.models;

public class Actividad {

    public static final int PRIORIDAD_ALTA = 2;
    public static final int PRIORIDAD_MEDIA = 1;
    public static final int PRIORIDAD_BAJA = 0;

    public static final String TIPO_TAREA = "Tarea";
    public static final String TIPO_EXAMEN = "Examen";
    public static final String TIPO_EXPOSICION = "Exposición";

    private long id;
    private long idCurso;
    private String titulo;
    private String tipo;
    private String fecha;
    private String hora;
    private int prioridad;
    private String descripcion;
    private boolean completada;

    public Actividad() {}

    public Actividad(String titulo, String tipo, String fecha, String hora, int prioridad, String descripcion, long idCurso) {
        this.titulo = titulo;
        this.tipo = tipo;
        this.fecha = fecha;
        this.hora = hora;
        this.prioridad = prioridad;
        this.descripcion = descripcion;
        this.idCurso = idCurso;
        this.completada = false;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

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

    public boolean isCompletada() { return completada; }
    public void setCompletada(boolean completada) { this.completada = completada; }

    public int getIconoResId() {
        switch (tipo != null ? tipo.toLowerCase() : "") {
            case "examen":
                return com.example.unitask_manager.R.drawable.ic_examen;
            case "exposición":
            case "exposicion":
                return com.example.unitask_manager.R.drawable.ic_exposicion;
            default:
                return com.example.unitask_manager.R.drawable.ic_tarea;
        }
    }

    public int getColorPrioridad() {
        switch (prioridad) {
            case PRIORIDAD_ALTA:
                return com.example.unitask_manager.R.color.priority_high;
            case PRIORIDAD_MEDIA:
                return com.example.unitask_manager.R.color.priority_medium;
            case PRIORIDAD_BAJA:
                return com.example.unitask_manager.R.color.priority_low;
            default:
                return com.example.unitask_manager.R.color.text_secondary;
        }
    }

    public int getColorFondoPrioridad() {
        switch (prioridad) {
            case PRIORIDAD_ALTA:
                return com.example.unitask_manager.R.color.priority_high_bg;
            case PRIORIDAD_MEDIA:
                return com.example.unitask_manager.R.color.priority_medium_bg;
            case PRIORIDAD_BAJA:
                return com.example.unitask_manager.R.color.priority_low_bg;
            default:
                return com.example.unitask_manager.R.color.card_white;
        }
    }
}
