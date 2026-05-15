package com.example.unitask_manager.models;

public class Curso {

    private long id;
    private String nombre;
    private String profesor;
    private String color;

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

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getProfesor() { return profesor; }
    public void setProfesor(String profesor) { this.profesor = profesor; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}
