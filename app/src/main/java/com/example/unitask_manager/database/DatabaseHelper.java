package com.example.unitask_manager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.unitask_manager.models.Actividad;
import com.example.unitask_manager.models.Curso;
import com.example.unitask_manager.models.Usuario;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "unitask.db";
    private static final int DATABASE_VERSION = 2;

    // Tabla Usuarios
    private static final String TABLE_USUARIOS = "usuarios";
    private static final String COL_USUARIO_ID = "id";
    private static final String COL_USUARIO_NOMBRE = "nombre";
    private static final String COL_USUARIO_EMAIL = "email";
    private static final String COL_USUARIO_CONTRASENA = "contrasena";

    // Tabla Cursos
    private static final String TABLE_CURSOS = "cursos";
    private static final String COL_CURSO_ID = "id";
    private static final String COL_CURSO_NOMBRE = "nombre";
    private static final String COL_CURSO_PROFESOR = "profesor";
    private static final String COL_CURSO_COLOR = "color";
    private static final String COL_CURSO_HORARIO = "horario";
    private static final String COL_CURSO_DESCRIPCION = "descripcion";

    // Tabla Actividades
    private static final String TABLE_ACTIVIDADES = "actividades";
    private static final String COL_ACTIVIDAD_ID = "id";
    private static final String COL_ACTIVIDAD_ID_CURSO = "id_curso";
    private static final String COL_ACTIVIDAD_TITULO = "titulo";
    private static final String COL_ACTIVIDAD_TIPO = "tipo";
    private static final String COL_ACTIVIDAD_FECHA = "fecha";
    private static final String COL_ACTIVIDAD_HORA = "hora";
    private static final String COL_ACTIVIDAD_PRIORIDAD = "prioridad";
    private static final String COL_ACTIVIDAD_DESCRIPCION = "descripcion";
    private static final String COL_ACTIVIDAD_COMPLETADA = "completada";

    private static final String CREATE_TABLE_USUARIOS =
            "CREATE TABLE " + TABLE_USUARIOS + "("
                    + COL_USUARIO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_USUARIO_NOMBRE + " TEXT NOT NULL, "
                    + COL_USUARIO_EMAIL + " TEXT UNIQUE NOT NULL, "
                    + COL_USUARIO_CONTRASENA + " TEXT NOT NULL"
                    + ")";

    private static final String CREATE_TABLE_CURSOS =
            "CREATE TABLE " + TABLE_CURSOS + "("
                    + COL_CURSO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_CURSO_NOMBRE + " TEXT NOT NULL, "
                    + COL_CURSO_PROFESOR + " TEXT, "
                    + COL_CURSO_COLOR + " TEXT DEFAULT '#7C3AED', "
                    + COL_CURSO_HORARIO + " TEXT, "
                    + COL_CURSO_DESCRIPCION + " TEXT"
                    + ")";

    private static final String CREATE_TABLE_ACTIVIDADES =
            "CREATE TABLE " + TABLE_ACTIVIDADES + "("
                    + COL_ACTIVIDAD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_ACTIVIDAD_ID_CURSO + " INTEGER, "
                    + COL_ACTIVIDAD_TITULO + " TEXT NOT NULL, "
                    + COL_ACTIVIDAD_TIPO + " TEXT, "
                    + COL_ACTIVIDAD_FECHA + " TEXT NOT NULL, "
                    + COL_ACTIVIDAD_HORA + " TEXT, "
                    + COL_ACTIVIDAD_PRIORIDAD + " INTEGER DEFAULT 0, "
                    + COL_ACTIVIDAD_DESCRIPCION + " TEXT, "
                    + COL_ACTIVIDAD_COMPLETADA + " INTEGER DEFAULT 0, "
                    + "FOREIGN KEY (" + COL_ACTIVIDAD_ID_CURSO + ") REFERENCES "
                    + TABLE_CURSOS + "(" + COL_CURSO_ID + ")"
                    + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USUARIOS);
        db.execSQL(CREATE_TABLE_CURSOS);
        db.execSQL(CREATE_TABLE_ACTIVIDADES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_CURSOS + " ADD COLUMN " + COL_CURSO_HORARIO + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_CURSOS + " ADD COLUMN " + COL_CURSO_DESCRIPCION + " TEXT");
        }
    }

    // ===================== USUARIO =====================

    public long insertarUsuario(Usuario usuario) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USUARIO_NOMBRE, usuario.getNombre());
        values.put(COL_USUARIO_EMAIL, usuario.getEmail());
        values.put(COL_USUARIO_CONTRASENA, usuario.getContrasena());
        return db.insert(TABLE_USUARIOS, null, values);
    }

    public Usuario validarLogin(String email, String contrasena) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USUARIOS, null,
                COL_USUARIO_EMAIL + "=? AND " + COL_USUARIO_CONTRASENA + "=?",
                new String[]{email, contrasena}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Usuario usuario = new Usuario(
                    cursor.getLong(cursor.getColumnIndexOrThrow(COL_USUARIO_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USUARIO_NOMBRE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USUARIO_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USUARIO_CONTRASENA))
            );
            cursor.close();
            return usuario;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    // ===================== CURSOS =====================

    public long insertarCurso(Curso curso) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_CURSO_NOMBRE, curso.getNombre());
        values.put(COL_CURSO_PROFESOR, curso.getProfesor());
        values.put(COL_CURSO_COLOR, curso.getColor());
        values.put(COL_CURSO_HORARIO, curso.getHorario());
        values.put(COL_CURSO_DESCRIPCION, curso.getDescripcion());
        return db.insert(TABLE_CURSOS, null, values);
    }

    public List<Curso> obtenerCursos() {
        List<Curso> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CURSOS, null, null, null, null, null, COL_CURSO_NOMBRE + " ASC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_CURSO_ID));
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow(COL_CURSO_NOMBRE));
                String profesor = cursor.getString(cursor.getColumnIndexOrThrow(COL_CURSO_PROFESOR));
                String color = cursor.getString(cursor.getColumnIndexOrThrow(COL_CURSO_COLOR));
                String horario = cursor.getString(cursor.getColumnIndexOrThrow(COL_CURSO_HORARIO));
                String descripcion = cursor.getString(cursor.getColumnIndexOrThrow(COL_CURSO_DESCRIPCION));
                int pendientes = contarPendientesPorCurso(id);
                Curso curso = new Curso(id, nombre, profesor, color, pendientes);
                curso.setHorario(horario);
                curso.setDescripcion(descripcion);
                lista.add(curso);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return lista;
    }

    public int contarPendientesPorCurso(long cursoId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_ACTIVIDADES
                        + " WHERE " + COL_ACTIVIDAD_ID_CURSO + "=? AND " + COL_ACTIVIDAD_COMPLETADA + "=?",
                new String[]{String.valueOf(cursoId), "0"});
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        return count;
    }

    public int contarCompletadasPorCurso(long cursoId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_ACTIVIDADES
                        + " WHERE " + COL_ACTIVIDAD_ID_CURSO + "=? AND " + COL_ACTIVIDAD_COMPLETADA + "=?",
                new String[]{String.valueOf(cursoId), "1"});
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        return count;
    }

    public int contarActividadesPorCurso(long cursoId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_ACTIVIDADES
                        + " WHERE " + COL_ACTIVIDAD_ID_CURSO + "=?",
                new String[]{String.valueOf(cursoId)});
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        return count;
    }

    public List<Actividad> obtenerActividadesPorCursoId(long cursoId) {
        List<Actividad> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ACTIVIDADES, null,
                COL_ACTIVIDAD_ID_CURSO + "=?", new String[]{String.valueOf(cursoId)},
                null, null, COL_ACTIVIDAD_FECHA + " ASC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                lista.add(crearActividadDesdeCursor(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return lista;
    }

    public void eliminarCurso(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACTIVIDADES, COL_ACTIVIDAD_ID_CURSO + "=?", new String[]{String.valueOf(id)});
        db.delete(TABLE_CURSOS, COL_CURSO_ID + "=?", new String[]{String.valueOf(id)});
    }

    public void actualizarCurso(Curso curso) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_CURSO_NOMBRE, curso.getNombre());
        values.put(COL_CURSO_PROFESOR, curso.getProfesor());
        values.put(COL_CURSO_COLOR, curso.getColor());
        values.put(COL_CURSO_HORARIO, curso.getHorario());
        values.put(COL_CURSO_DESCRIPCION, curso.getDescripcion());
        db.update(TABLE_CURSOS, values, COL_CURSO_ID + "=?", new String[]{String.valueOf(curso.getId())});
    }

    // ===================== ACTIVIDADES =====================

    public void limpiarTodo() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACTIVIDADES, null, null);
        db.delete(TABLE_CURSOS, null, null);
        db.delete(TABLE_USUARIOS, null, null);
    }

    public long insertarActividad(Actividad actividad) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ACTIVIDAD_ID_CURSO, actividad.getIdCurso());
        values.put(COL_ACTIVIDAD_TITULO, actividad.getTitulo());
        values.put(COL_ACTIVIDAD_TIPO, actividad.getTipo());
        values.put(COL_ACTIVIDAD_FECHA, actividad.getFecha());
        values.put(COL_ACTIVIDAD_HORA, actividad.getHora());
        values.put(COL_ACTIVIDAD_PRIORIDAD, actividad.getPrioridad());
        values.put(COL_ACTIVIDAD_DESCRIPCION, actividad.getDescripcion());
        values.put(COL_ACTIVIDAD_COMPLETADA, actividad.isCompletada() ? 1 : 0);
        return db.insert(TABLE_ACTIVIDADES, null, values);
    }

    private String actividadesValidas() {
        return "(" + COL_ACTIVIDAD_ID_CURSO + "=-1 OR " + COL_ACTIVIDAD_ID_CURSO
                + " IN (SELECT " + COL_CURSO_ID + " FROM " + TABLE_CURSOS + "))";
    }

    public List<Actividad> obtenerActividades() {
        return obtenerActividades(null, null);
    }

    public List<Actividad> obtenerActividades(String orderBy, String limit) {
        List<Actividad> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ACTIVIDADES + " WHERE " + actividadesValidas();
        if (orderBy != null) query += " ORDER BY " + orderBy;
        if (limit != null) query += " LIMIT " + limit;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                lista.add(crearActividadDesdeCursor(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return lista;
    }

    public int contarCompletadas() {
        return contarActividades(true);
    }

    public int contarPendientes() {
        return contarActividades(false);
    }

    public int contarCompletadasPorFecha(String fecha) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_ACTIVIDADES
                        + " WHERE " + COL_ACTIVIDAD_COMPLETADA + "=1"
                        + " AND " + COL_ACTIVIDAD_FECHA + "=?"
                        + " AND " + actividadesValidas(),
                new String[]{fecha});
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        return count;
    }

    private int contarActividades(boolean completadas) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_ACTIVIDADES + " WHERE " + COL_ACTIVIDAD_COMPLETADA + "=? AND " + actividadesValidas(),
                new String[]{completadas ? "1" : "0"});
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        return count;
    }

    public void marcarCompletada(long id, boolean completada) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ACTIVIDAD_COMPLETADA, completada ? 1 : 0);
        db.update(TABLE_ACTIVIDADES, values, COL_ACTIVIDAD_ID + "=?", new String[]{String.valueOf(id)});
    }

    public void eliminarActividad(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACTIVIDADES, COL_ACTIVIDAD_ID + "=?", new String[]{String.valueOf(id)});
    }

    public void actualizarActividad(Actividad actividad) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ACTIVIDAD_ID_CURSO, actividad.getIdCurso());
        values.put(COL_ACTIVIDAD_TITULO, actividad.getTitulo());
        values.put(COL_ACTIVIDAD_TIPO, actividad.getTipo());
        values.put(COL_ACTIVIDAD_FECHA, actividad.getFecha());
        values.put(COL_ACTIVIDAD_HORA, actividad.getHora());
        values.put(COL_ACTIVIDAD_PRIORIDAD, actividad.getPrioridad());
        values.put(COL_ACTIVIDAD_DESCRIPCION, actividad.getDescripcion());
        values.put(COL_ACTIVIDAD_COMPLETADA, actividad.isCompletada() ? 1 : 0);
        db.update(TABLE_ACTIVIDADES, values, COL_ACTIVIDAD_ID + "=?", new String[]{String.valueOf(actividad.getId())});
    }

    public List<Actividad> obtenerActividadesPorPrioridad(int prioridad, boolean soloNoCompletadas) {
        List<Actividad> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String where = COL_ACTIVIDAD_PRIORIDAD + "=? AND " + actividadesValidas();
        String[] whereArgs;
        if (soloNoCompletadas) {
            where += " AND " + COL_ACTIVIDAD_COMPLETADA + "=?";
            whereArgs = new String[]{String.valueOf(prioridad), "0"};
        } else {
            whereArgs = new String[]{String.valueOf(prioridad)};
        }
        Cursor cursor = db.query(TABLE_ACTIVIDADES, null,
                where, whereArgs, null, null,
                COL_ACTIVIDAD_FECHA + " ASC, " + COL_ACTIVIDAD_HORA + " ASC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                lista.add(crearActividadDesdeCursor(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return lista;
    }

    public List<Actividad> obtenerActividadesPorFecha(String fecha) {
        List<Actividad> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ACTIVIDADES, null,
                COL_ACTIVIDAD_FECHA + "=? AND " + actividadesValidas(), new String[]{fecha},
                null, null, COL_ACTIVIDAD_HORA + " ASC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                lista.add(crearActividadDesdeCursor(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return lista;
    }

    public List<Actividad> obtenerActividadesEntreFechas(String fechaInicio, String fechaFin) {
        List<Actividad> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_ACTIVIDADES + " WHERE " + COL_ACTIVIDAD_FECHA + " BETWEEN ? AND ? ORDER BY " + COL_ACTIVIDAD_FECHA + " ASC",
                new String[]{fechaInicio, fechaFin});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                lista.add(crearActividadDesdeCursor(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return lista;
    }

    private Actividad crearActividadDesdeCursor(Cursor cursor) {
        Actividad actividad = new Actividad();
        actividad.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_ACTIVIDAD_ID)));
        actividad.setIdCurso(cursor.getLong(cursor.getColumnIndexOrThrow(COL_ACTIVIDAD_ID_CURSO)));
        actividad.setTitulo(cursor.getString(cursor.getColumnIndexOrThrow(COL_ACTIVIDAD_TITULO)));
        actividad.setTipo(cursor.getString(cursor.getColumnIndexOrThrow(COL_ACTIVIDAD_TIPO)));
        actividad.setFecha(cursor.getString(cursor.getColumnIndexOrThrow(COL_ACTIVIDAD_FECHA)));
        actividad.setHora(cursor.getString(cursor.getColumnIndexOrThrow(COL_ACTIVIDAD_HORA)));
        actividad.setPrioridad(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ACTIVIDAD_PRIORIDAD)));
        actividad.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow(COL_ACTIVIDAD_DESCRIPCION)));
        actividad.setCompletada(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ACTIVIDAD_COMPLETADA)) == 1);
        return actividad;
    }
}
