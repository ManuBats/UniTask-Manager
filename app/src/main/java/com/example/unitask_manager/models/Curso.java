package com.example.unitask_manager.models;

public class Curso {

    private long id;
    private String nombre;
    private String profesor;
    private String color;
    private int pendientes;
    private String horario;
    private int progreso;
    private String descripcion;
    private int iconResId;

    public Curso() {}

    public Curso(String nombre, String profesor, String color) {
        this.nombre = nombre;
        this.profesor = profesor;
        this.color = color;
    }

    public Curso(long id, String nombre, String profesor, String color) {
        this.id = id;
        this.nombre = nombre;
        this.profesor = profesor;
        this.color = color;
    }

    public Curso(long id, String nombre, String profesor, String color, int pendientes) {
        this.id = id;
        this.nombre = nombre;
        this.profesor = profesor;
        this.color = color;
        this.pendientes = pendientes;
    }

    public Curso(long id, String nombre, String profesor, String color, int pendientes, String horario, int progreso, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.profesor = profesor;
        this.color = color;
        this.pendientes = pendientes;
        this.horario = horario;
        this.progreso = progreso;
        this.descripcion = descripcion;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getProfesor() { return profesor; }
    public void setProfesor(String profesor) { this.profesor = profesor; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public int getPendientes() { return pendientes; }
    public void setPendientes(int pendientes) { this.pendientes = pendientes; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }

    public int getProgreso() { return progreso; }
    public void setProgreso(int progreso) { this.progreso = progreso; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getIconResId() { return iconResId; }
    public void setIconResId(int iconResId) { this.iconResId = iconResId; }
}
